/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.project1;

import Controller.ViewController;
import java.io.IOException;
import org.jdom2.JDOMException;

/**
 *
 * @author james
 */
public class Project1 {

    public static void main(String[] args) throws JDOMException, IOException {
        Controller.ViewController view = new ViewController();
        view.start();
    }
}
