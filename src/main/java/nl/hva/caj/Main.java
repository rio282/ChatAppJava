package nl.hva.caj;

import nl.hva.caj.app.Connection;
import nl.hva.caj.app.client.Client;
import nl.hva.caj.app.server.ClientHandler;
import nl.hva.caj.app.server.Server;
import nl.hva.caj.utils.Logger;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Logger.log("Start program!");

        String[] options = {Client.class.getSimpleName(), Server.class.getSimpleName(), "Exit"};
        int option = JOptionPane.showOptionDialog(
                null,
                "How do you want to continue?",
                "Program options",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (options[option].equals(Client.class.getSimpleName())) {
            Client client = new Client(Connection.LOCAL_HOST_ADDRESS, Connection.DEFAULT_PORT);
            client.start();
        } else if (options[option].equals(Server.class.getSimpleName())) {
            Server server = new Server(Connection.DEFAULT_PORT);
            server.start();
        }

        // exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> Logger.log("Program exit."), "Shutdown-thread"));
        exit();
    }

    public static void exit() {
        System.exit(1337);
    }
}
