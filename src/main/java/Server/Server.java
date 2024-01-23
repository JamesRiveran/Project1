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

    private ServerSocket srv;
    private List<Worker> workers;

    public Server() {
        try {
            srv = new ServerSocket(ProtocolData.PORT + 1);
            workers = Collections.synchronizedList(new ArrayList<>());
            System.out.println("Servidor iniciado "); // Informar el puerto utilizado
        } catch (IOException ex) {
            ex.printStackTrace(); // Considera imprimir un mensaje más descriptivo o lanzar una excepción
        }
    }

    public void run() {
        IService service = new Service();

        while (!srv.isClosed()) {
            try (Socket skt = srv.accept(); ObjectInputStream in = new ObjectInputStream(skt.getInputStream()); ObjectOutputStream out = new ObjectOutputStream(skt.getOutputStream())) {

                System.out.println("Conexión establecida con: " + skt.getInetAddress()); // Informar la conexión
                User user = this.login(in, out, service);
                Worker worker = new Worker(this, in, out, user, service);
                workers.add(worker);
                worker.start();

            } catch (IOException ex) {
                // Manejar la excepción de manera descriptiva
                System.err.println("Error al aceptar la conexión: " + ex.getMessage());
            } catch (ClassNotFoundException ex) {
                // Manejar la excepción de manera descriptiva
                System.err.println("Error al leer el objeto: " + ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Mensaje de cierre del servidor
        System.out.println("El servidor se ha detenido.");
    }

    private User login(ObjectInputStream in, ObjectOutputStream out, IService service) throws IOException, ClassNotFoundException, Exception {
        int method = in.readInt();
        if (method != ProtocolData.LOGIN) {
            throw new Exception("Debería iniciar sesión primero");
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
        System.out.println("Clientes restantes: " + workers.size());
    }

    public void stop() {
        try {
            // Cerrar todas las conexiones de los clientes antes de cerrar el servidor
            synchronized (workers) {
                workers.forEach(Worker::stop);
            }
            srv.close();
            System.out.println("Servidor detenido");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
