package coldsrc.cerve.network;

import coldsrc.cerve.security.EncryptionProfile;
import coldsrc.cerve.util.Throwables;
import coldsrc.coldlib.util.functional.Callback;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class NetworkHandler {

    public enum DisconnectReason {

        /**
         * The server issued the network handler to stop.
         */
        ISSUED,

        /**
         * The client gracefully disconnected.
         */
        DISCONNECT,

        /**
         * The socket was abruptly closed.
         */
        SOCKET_CLOSED,

        /**
         * A fatal error occurred.
         */
        FATAL_ERROR

    }

    /**
     * The network manager instance.
     */
    private final NetworkManager networkManager;

    /**
     * The connected socket for data streaming.
     */
    private final Socket socket;

    /**
     * The current encryption profile in use.
     */
    private EncryptionProfile encryptionProfile;

    /**
     * If encryption should be enabled.
     */
    private final AtomicBoolean enableEncryption = new AtomicBoolean();

    /** The network handler thread. */
    private Thread networkHandlerThread;
    private AtomicBoolean networkHandlerRunning;

    /* General Network Events */
    private final Callback<Void> onNetworkHandlerStart = Callback.multi();
    private final Callback<DisconnectReason> onNetworkHandlerDisconnect = Callback.multi();
    private final Callback<Throwable> onPacketReadError = Callback.multi();
    public Callback<Void> onNetworkHandlerStart() { return onNetworkHandlerStart; }
    public Callback<DisconnectReason> onNetworkHandlerDisconnect() { return onNetworkHandlerDisconnect; }
    public Callback<Throwable> onPacketReadError() { return onPacketReadError; }

    /* Packet Events */
    private final Callback<ReceivedPacket<?>> onAnyPacketReceived = Callback.multi();
    public Callback<ReceivedPacket<?>> onAnyPacketReceived() { return onAnyPacketReceived; }
    private final Map<PacketType, Callback<ReceivedPacket>> packetReceivedEvents =
            new HashMap<>();

    public <T> Callback<ReceivedPacket<T>> onPacketReceived(PacketType<T> type) {
        return (Callback<ReceivedPacket<T>>) (Object) packetReceivedEvents.computeIfAbsent(type, __ -> Callback.multi());
    }

    public NetworkHandler(NetworkManager networkManager, Socket socket) {
        this.networkManager = networkManager;
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    /**
     * Set the current encryption state.
     *
     * @param b The value.
     */
    public void enableEncryption(boolean b) {
        enableEncryption.set(b);
    }

    /**
     * Get if the connection to this client should
     * currently be encrypted.
     */
    public boolean isCurrentlyEncrypted() {
        return encryptionProfile != EncryptionProfile.NOT_ENCRYPTING && enableEncryption.get();
    }

    /**
     * Check if this client has an encryption profile established.
     */
    public boolean canEncrypt() {
        return encryptionProfile != null;
    }

    /**
     * Get the input stream to the socket, automatically
     * decrypted if an encryption profile is present.
     *
     * Returns null if the socket is closed.
     *
     * @return The input stream.
     */
    public ObjectInputStream getInputStream() {
        if (socket.isClosed())
            return null;

        try {
            return encryptionProfile.decryptingInputStream(socket.getInputStream())
                    .setEncryptCheck(enableEncryption::get)
                    .toObjectStream();
        } catch (Exception e) {
            Throwables.sneakyThrow(e);
            return null;
        }
    }

    /**
     * Get the output stream to the socket, automatically
     * encrypted if an encryption profile is present.
     *
     * Returns null if the socket is closed.
     *
     * @return The output stream.
     */
    public ObjectOutputStream getOutputStream() {
        if (socket.isClosed())
            return null;

        try {
            return encryptionProfile.encryptingOutputStream(socket.getOutputStream())
                    .setEncryptCheck(enableEncryption::get)
                    .toObjectStream();
        } catch (Exception e) {
            Throwables.sneakyThrow(e);
            return null;
        }
    }

    /**
     * Start the network handler.
     *
     * @return If it was successfully started.
     */
    public boolean startNetworkHandler() {
        if (socket.isClosed())
            return false;

        networkHandlerThread = new Thread(() -> {
            final ObjectInputStream inputStream = getInputStream();
            DisconnectReason disconnectReason;

            try {
                while (true) {
                    if (!networkHandlerRunning.get()) {
                        disconnectReason = DisconnectReason.ISSUED;
                        break;
                    }

                    if (socket.isClosed()) {
                        disconnectReason = DisconnectReason.SOCKET_CLOSED;
                        break;
                    }

                    try {
                        // get packet id
                        int packetId = inputStream.readInt();
                        PacketType<?> packetType = networkManager.getPacketTypeByHash(packetId);

                        if (packetType == null) {
                            throw new NoSuchPacketException(packetId);
                        }

                        // deserialize value
                        Object value = packetType.deserialize(inputStream);
                        ReceivedPacket packet = new ReceivedPacket(this, packetType, value);

                        // call packet event
                        onAnyPacketReceived.call(packet);
                        Callback callback = packetReceivedEvents.get(packetType);
                        if (callback != null) {
                            callback.call(packet);
                        }
                    } catch (Exception e) {
                        onPacketReadError.call(e);
                    }
                }
            } catch (Exception e) {
                disconnectReason = DisconnectReason.FATAL_ERROR;
                e.printStackTrace();
            }

            onNetworkHandlerDisconnect.call(disconnectReason);
        }, "CerveClientNetworkHandler");

        networkHandlerRunning.set(true);
        networkHandlerThread.start();
        onNetworkHandlerStart.call();
        return true;
    }

}
