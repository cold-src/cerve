package coldsrc.cerve.network;

@SuppressWarnings("unchecked")
public record ReceivedPacket<T>(NetworkHandler receiver, PacketType<T> type, T value) {
    public <T2> T2 get() {
        return (T2) value;
    }
}
