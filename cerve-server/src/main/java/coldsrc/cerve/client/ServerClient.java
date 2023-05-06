package coldsrc.cerve.client;

import coldsrc.cerve.CerveServer;
import coldsrc.cerve.security.EncryptionProfile;
import coldsrc.cerve.user.ServerUser;
import coldsrc.cerve.util.Throwables;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a client connected to the Cerve server.
 */
public class ServerClient {

    /**
     * The server.
     */
    private final CerveServer server;

    /**
     * The client socket.
     */
    private final Socket socket;

    /**
     * The current encryption profile in use.
     */
    private EncryptionProfile encryptionProfile;

    /**
     * If encryption should be enabled.
     */
    private AtomicBoolean enableEncryption;

    /**
     * The user they are logged in to.
     */
    private ServerUser loggedInUser;

    /** The network handler thread. */
    private Thread networkHandlerThread;
    private AtomicBoolean networkHandlerRunning;

    public ServerClient(CerveServer server, Socket socket) {
        this.server = server;
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
     * Start the network handler.
     *
     * @return If it was successfully started.
     */
    public boolean startNetworkHandler() {
        if (socket.isClosed())
            return false;

        networkHandlerThread = new Thread(() -> {
            final ObjectInputStream inputStream = getInputStream();
            while (networkHandlerRunning.get() && !socket.isClosed()) {
                try {
                    // get packet id
                    int packetId = inputStream.readInt();
                } catch (Exception e) {
                    server.clientLogger.warn("Error while reading packet for client(" + socket.getRemoteSocketAddress() + ")");
                }
            }

            server.clientLogger.info("Server client(" + socket.getRemoteSocketAddress() + ") network handler disconnected");
            onNetworkHandlerDisconnect();
        }, "CerveClientNetworkHandler");

        networkHandlerRunning.set(true);
        networkHandlerThread.start();
        return true;
    }

    // called once the network handler disconnects
    protected void onNetworkHandlerDisconnect() {
        // remove client from server
        synchronized (server.getClients()) {
            server.removeClient(this);
        }
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

    public EncryptionProfile getEncryptionProfile() {
        return encryptionProfile;
    }

    /**
     * Get the user they logged in to.
     *
     * @return The user.
     */
    public ServerUser getLoggedInUser() {
        return loggedInUser;
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
                    .setEncryptCheck(() -> enableEncryption.get())
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
                    .setEncryptCheck(() -> enableEncryption.get())
                    .toObjectStream();
        } catch (Exception e) {
            Throwables.sneakyThrow(e);
            return null;
        }
    }

}
