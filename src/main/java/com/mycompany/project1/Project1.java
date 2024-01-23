/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.project1;

import Presentation.Controller.ViewController;
import Data.BDCalibration;
import Data.BDMeasurement;
import Protocol.Message;
import Protocol.ProtocolData;
import Protocol.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author james
 */
public class Project1 {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
//        Presentation.Controller.ViewController view = new ViewController();
//        view.start();
        try (Socket socket = new Socket("127.0.0.1", ProtocolData.PORT + 1); ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            User user = new User("ID_123", "clave_secreta", "NombreDeUsuario");
            String instrumentType = "TipoInstrumentoEjemplo";
            // Ejemplo: Enviar un mensaje al servidor
            Message message = new Message(user, instrumentType, null);
            out.writeInt(ProtocolData.POST); // Código de operación para un mensaje POST
            out.writeObject(message);
            out.flush();

            // Leer la respuesta del servidor (si es necesario)
            Object response = in.readObject();
            if (response instanceof Message) {
                Message serverResponse = (Message) response;
                System.out.println("Respuesta del servidor: " + serverResponse.getMessage());
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
