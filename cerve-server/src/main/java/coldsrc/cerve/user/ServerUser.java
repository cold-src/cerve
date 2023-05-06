package coldsrc.cerve.user;

import coldsrc.cerve.CerveServer;

/**
 * Represents a user on a Cerve server.
 */
public class ServerUser implements CerveUser {

    /**
     * The server.
     */
    private final CerveServer server;

    /**
     * The name of the user.
     */
    private final String name;

    public ServerUser(CerveServer server, String name) {
        this.server = server;
        this.name = name;
    }

    @Override
    public String getUserName() {
        return name;
    }

}
