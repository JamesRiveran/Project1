package Server;

import Protocol.IService;
import Protocol.Message;
import Protocol.ProtocolData;
import Protocol.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Worker {
    Server srv;
    ObjectInputStream in;
    ObjectOutputStream out;
    IService service;
    User user;

    public Worker(Server srv, ObjectInputStream in, ObjectOutputStream out, User user, IService service) {
        this.srv=srv;
        this.in=in;
        this.out=out;
        this.user=user;
        this.service=service;
    }

    boolean continuar;    
    public void start(){
        try {
            System.out.println("Worker atendiendo peticiones...");
            Thread t = new Thread(new Runnable(){
                public void run(){
                    listen();
                }
            });
            continuar = true;
            t.start();
        } catch (Exception ex) {  
        }
    }
    
    public void stop(){
        continuar=false;
        System.out.println("Conexion cerrada...");
    }
    
    public void listen(){
        int method;
        while (continuar) {
            try {
                
                method = in.readInt();
                System.out.println("Operacion: "+method);
                switch(method){
                //case Protocol.LOGIN: done on accept
                case ProtocolData.LOGOUT:
                    try {
                        srv.remove(user);
                        //service.logout(user); //nothing to do
                    } catch (Exception ex) {}
                    stop();
                    break;                 
                case ProtocolData.POST:
                    Message message=null;
                    try {
                        message = (Message)in.readObject();
                        message.setSender(user);
                        //Toda la logica de implementacion de ir al crud hacer los cambios y retornar el mensaje que se necesita.
                        srv.deliver(message);
                        //service.post(message); // if wants to save messages, ex. recivier no logged on
                        System.out.println(user.getNombre()+": "+message.getMessage());
                    } catch (ClassNotFoundException ex) {}
                    break;

                    case ProtocolData.DoECHO:




                        break;
                }
                out.flush();
            } catch (IOException  ex) {
                System.out.println("VSADV"+ex);
                continuar = false;
            }                        
        }
    }
    
    //Metodo para entregar solo a su propio cliente, el de arriba entrega a todos.
    public void deliver(Message message){
        try {
            out.writeInt(ProtocolData.DELIVER);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
        }
    }
}
