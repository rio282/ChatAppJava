package nl.hva.caj.app.server;

import nl.hva.caj.Main;
import nl.hva.caj.app.Connection;
import nl.hva.caj.utils.Logger;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Connection {

    private static final int MAX_CLIENTS = 5;
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients;

    public Server(int port) {
        super(Connection.LOCAL_HOST_ADDRESS, port);
    }

    @Override
    protected void initialize() throws IOException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (BindException be) {
            Logger.err("Server already in use.");
            Main.exit();
        }
        clients = new ArrayList<>();
    }

    private void onClientConnect(ClientHandler client) throws IOException {
        String clientNickname = client.getInputStream().readLine();
        client.setNickname(clientNickname);

        Logger.logf("Client %s connected from %s.", client.getNickname(), client.getSocket().getLocalSocketAddress());
        clients.add(client);

        // start client thread
        Thread clientThread = new Thread(client);
        clientThread.start();
    }

    @Override
    public void start() {
        Logger.logf("Server listening on %s:%d", hostAddress, port);
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                int clientId = clients.size() + 1;
                if (clients.size() >= MAX_CLIENTS) { // check if we have too many clients
                    Logger.errf("Client connection aborted. Max amount of clients reached (%d).", MAX_CLIENTS);
                    continue;
                }

                // init client
                ClientHandler client = null;
                try {
                    client = new ClientHandler(this, clientId, clientSocket);
                    onClientConnect(client);
                } catch (IOException ioe) {
                    Logger.errf("Error when instantiating client with id %d: %s", clientId, ioe.getMessage());
                }
            } catch (IOException e) {
                Logger.err(e.getMessage());
            }
        }
    }

    public ArrayList<ClientHandler> getClients() {
        return clients;
    }

    @Override
    public void cleanup() throws IOException {
        // disconnect & cleanup clients
        clients.forEach(ClientHandler::cleanup);
        if (serverSocket != null)
            serverSocket.close();
        Logger.log("Cleaned up server.");
    }

    public void broadcastText(ClientHandler clientHandler, String clientMessage) {
        clients.forEach(client -> {
            try {
                // format out
                String out = String.format("%s: %s", clientHandler.getNickname(), clientMessage);
                Logger.log("[BROADCAST] " + out);

                // broadcast msg
                client.getOutputStream().write(out);
                client.getOutputStream().newLine();
                client.getOutputStream().flush();
            } catch (IOException e) {
                Logger.errf("Error when sending message '%s': %s", clientMessage, e.getMessage());
                client.cleanup();
            }
        });
    }
}
