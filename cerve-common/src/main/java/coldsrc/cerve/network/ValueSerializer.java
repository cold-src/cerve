package coldsrc.cerve.network;

import java.io.IOException;
import java.io.ObjectOutputStream;

@FunctionalInterface
public interface ValueSerializer<T> {

    void serialize(ObjectOutputStream stream, T value) throws Exception;

}
