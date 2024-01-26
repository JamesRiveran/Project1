package Protocol;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
    private User sender;
    private String message;
    private String[] data;

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

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", message='" + message + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
