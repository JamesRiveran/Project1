package Protocol;

import Presentation.Model.UnidsType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Message implements Serializable {

    private User sender;
    private String message;
    private String[] data;
    private ArrayList<UnidsType> units;

    public Message() {
    }

    public Message(User sender, String message, String[] data) {
        this.sender = sender;
        this.message = message;
        this.data = data;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public ArrayList<UnidsType> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<UnidsType> units) {
        this.units = units;
    }

    @Override
    public String toString() {
        return "Message{"
                + "sender=" + sender
                + ", message='" + message + '\''
                + ", data=" + Arrays.toString(data)
                + '}';
    }
}
