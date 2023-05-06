package coldsrc.cerve.network;

public class NoSuchPacketException extends RuntimeException {

    private final int typeHash;

    public NoSuchPacketException(int typeHash) {
        this.typeHash = typeHash;
    }

    public int getTypeHash() {
        return typeHash;
    }
    
}
