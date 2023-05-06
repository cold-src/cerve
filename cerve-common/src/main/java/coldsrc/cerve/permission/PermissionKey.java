package coldsrc.cerve.permission;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public interface PermissionKey {

    static PermissionKey single(String name) {
        return new PermissionKey() {
            @Override
            public Set<PermissionKey> unwrap() {
                return Set.of(this);
            }

            @Override
            public String toString() {
                return "permission(" + name + ")";
            }

            @Override
            public int hashCode() {
                return name.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == null) return false;
                if (!(obj instanceof PermissionKey key)) return false;
                return obj.hashCode() == this.hashCode();
            }
        };
    }

    static PermissionKey all(final PermissionKey... keys) {
        return new PermissionKey() {
            @Override
            public Set<PermissionKey> unwrap() {
                Set<PermissionKey> set = new HashSet<>();
                for (PermissionKey key : keys)
                    set.addAll(key.unwrap());
                return set;
            }

            @Override
            public String toString() {
                StringJoiner joiner = new StringJoiner(", ");
                for (PermissionKey key : keys)
                    joiner.add(key.toString());
                return "permissions(" + joiner + ")";
            }
        };
    }

    /**
     * Flatten this key to a set of all affected permissions.
     *
     * @return The set.
     */
    Set<PermissionKey> unwrap();

    /**
     * Check whether this key covers the given permission.
     *
     * @param permission The permission.
     * @return If it covers.
     */
    default boolean contains(PermissionKey permission) {
        return unwrap().contains(permission);
    }

}
