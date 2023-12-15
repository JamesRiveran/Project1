/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.project1;

import Controller.CalibrationController;
import Controller.ViewController;

/**
 *
 * @author james
 */
public class Project1 {

    public static void main(String[] args) {
        Controller.ViewController view = new ViewController();
        Controller.CalibrationController viewCalibration = new CalibrationController();
        view.start();
        viewCalibration.startCalibration();
    }
}
