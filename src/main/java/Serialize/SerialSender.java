package Serialize;


import dao.PropertyDAO;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;

/**
 * Handles Sending to website
 *
 * @author Dexter Elliott
 */
public class SerialSender {
    /**
     * Sets the socket to send objects from for serialized objects
     *
     * @param obj The serializable object that is sent to the website
     */
    public static void send(Serializable obj) {
        try {
            Socket socket = new Socket("http://localhost/", 8082);
            ObjectOutputStream OOS = new ObjectOutputStream(socket.getOutputStream());
            OOS.writeObject(obj);
        } catch (IOException e) {
            e.getMessage().trim();
        }
    }

    /**
     * Sets the socket to send objects from for serialized objects that are lists
     *
     * @param objs A serialized list
     */
    public static void send(List<PropertyDAO.PropertyBaseData> objs) {
        try {
            Socket socket = new Socket("http://localhost/", 8082);
            ObjectOutputStream OOS = new ObjectOutputStream(socket.getOutputStream());
            OOS.writeObject(objs);
        } catch (IOException e) {
            e.getMessage().trim();
        }

    }

}
