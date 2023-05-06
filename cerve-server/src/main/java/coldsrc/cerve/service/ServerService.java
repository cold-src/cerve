package coldsrc.cerve.service;

import coldsrc.cerve.CerveServer;
import coldsrc.cerve.permission.PermissionNamespace;

/**
 * Represents a service on the server providing resources to clients.
 */
public class ServerService {

    /**
     * The server.
     */
    private final CerveServer server;

    /**
     * The name of the service.
     */
    private final String name;

    /**
     * The permission namespace for all resource access.
     */
    final PermissionNamespace resourcePermissions = new PermissionNamespace();

    public ServerService(CerveServer server, String name) {
        this.server = server;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public PermissionNamespace resourcePermissions() {
        return resourcePermissions;
    }

}
