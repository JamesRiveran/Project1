/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.project1;

import Controller.DataBaseConnection;
import Controller.ViewController;
import Model.XMLCreator;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author james
 */
public class Project1 {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        XMLCreator xmlCreator = new XMLCreator();
        xmlCreator.createLaboratorioXML();
        Controller.ViewController view = new ViewController();
        DataBaseConnection databaseConn = new DataBaseConnection();
        
        view.start();
        
        String UserName = "root";
        String Password = "R#m4B@!p8$Dw2%";

        
        databaseConn.connect("jdbc:mysql://127.0.0.1:3306/bd_laboratorio",UserName,Password);
        databaseConn.getAllRecords();
    }
}
