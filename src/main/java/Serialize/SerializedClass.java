package Serialize;



import java.io.Serializable;
import java.util.Date;
import java.util.Random;

/**
 * Simple object to send from one process to another through a network
 * connection.
 *
 * @author Kenneth Ingham
 */
public class SerializedClass implements Serializable {

    private transient float fData;
    private String sData;
    private int iData;

    /**
     * Constructor that uses a random integer for the second datum.
     *
     * @param s String data
     */
    SerializedClass(String s) {
        sData = s;
        Date d = new Date();
        Random r = new Random(d.getTime());
        iData = r.nextInt();
        fData = r.nextFloat();
    }

    /**
     * Constructor that uses provided data.
     *
     * @param s a String
     * @param i an integer
     */
    SerializedClass(String s, int i) {
        Date d = new Date();
        Random r = new Random(d.getTime());
        sData = s;
        iData = i;
        fData = r.nextFloat();
    }

    /**
     * Convert this object into a String.
     *
     * @return the string containing the data in the object
     */
    @Override
    public String toString() {
        return String.format("data: '%s' iData: %d fData: %f", sData, iData, fData);
    }

}
