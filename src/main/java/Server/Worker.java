package Server;

import Presentation.Model.UnidsType;
import Protocol.IService;
import Protocol.Message;
import Protocol.ProtocolData;
import Protocol.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker {

    Server srv;
    ObjectInputStream in;
    ObjectOutputStream out;
    IService service;
    User user;
    BDTypeInstrument type;

    public Worker(Server srv, ObjectInputStream in, ObjectOutputStream out, User user, IService service) {
        this.srv = srv;
        this.in = in;
        this.out = out;
        this.user = user;
        this.service = service;
        this.type = new BDTypeInstrument();
    }

    boolean continuar;

    public void start() {
        try {
            System.out.println("Worker atendiendo peticiones...");
            Thread t = new Thread(new Runnable() {
                public void run() {
                    listen();
                }
            });
            continuar = true;
            t.start();
        } catch (Exception ex) {
        }
    }

    public void stop() {
        continuar = false;
        System.out.println("Conexion cerrada...");
    }

    public void listen() {
        int method;
        while (continuar) {
            try {

                method = in.readInt();
                System.out.println("Operacion: " + method);
                switch (method) {
                    //case Protocol.LOGIN: done on accept
                    case ProtocolData.LOGOUT:
                    try {
                        srv.remove(user);
                        //service.logout(user); //nothing to do
                    } catch (Exception ex) {
                    }
                    stop();
                    break;
                    case ProtocolData.POST:
                        Message message = null;
                        try {
                            message = (Message) in.readObject();
                            System.out.println(message);
                            message.setSender(user);
                            //Toda la logica de implementacion de ir al crud hacer los cambios y retornar el mensaje que se necesita.
                            srv.deliver(message);
                            //service.post(message); // if wants to save messages, ex. recivier no logged on
                            System.out.println(user.getNombre() + ": " + message.getMessage());
                        } catch (ClassNotFoundException ex) {
                        }
                        break;

                    case ProtocolData.getUnit:
                        Message getUnit = null;
                        try {
                            getUnit = (Message) in.readObject();
                            System.out.println(getUnit);
                            getUnit.setUnits(type.getAllRecordsOfUnits());
                            getUnit.setSender(user);
                            
                            // Envía la lista de unidades al cliente a través del método deliver
                            srv.deliver(getUnit);
                            System.out.println(user.getNombre() + ": " + getUnit.getUnits());
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case ProtocolData.GETTYPEINSTRUMENT:
                        Message getTypeInstrument = null;
                        try {
                            getTypeInstrument = (Message) in.readObject();
                            System.out.println(getTypeInstrument);
                            getTypeInstrument.setListOfIModu1o1(type.getTypeInstrument());
                            getTypeInstrument.setSender(user);
                            
                            // Envía la lista de unidades al cliente a través del método deliver
                            srv.deliver(getTypeInstrument);
                            System.out.println(user.getNombre() + ": " + getTypeInstrument.getListOfIModu1o1());
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;

                }
                out.flush();
            } catch (IOException ex) {
                System.out.println(ex);
                continuar = false;
            }
        }
    }

    //Metodo para entregar solo a su propio cliente, el de arriba entrega a todos.
    public void deliver(Message message) {
        try {
            System.out.println("Esto es en el worker deliver " + message);
            out.writeInt(ProtocolData.DELIVER);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
        }
    }
}
