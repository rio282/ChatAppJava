package nl.hva.caj.app;

import nl.hva.caj.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public abstract class Connection {

    public static final String LOCAL_HOST_ADDRESS = getInternalIp();
    public static final String PUBLIC_HOST_ADDRESS = getExternalIp();
    public static final int DEFAULT_PORT = 55555;

    protected String hostAddress;
    protected int port;

    public Connection(String hostAddress, int port) {
        this.hostAddress = hostAddress;
        this.port = port;

        try {
            initialize();
        } catch (IOException e) {
            Logger.errf("%s:%s", e.getCause(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getInternalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "localhost";
    }

    public static String getExternalIp() {
        URL whatismyip = null;
        BufferedReader bufferedReader = null;
        try {
            whatismyip = new URL("http://checkip.amazonaws.com");
            bufferedReader = new BufferedReader(
                    new InputStreamReader(whatismyip.openStream())
            );

            // return ip
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "ERROR";
    }


    protected abstract void initialize() throws IOException;

    public abstract void start() throws IOException;

    public abstract void cleanup() throws IOException;
}
