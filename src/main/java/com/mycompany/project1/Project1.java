/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.project1;

import Presentation.Controller.ControllerSocket;
import Presentation.Controller.ViewController;
import Presentation.Model.SocketModel;
import Presentation.View.Modulo;
import Server.data.BDCalibration;
import Server.data.BDMeasurement;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author james
 */
public class Project1 {
    private static ControllerSocket controllerSocket;
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        Modulo view = new Modulo();
        SocketModel model = new SocketModel();
        ViewController viewController = new ViewController();
        
        
       
       viewController.startSocket();
       viewController.start();
       
       //ViewController.SendMessage("ADIOS");
       
    }
}
