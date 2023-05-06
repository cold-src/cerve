package coldsrc.cerve.client;

import coldsrc.cerve.CerveServer;
import coldsrc.cerve.network.BaseProtocol;
import coldsrc.cerve.network.NetworkHandler;
import coldsrc.cerve.network.NetworkManager;
import coldsrc.cerve.security.EncryptionProfile;
import coldsrc.cerve.user.ServerUser;
import coldsrc.cerve.util.Throwables;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

/**
 * Represents a client connected to the Cerve server.
 */
public class ServerClient extends NetworkHandler {

    /**
     * The server.
     */
    private final CerveServer server;

    /**
     * The user this client is currently logged into.
     */
    private volatile ServerUser loggedInUser;

    public ServerClient(CerveServer server, Socket socket) {
        super(server.getNetworkManager(), socket);
        this.server = server;
    }

    /**
     * Get the user they logged in to.
     *
     * @return The user.
     */
    public ServerUser getLoggedInUser() {

        return loggedInUser;
    }

}
