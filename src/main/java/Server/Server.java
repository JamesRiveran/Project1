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
import java.util.Collections;
import java.util.List;

public class Server {

    ServerSocket srv;
    List<Worker> workers;

    public Server() {
        try {
            srv = new ServerSocket(ProtocolData.PORT);
            workers = Collections.synchronizedList(new ArrayList<Worker>());
            System.out.println("Servidor iniciado...");
        } catch (IOException ex) {
        }
    }

    public void run() {
        IService service = new Service();

        boolean continuar = true;
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        Socket skt = null;
        while (continuar) {
            try {
                //skt = srv.accept();
                skt = srv.accept();
                in = new ObjectInputStream(skt.getInputStream());
                out = new ObjectOutputStream(skt.getOutputStream());
                System.out.println("Conexion Establecida...");
                User user = this.login(in, out, service);
                System.out.println("Credenciales recibidos...");
                Worker worker = new Worker(this, in, out, user, service);
                System.out.println("New worker creado...");
                workers.add(worker);
                worker.start();
                System.out.println("Worker iniciado...");
            } catch (IOException | ClassNotFoundException ex) {
            } catch (Exception ex) {
                try {
                    out.writeInt(ProtocolData.ERROR_LOGIN);
                    out.flush();
                    skt.close();
                } catch (IOException ex1) {
                }
                System.out.println("Conexion cerrada...");
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
        for (Worker wk : workers) {
            System.out.println("Esto esta en el server " + message);
            wk.deliver(message);
        }
    }

    public void remove(User u) {
        for (Worker wk : workers) {
            if (wk.user.equals(u)) {
                workers.remove(wk);
                break;
            }
        }
        System.out.println("Quedan: " + workers.size());
    }

}
