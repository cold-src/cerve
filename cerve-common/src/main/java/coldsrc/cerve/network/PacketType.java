package coldsrc.cerve.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PacketType<T> {

    @SuppressWarnings("unchecked")
    public static <T> PacketType<T> objectValue(String name) {
        return new PacketType<>(name,
                stream -> (T) stream.readObject(),
                ObjectOutputStream::writeObject);
    }

    public static PacketType<byte[]> byteArrayValue(String name) {
        return new PacketType<>(name,
                (stream -> {
                    byte[] b = new byte[stream.readInt()];
                    stream.read(b);
                    return b;
                }),
                ((stream, value) -> {
                    stream.writeInt(value.length);
                    stream.write(value);
                }));
    }

    public static PacketType<Void> noValue(String name) {
        return new PacketType<>(name, null, null);
    }

    ////////////////////////////////////

    /**
     * The name of the packet.
     */
    private final String name;

    /**
     * The type ID (name hashCode).
     */
    private final int id;

    /**
     * The data deserializer.
     */
    private final ValueDeserializer<T> deserializer;

    /**
     * The data serializer.
     */
    private final ValueSerializer<T> serializer;

    public PacketType(String name, ValueDeserializer<T> deserializer, ValueSerializer<T> serializer) {
        this.name = name;
        this.id = name.hashCode();
        this.deserializer = deserializer;
        this.serializer = serializer;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    public ValueDeserializer<T> getDeserializer() {
        return deserializer;
    }

    public ValueSerializer<T> getSerializer() {
        return serializer;
    }

    public T deserialize(ObjectInputStream stream) throws Exception {
        if (deserializer == null)
            return null;
        return deserializer.deserialize(stream);
    }

    public void serialize(ObjectOutputStream stream, T value) throws Exception {
        if (serializer != null)
            serializer.serialize(stream, value);
    }

}
