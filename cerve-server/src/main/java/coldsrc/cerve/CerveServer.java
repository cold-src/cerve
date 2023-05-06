package coldsrc.cerve;

import coldsrc.cerve.logging.LoggerProvider;
import coldsrc.cerve.logging.LoggerProxy;
import coldsrc.cerve.permission.PermissionNamespace;
import coldsrc.cerve.service.ServerService;

import java.util.HashMap;
import java.util.Map;

/**
 * Main server class.
 */
public class CerveServer {

    /**
     * The network server port.
     */
    final int port;

    /**
     * The permission namespace for general server permissions.
     */
    final PermissionNamespace serverPermissions = new PermissionNamespace();

    /**
     * The logger provider to use.
     */
    final LoggerProvider loggerProvider;

    /**
     * All registered services.
     */
    final Map<String, ServerService> serviceMap = new HashMap<>();

    CerveServer(int port, LoggerProvider loggerProvider) {
        this.port = port;
        this.loggerProvider = loggerProvider;
    }

    /**
     * Get the network port for the server to accept on.
     *
     * @return The network port.
     */
    public int getPort() {
        return port;
    }

    /**
     * The permission namespace for general server permissions.
     *
     * @return The namespace.
     */
    public PermissionNamespace serverPermissions() {
        return serverPermissions;
    }

    /**
     * Get the logger provider for this server.
     *
     * @return The logger provider instance.
     */
    public LoggerProvider getLoggerProvider() {
        return loggerProvider;
    }

    public LoggerProxy getLogger(String name) {
        return loggerProvider.getLogger(name);
    }

    /**
     * Get the service associated with the given name
     * if present.
     *
     * @param name The name of the service.
     * @return The service or null if absent.
     */
    public ServerService getService(String name) {
        return serviceMap.get(name);
    }

    /**
     * Check if a service with the given name
     * is registered.
     *
     * @param name The name to check.
     * @return If a service with that name exists.
     */
    public boolean hasService(String name) {
        return serviceMap.containsKey(name);
    }

    /**
     * Creates and registers a new service with the given
     * name, returning the new instance for management.
     *
     * @param name The name of the service.
     * @return The new instance.
     */
    public ServerService createService(String name) {
        ServerService service = new ServerService(name);
        serviceMap.put(name, service);
        return service;
    }

    public ServerService getOrCreateService(String name) {
        ServerService service = getService(name);
        if (service != null)
            return service;
        return createService(name);
    }

}
