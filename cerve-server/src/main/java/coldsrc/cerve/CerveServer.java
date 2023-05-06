package coldsrc.cerve;

import coldsrc.cerve.client.ServerClient;
import coldsrc.cerve.logging.LoggerProvider;
import coldsrc.cerve.logging.LoggerProxy;
import coldsrc.cerve.network.NetworkManager;
import coldsrc.cerve.permission.PermissionNamespace;
import coldsrc.cerve.security.AsymmetricEncryptionProfile;
import coldsrc.cerve.security.EncryptionProfile;
import coldsrc.cerve.security.StandardEncryption;
import coldsrc.cerve.service.ServerService;
import coldsrc.cerve.user.ServerUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    /**
     * All registered users.
     */
    final Map<String, ServerUser> userMap = new HashMap<>();

    /**
     * All connected clients.
     */
    final List<ServerClient> clients = new ArrayList<>();

    /**
     * The network manager.
     */
    final NetworkManager networkManager = new NetworkManager();

    /**
     * The servers encryption profile to initiate safe communication.
     */
    final AsymmetricEncryptionProfile serverEncryption = StandardEncryption.newAsymmetricEncryptionProfile();

    public CerveServer(int port, LoggerProvider loggerProvider) {
        this.port = port;
        this.loggerProvider = loggerProvider;

        // loggers //
        this.clientLogger = loggerProvider.getLogger("ServerClient");

        // generate public and private key
        // for the handshakes
        serverEncryption.generateKeys();
    }

    /* Loggers */
    public final LoggerProxy clientLogger;

    /**
     * Get the network port for the server to accept on.
     *
     * @return The network port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Get the global network manager.
     *
     * @return The network manager.
     */
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    /**
     * Get the server encryption profile.
     *
     * @return The servers encryption profile to initiate safe communication.
     */
    public AsymmetricEncryptionProfile getServerEncryption() {
        return serverEncryption;
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
        ServerService service = new ServerService(this, name);
        serviceMap.put(name, service);
        return service;
    }

    public ServerService getOrCreateService(String name) {
        ServerService service = getService(name);
        if (service != null)
            return service;
        return createService(name);
    }

    /**
     * Get a user by name if present.
     *
     * @param name The user name.
     * @return The user or null if absent.
     */
    public ServerUser getUser(String name) {
        return userMap.get(name);
    }

    /**
     * Check if a user is registered.
     *
     * @param name The user name.
     * @return If a user by the given name exists.
     */
    public boolean hasUser(String name) {
        return userMap.containsKey(name);
    }

    /**
     * Create a new user with the given name.
     *
     * @param name The user name.
     * @return The new user instance.
     */
    public ServerUser createUser(String name) {
        ServerUser user = new ServerUser(this, name);
        userMap.put(name, user);
        return user;
    }

    /**
     * Get the list of connected clients.
     *
     * @return The clients.
     */
    public List<ServerClient> getClients() {
        return clients;
    }

    public void removeClient(ServerClient client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

}
