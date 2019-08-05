package Serialize;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Demo for receiving a serialized object through the network.
 *
 * @author Kenneth Ingham
 */
public class SerialReceiver {

    /**
     * Set up the server socket, handing the possible exceptions by printing an
     * error message and exiting.
     *
     * @param port the port to listen on
     * @return a ServerSocket that is set up and listening.
     */
    private static ServerSocket serverSetup(int port) {
        ServerSocket server = null;

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Received an I/O exception while creating "
                    + "a server port on " + port + e.getMessage());
            System.exit(1);
        }
        return server;
    }

    /**
     * A wrapper for getting a client connection. This method handles the
     * possible exceptions by printing a message and exiting.
     *
     * @param server the SeverSocket that is in the listen state
     * @return a Socket connected to the client
     */
    private static Socket getNextClient(ServerSocket server) {
        Socket client = null;
        try {
            client = server.accept();
        } catch (IOException e) {
            System.err.println("Received an I/O exception while accepting "
                    + "a client connection: " + e.getMessage());
            System.exit(1);
        }

        // Log information about the client.
        InetAddress clientAddr = client.getInetAddress();
        System.out.println("Received a connection from client '"
                + clientAddr.getHostName() + "' IP: "
                + clientAddr.getHostAddress() + " on port "
                + client.getPort());
        return client;
    }

    public static void main(String args[]) {
        final int port = 5678;
        ServerSocket server = null;
        Socket client = null;

        ObjectInputStream in = null;

        server = serverSetup(port);
        while (true) {
            client = getNextClient(server);

            try {
                in = new ObjectInputStream(client.getInputStream());
            } catch (IOException e) {
                System.err.println("Received an I/O exception while getting "
                        + "client InputStream " + e.getMessage());
                System.exit(1);
            }

            System.out.println("Have the input stream.");

            try {
                data = (SerializedClass) in.readObject();
            } catch (IOException e) {
                System.err.println("Received an I/O exception while getting "
                        + "object " + e.getMessage());
                System.exit(1);
            } catch (ClassNotFoundException ex) {
                System.err.println("Received a ClassNotFoundException "
                        + "while reading the object. Err: " + ex.toString());
                System.exit(1);
            }

            System.out.println("Received the object: " + data.toString());

            // Important in a server to close the client connection when done.
            try {
                client.close();
            } catch (IOException e) {
                System.err.println("Received an I/O exception while closing "
                        + "a client connection: " + e.getMessage());
                System.exit(1);
            }
            System.out.println("Closed client socket.");

        }
    }
}
