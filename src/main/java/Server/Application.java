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
        try (
            Socket socket = new Socket("127.0.0.1", ProtocolData.PORT + 1);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
    ) {
        // Crear un mensaje con tipo de instrumento y usuario
        User user = new User("001", "001", "Juan");
        String instrumentType = "TipoInstrumentoEjemplo";
        Message message = new Message(user, instrumentType, null);

        // Enviar el mensaje al servidor
        out.writeObject(message);

        // Recibir la respuesta del servidor
        String response = (String) in.readObject();
        System.out.println("Respuesta del servidor: " + response);
    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
    }
    }
}
