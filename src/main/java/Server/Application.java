package Server;

import Protocol.Message;
import Protocol.ProtocolData;
import Protocol.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Application {

    public static void main(String[] args) {

        Server server = new Server();
        server.run();

    }
}
