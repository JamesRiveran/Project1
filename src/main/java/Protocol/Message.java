package Protocol;

import java.io.Serializable;

public class Message implements Serializable{
    User sender;
    String message;

    public Message() {
    }

    public Message(User sender,String message) {
        this.sender = sender;
        this.message = message;
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
    
}
