/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project1;

import Presentation.Controller.ViewController;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author james
 */
public class ClienteA {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        ViewController viewController = new ViewController();
       viewController.startA();
    }
}
