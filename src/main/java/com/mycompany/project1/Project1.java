/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.project1;

import Presentation.Controller.ViewController;
import Data.BDCalibration;
import Data.BDMeasurement;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author james
 */
public class Project1 {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        Presentation.Controller.ViewController view = new ViewController();
        view.start();
        

    }
}
