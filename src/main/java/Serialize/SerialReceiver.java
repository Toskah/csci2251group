package Serialize;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Handle Receiving info from server
 *
 * @author Dexter Elliott
 */
public class SerialReceiver {


    public SerialReceiver() {

        try {
            Socket socket = new Socket("Local Host", 8080);
            ObjectInputStream OIS = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Problem setting socket");
            e.getMessage().trim();
        }



    }


}