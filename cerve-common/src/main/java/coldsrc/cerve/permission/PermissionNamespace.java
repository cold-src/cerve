package coldsrc.cerve.permission;

import java.util.HashMap;
import java.util.Map;

public class PermissionNamespace {

    /**
     * The set permissions for all users.
     */
    private final Map<String, Map<PermissionKey, Permit>> permissionMap = new HashMap<>();

    /**
     * Get all set permissions for the given user.
     *
     * @param user The user name.
     * @return The mutable permission map.
     */
    public Map<PermissionKey, Permit> getSetPermissions(String user) {
        return permissionMap.computeIfAbsent(user, __ -> new HashMap<>());
    }

    /**
     * Set all permissions by the given key to the given value
     * for the given user.
     *
     * @param user The user name.
     * @param key The key.
     * @param permit The value.
     */
    public void setPermission(String user, PermissionKey key, Permit permit) {
        var map = getSetPermissions(user);

        if (permit == Permit.UNSET) {
            for (PermissionKey k : key.unwrap()) {
                map.remove(k);
            }

            return;
        }

        for (PermissionKey k : key.unwrap()) {
            map.put(k, permit);
        }
    }

    public void setPermission(PermissionUser user, PermissionKey key, Permit permit) {
        setPermission(user.getUserName(), key, permit);
    }

    /**
     * Get the set value for the given permission for the given user.
     *
     * @param user The user name.
     * @param key The key.
     * @return The value.
     */
    public Permit getPermission(String user, PermissionKey key) {
        return getSetPermissions(user).getOrDefault(key, Permit.UNSET);
    }

    public Permit getPermission(PermissionUser user, PermissionKey key) {
        return getPermission(user.getUserName(), key);
    }

    /**
     * Check if the given user has the given permission.
     * Defaults to false when unset.
     *
     * @param user The user name.
     * @param key The key.
     * @return The boolean value.
     */
    public boolean hasPermission(String user, PermissionKey key) {
        return getPermission(user, key).asBoolean(false);
    }

    public boolean hasPermission(PermissionUser user, PermissionKey key) {
        return hasPermission(user.getUserName(), key);
    }

}
