package coldsrc.cerve.service;

import coldsrc.cerve.permission.PermissionKey;

public final class ServicePermissions {

    /*
        These permissions apply across the whole service, so for
        all provided resources.
     */

    public static PermissionKey all() {
        return PermissionKey.all(QUERY_RESOURCES, READ_PACKAGES, WRITE_PACKAGES);
    }

    /**
     * Grants a user permission to query data about resources.
     */
    public static final PermissionKey QUERY_RESOURCES = PermissionKey.single("query_resources");

    /**
     * Grants a user permission to read and stream packages.
     */
    public static final PermissionKey READ_PACKAGES = PermissionKey.single("read_packages");

    /**
     * Grants a user permission to write packages.
     */
    public static final PermissionKey WRITE_PACKAGES = PermissionKey.single("write_packages");

}
