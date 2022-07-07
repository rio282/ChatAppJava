package nl.hva.caj.app.server;

import nl.hva.caj.utils.Logger;

import java.io.*;
import java.net.Socket;

/**
 * Handles a client used by our Server class.
 */
public class ClientHandler implements Runnable {

    private Server server;
    private int id;
    private String nickname;
    private Socket socket;

    private BufferedReader inputStream;
    private BufferedWriter outputStream;

    public ClientHandler(Server server, int id, Socket socket) throws IOException {
        this.server = server;
        this.id = id;
        this.nickname = String.format("USER_%.0f", (Math.random() * (Integer.MAX_VALUE >> 2))); // temp nickname
        this.socket = socket;

        inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public String toString() {
        return String.format("[CLIENT INFO] %d:%s -- %s:%d", id, nickname, socket.getInetAddress(), socket.getPort());
    }

    @Override
    public void run() {
        String clientMessage;
        while (socket != null && socket.isConnected()) {
            try {
                clientMessage = inputStream.readLine();
                server.broadcastText(this, clientMessage);
            } catch (IOException e) {
                Logger.errf("Client with id %d crashed. Cleaning up client.", id);
                cleanup();
                break;
            }
        }
    }

    public void cleanup() {
        try {
            // rem server
            server.broadcastText(this, String.format("Client %s disconnected.", nickname));
            server.getClients().remove(this);
            server = null;

            // rem sock
            socket.close();
            socket = null;

            // rem io
            inputStream.close();
            inputStream = null;
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Logger.errf("Error thrown by client with id %d: %s", id, e.getMessage());
        }

        Logger.logf("Cleaned up client %d", id);
    }

    public BufferedReader getInputStream() {
        return inputStream;
    }

    public BufferedWriter getOutputStream() {
        return outputStream;
    }
}
