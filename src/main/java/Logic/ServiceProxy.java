/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

import Presentation.Controller.ControllerSocket;
import Presentation.Controller.ViewController;
import Protocol.IService;
import Protocol.Message;
import Protocol.ProtocolData;
import Protocol.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.SwingUtilities;

/**
 *
 * @author james
 */
public class ServiceProxy implements Protocol.IService {

    private static IService theInstance;

    public static IService instance() {
        if (theInstance == null) {
            theInstance = new ServiceProxy();
        }
        return theInstance;
    }

    ObjectInputStream in;
    ObjectOutputStream out;
    ControllerSocket controller;
    ViewController viewController;

    public ServiceProxy() {
        viewController=new ViewController(true);
    }

    public void setController(ControllerSocket controller) {
        this.controller = controller;
    }

    public void setViewController(ViewController viewController) {
        this.viewController = viewController;
    }
    

    Socket skt;

    private void connect() throws Exception {
        //skt = new Socket(ProtocolData.SERVER,ProtocolData.PORT+1);
        skt = new Socket(ProtocolData.SERVER, ProtocolData.PORT);
        out = new ObjectOutputStream(skt.getOutputStream());
        out.flush();
        in = new ObjectInputStream(skt.getInputStream());
        
    }

    private void disconnect() throws Exception {
        skt.shutdownOutput();
        skt.close();
    }

    public User login(User u) throws Exception {
        connect();
        try {
            System.out.println("Enviando Protocol Login...");
            out.writeInt(ProtocolData.LOGIN);
            System.out.println("Enviando User...");
            out.writeObject(u);
            System.out.println("Flush...");
            out.flush();
            int response = in.readInt();
            System.out.println("Respuesta del server: " + response);
            if (response == ProtocolData.ERROR_NO_ERROR) {
                System.out.println("Respuesta User...");
                User u1 = (User) in.readObject();
                System.out.println("call start()...");
                this.start();
                System.out.println("end call start()...");
                return u1;
            } else {
                System.out.println("Error recibido:" + response);
                disconnect();
                throw new Exception("No remote user");
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("IOException..." + ex.getMessage());
            return null;
        }
    }

    public void logout(User u) throws Exception {
        out.writeInt(ProtocolData.LOGOUT);
        out.writeObject(u);
        out.flush();
        this.stop();
        this.disconnect();
    }

    public void post(Message message) {
        try {
            out.writeInt(ProtocolData.POST);
            out.writeObject(message);
            System.out.println("Esto esta en el proxy post" + message);
            out.flush();
        } catch (IOException ex) {

        }
    }

    public void getUnit(Message message) {
        try {
            out.writeInt(ProtocolData.getUnit);
            out.writeObject(message);
            System.out.println("Esto es " + message);
            out.flush();
        } catch (IOException ex) {

        }
    }

    // LISTENING FUNCTIONS
    boolean continuar = true;

    public void start() {
        System.out.println("Client worker atendiendo peticiones...");
        Thread t = new Thread(new Runnable() {
            public void run() {
                listen();
            }
        });
        continuar = true;
        t.start();
    }

    public void stop() {
        continuar = false;
    }

    public void listen() {
        int method;
        while (continuar) {
            try {
                method = in.readInt();
                System.out.println("DELIVERY");
                System.out.println("Operacion: " + method);

                //Validar respuesta según codigo que envia el socket mediante el method
                switch (method) {
                    case ProtocolData.DELIVER:
                    try {
                        Message message = (Message) in.readObject();
                        System.out.println("Esto esta en el proxy " + message);
                        deliver(message);
                    } catch (ClassNotFoundException ex) {
                    }
                    break;
                }
                out.flush();
            } catch (IOException ex) {
                continuar = false;
            }
        }
    }

    private void deliver(final Message message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                System.out.println("Respuesta recibida:");
                System.out.println("Mensaje " + message.getMessage());
                System.out.println("Mensaje " + message.getUnits());
                System.out.println("Mensaje " + message.getSender());
                viewController.deliver(message);
            }
        }
        );
    }
}
