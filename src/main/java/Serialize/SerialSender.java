package Serialize;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles Sending to website
 *
 * @author Dexter Elliott
 */
public class SerialSender {

    public SerialSender(){
        try{
            Socket socket = new Socket("Local Host", 8082);
            ObjectOutputStream OOS = new ObjectOutputStream(socket.getOutputStream());
        }catch (IOException e){
            e.getMessage().trim();
        }

    }

}
