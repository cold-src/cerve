package coldsrc.cerve.network;

/**
 * Base/general-purpose protocol specification.
 */
public class BaseProtocol {

    /*
        Encryption Handshake
     */

    /**
     * Clientbound: Sends the servers public key, initiating the encryption handshake.
     */
    public static final PacketType<byte[]> CLIENTBOUND_PUBLIC_KEY = PacketType.byteArrayValue("clientbound_public_key");

    /**
     * Serverbound: Signals to the server that the client denies the request to encrypt.
     */
    public static final PacketType<Void> SERVERBOUND_DENY_ENCRYPTION = PacketType.noValue("serverbound_deny_encryption");

    /**
     * Serverbound: Sends the secret key generated by the client to the server.
     * This key is encrypted with the given public key of the server.
     */
    public static final PacketType<byte[]> SERVERBOUND_SECRET_KEY = PacketType.byteArrayValue("serverbound_secret_key");

    /**
     * Clientbound: Signals to the client that the handshake was successful.
     */
    public static final PacketType<Void> CLIENTBOUND_HANDSHAKE_OK = PacketType.noValue("clientbound_handshake_ok");

    /*
        Disconnect
     */

    /**
     * A disconnect reason for communication.
     */
    public static record Disconnect(String reason) { }

    public static final PacketType<Disconnect> DISCONNECT = new PacketType<>(
            "disconnect",
            stream -> new Disconnect(stream.readUTF()),
            (stream, value) -> stream.writeUTF(value.reason())
    );

}
