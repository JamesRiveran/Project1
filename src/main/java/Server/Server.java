/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
 *
 * @author james
 */
public class Server {
    ServerSocket srv;
    List<Worker> workers; 
    
    public Server() {
        try {
            srv = new ServerSocket(ProtocolData.PORT);
            //workers se encarga del cliente, es lo que tengo que atender para atender las solicitudes del cliente. Es un hilo
            workers =  Collections.synchronizedList(new ArrayList<Worker>());
            System.out.println("Servidor iniciado...");//Está listo para atender solicitudes
        } catch (IOException ex) {
            
        }
    }
    
    public void run(){
        IService service = new Service();

        boolean continuar = true;
        ObjectInputStream in=null;
        ObjectOutputStream out=null;
        Socket skt=null;
        while (continuar) { //Se enclicla esperan a que llegue Datos, garantiza que el hilo escuche siempre
            try {
                skt = srv.accept();
                //Limpiar entrada y salida del socket
                in = new ObjectInputStream(skt.getInputStream());
                out = new ObjectOutputStream(skt.getOutputStream() );
                System.out.println("Conexion Establecida...");//Alguien que quiere comunicación
                User user=this.login(in,out,service);                          
                Worker worker = new Worker(this,in,out,user, service); 
                workers.add(worker);                      
                worker.start();                                                
            }
            catch (IOException | ClassNotFoundException ex) {}
            catch (Exception ex) {
                try {
                    out.writeInt(ProtocolData.ERROR_LOGIN);
                    out.flush();
                    skt.close();
                } catch (IOException ex1) {}
               System.out.println("Conexion cerrada...");
            }
        }
    }
    
    private User login(ObjectInputStream in,ObjectOutputStream out,IService service) throws IOException, ClassNotFoundException, Exception{
        int method = in.readInt();
        if (method!=ProtocolData.LOGIN) throw new Exception("Should login first");
        User user=(User)in.readObject();                          
        user=service.login(user);
        out.writeInt(ProtocolData.ERROR_NO_ERROR);
        out.writeObject(user);
        out.flush();
        return user;
    }
    
    public void deliver(Message message){//Como receta, no se modifica.
        for(Worker wk:workers){
            wk.deliver(message);
        }        
    } 
    
    public void remove(User u){
        for(Worker wk:workers) if(wk.user.equals(u)){workers.remove(wk);break;}
        System.out.println("Quedan: " + workers.size());
    }
    
}
