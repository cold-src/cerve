package coldsrc.cerve.network;

import java.io.IOException;
import java.io.ObjectInputStream;

@FunctionalInterface
public interface ValueDeserializer<T> {

    T deserialize(ObjectInputStream stream) throws Exception;

}
