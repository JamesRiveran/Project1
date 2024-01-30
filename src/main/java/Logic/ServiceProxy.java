/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

import Presentation.Controller.CalibrationController;
import Presentation.Controller.ControllerSocket;
import Presentation.Controller.IntrumentsController;
import Presentation.Controller.ViewController;
import static Presentation.Controller.ViewController.showMessage;
import Protocol.IService;
import Protocol.Message;
import Protocol.ProtocolData;
import Protocol.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    IntrumentsController inst;
    CalibrationController cali;

    public ServiceProxy() {
        viewController = new ViewController(true);
        inst = new IntrumentsController(true);
        cali = new CalibrationController(true);
    }

    public void setController(ControllerSocket controller) {
        this.controller = controller;
    }

    public void setViewController(ViewController viewController) {
        this.viewController = viewController;
    }

    public void setCali(CalibrationController cali) {
        this.cali = cali;
    }

    Socket skt;

    private void connect() {
        try {
            skt = new Socket(ProtocolData.SERVER, ProtocolData.PORT);
            out = new ObjectOutputStream(skt.getOutputStream());
            out.flush();
            in = new ObjectInputStream(skt.getInputStream());
        } catch (IOException ex) {
            // Manejar la excepción de E/S, por ejemplo, mostrar un mensaje de error
            showMessage(ViewController.view, "Error de conexión, revisar conexión con el servidor", "error");
        } catch (Exception ex) {
            // Manejar otras excepciones de manera general
            showMessage(ViewController.view, "Error al conectar", "error");
        }
    }

    private void disconnect() throws Exception {
        skt.shutdownOutput();
        skt.close();
    }

    public User login(User u) throws Exception {
        connect();
        try {
//            System.out.println("Enviando Protocol Login...");
            out.writeInt(ProtocolData.LOGIN);
//            System.out.println("Enviando User...");
            out.writeObject(u);
//            System.out.println("Flush...");
            out.flush();
            int response = in.readInt();
            System.out.println("Respuesta del server: " + response);
            if (response == ProtocolData.ERROR_NO_ERROR) {
//                System.out.println("Respuesta User...");
                User u1 = (User) in.readObject();
//                System.out.println("call start()...");
                this.start();
//                System.out.println("end call start()...");
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
            out.flush();
        } catch (IOException ex) {
            System.out.print("Error");
        }
    }

    public void getInformation(Message message) {
        try {
            out.writeInt(ProtocolData.GET_INFROMATION_MODULO_1);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
            System.out.print("Error");

        }
    }

    public void saveIntruments(Message message) {
        try {
            out.writeInt(ProtocolData.SAVE_TYPEINSTRUMENTS);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {

        }
    }

    public void saveModulo2(Message message) {
        try {
            out.writeInt(ProtocolData.SAVE_INSTRUMENTS);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
            System.out.print("Error");

        }
    }

    public void deleteInstruments(Message message) {
        try {
            out.writeInt(ProtocolData.DELETE_TYPEINSTRUMENTS);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
            System.out.print("Error");

        }
    }

    public void deleteModulo2(Message message) {
        try {
            out.writeInt(ProtocolData.DELETE_INSTRUMENTS);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
            System.out.print("Error");

        }
    }

    public void getInformationModulo2(Message message) {
        try {
            out.writeInt(ProtocolData.GET_INFROMATION_MODULO_2);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
            System.out.print("Error");

        }
    }

    public void getInformationModulo3(Message message) {
        try {
            out.writeInt(ProtocolData.GET_INFORMATION_MODULO_3);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
            System.out.print("Error");

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
//                System.out.println("Mensaje " + message.getMessage());
//                System.out.println("Mensaje " + message.getUnits());
//                System.out.println("Mensaje " + message.getSender());
//                System.out.println("Mensaje " + message.getTypeIntruments());
                cali.deliver(message);
                inst.deliver(message);
                viewController.deliver(message);

            }
        }
        );
    }
}
