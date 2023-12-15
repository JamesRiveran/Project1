/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.CalibrationList;
import View.Modulo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 *
 * @author james
 */
public class CalibrationController implements ActionListener {
    CalibrationList calibrationList;
    Modulo view;
    static int idCalibration=0;
   
    public CalibrationController() {
        this.calibrationList = new CalibrationList();
        this.view = new Modulo();
    }
    
    public void startCalibration(){
        view.getCalibrationBtnClean().addActionListener(this);
        view.getCalibrationBtnDelete().addActionListener(this);
        view.getCalibrationBtnSave().addActionListener(this);
       // view.getBtnDateCalibraton().addActionListener(this);
        view.getBtnPDFCalibration().addActionListener(this);
        view.getBtnSearchCalibration().addActionListener(this);
        view.setLocationRelativeTo(null);
        view.setVisible(true);
        view.getCalibrationTxtNumber().setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(view.getBtnSave())){
            
        }
    }
    
}
