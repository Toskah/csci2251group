package Serialize;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Demo for sending a serialized object through the network.
 *
 * @author Kenneth Ingham
 */
public class SerialSender {
    /**
     * Connect to the specified server.
     *
     * @param host the host name to connect to
     * @param port the port on the host
     * @return a set up Socket connected to the server
     */
    private static Socket ServerConnect(String host, int port) {
        // Open connection to server
        Socket server = null;
        try {
            server = new Socket(host, port);
        } catch (UnknownHostException e) {
            System.err.println("Received an Unknown host exception for host '"
                    + host + "': " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Received an I/O exception for server host '"
                    + host + "' on port " + port + ": " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Connected to " + host + ":" + port);
        return server;
    }

    public static void main(String args[]) {
        if (args.length != 3) {
            System.err.println("Invalid argument count: " + args.length);
            System.err.println("Usage: java SerialSender hostname port string");
            System.exit(1);
        }
        String host = args[0];
        Integer port = Integer.parseInt(args[1]);
        SerializedClass data = new SerializedClass(args[2]);

        Socket server = ServerConnect(host, port);
        ObjectOutputStream serverObj = null;
        try {
            serverObj = new ObjectOutputStream(server.getOutputStream());
        } catch (IOException e) {
            System.err.println("Received an I/O exception while getting "
                    + " OutputStream for server" + e.getMessage());
            System.exit(1);
        }

        // Send the object.  Remember to flush the stream.
        try {
            serverObj.writeObject(data);
        } catch (IOException e) {
            System.err.println("Received an I/O exception while writing "
                    + " object " + e.getMessage());
            System.exit(1);
        }
        try {
            serverObj.flush();
        } catch (IOException e) {
            System.err.println("Received an I/O exception while flushing "
                    + " object " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Sent " + data.toString());
    }
}
