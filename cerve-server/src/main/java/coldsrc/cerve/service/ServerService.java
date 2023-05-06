package coldsrc.cerve.service;

/**
 * Represents a service on the server providing resources to clients.
 */
public class ServerService {

    /**
     * The name of the service.
     */
    private final String name;

    public ServerService(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
