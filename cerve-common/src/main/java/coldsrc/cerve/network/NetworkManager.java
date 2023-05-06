package coldsrc.cerve.network;

import coldsrc.cerve.util.Throwables;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class NetworkManager {

    /**
     * All registered packet types by ID.
     */
    private final Map<Integer, PacketType<?>> packetTypesByHash = new HashMap<>();

    {
        registerAll(BaseProtocol.class);
    }

    /**
     * Registers the given packet type.
     *
     * @param type The type.
     */
    public void register(PacketType<?> type) {
        packetTypesByHash.put(type.getID(), type);
    }

    /**
     * Registers all statically defined packet types
     * in the given class.
     */
    public void registerAll(Class<?> spec) {
        try {
            for (Field field : spec.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) continue;
                if (!PacketType.class.isAssignableFrom(field.getType())) continue;

                field.setAccessible(true);
                PacketType<?> type = (PacketType<?>) field.get(null);

                register(type);
            }
        } catch (Exception e) {
            Throwables.sneakyThrow(e);
        }
    }

    /**
     * Get a packet type by hash/ID if present.
     *
     * @param id The hash.
     * @return The packet type or null if absent.
     */
    public PacketType<?> getPacketTypeByHash(int id) {
        return packetTypesByHash.get(id);
    }

}
