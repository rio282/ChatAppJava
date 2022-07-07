package nl.hva.caj.app.client;

import nl.hva.caj.app.Connection;
import nl.hva.caj.utils.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class is the user client
 */
public class Client extends Connection {

    private Socket socket;
    private String nickname;

    private BufferedReader inputStream;
    private BufferedWriter outputStream;

    public Client(String hostAddress, int port) {
        super(hostAddress, port);
    }

    @Override
    protected void initialize() throws IOException {
        socket = new Socket(hostAddress, port);
        inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Logger.logf("Connected to server: %s", socket.getRemoteSocketAddress());
    }

    @Override
    public void start() throws IOException {
        listen();

        nickname = "BIG BOI";

        // tell the server our creds
        outputStream.write(nickname);
        outputStream.newLine();
        outputStream.flush();

        // TODO: move over to GUI
        // input
        Scanner scanner = new Scanner(System.in);
        while (socket != null && socket.isConnected()) {
            System.out.print("You: ");
            String message = scanner.nextLine();

            outputStream.write(message);
            outputStream.newLine();
            outputStream.flush();

            System.out.println();
        }
    }

    private void listen() throws IOException {
        new Thread(() -> {
            String incoming;

            while (socket != null && socket.isConnected()) {
                try {
                    incoming = inputStream.readLine();
                    System.out.println("SERVER: " + incoming);
                } catch (IOException e) {
                    Logger.errf("Error when reading incoming data: ", e.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void cleanup() throws IOException {
        // rem io
        inputStream.close();
        inputStream = null;
        outputStream.close();
        outputStream = null;

        // rem sock
        socket.close();
        socket = null;
    }

    private void sendText(String message) throws IOException {
        outputStream.write(message);
        outputStream.newLine();
        outputStream.flush();
    }
}
