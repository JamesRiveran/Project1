package Server;

import Protocol.IService;
import Protocol.Message;
import Protocol.ProtocolData;
import Protocol.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Server {

    private ServerSocket srv;
    private List<Worker> workers;

    public Server() {
        try {
            srv = new ServerSocket(ProtocolData.PORT +1);
            workers = Collections.synchronizedList(new ArrayList<>());
            System.out.println("Servidor iniciado..."); // Está listo para atender solicitudes
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        IService service = new Service();

        while (!srv.isClosed()) {
            try (Socket skt = srv.accept(); ObjectInputStream in = new ObjectInputStream(skt.getInputStream()); ObjectOutputStream out = new ObjectOutputStream(skt.getOutputStream())) {

                System.out.println("Conexion Establecida..."); // Alguien que quiere comunicación
                User user = this.login(in, out, service);
                Worker worker = new Worker(this, in, out, user, service);
                workers.add(worker);
                worker.start();

            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private User login(ObjectInputStream in, ObjectOutputStream out, IService service) throws IOException, ClassNotFoundException, Exception {
        int method = in.readInt();
        if (method != ProtocolData.LOGIN) {
            throw new Exception("Should login first");
        }
        User user = (User) in.readObject();
        user = service.login(user);
        out.writeInt(ProtocolData.ERROR_NO_ERROR);
        out.writeObject(user);
        out.flush();
        return user;
    }

    public void deliver(Message message) {
        synchronized (workers) {
            workers.forEach(wk -> wk.deliver(message));
        }
    }

    public void remove(User u) {
        synchronized (workers) {
            workers.removeIf(wk -> wk.user.equals(u));
        }
        System.out.println("Quedan: " + workers.size());
    }

    public void stop() {
        try {
            srv.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
