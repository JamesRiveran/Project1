/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.Calibration;
import Model.CalibrationList;
import Model.XMLLoader;
import View.Modulo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author james
 */
public class CalibrationController implements ActionListener {
    CalibrationList calibrationList;
    ViewController viewController;
    Modulo view;
    String filePath = "Laboratorio.xml";
   
    public CalibrationController() {
        this.calibrationList = new CalibrationList();
        this.view = new Modulo();
    }
    
    public void startCalibration(){
        view.getCalibrationBtnClean().addActionListener(this);
        view.getCalibrationBtnDelete().addActionListener(this);
        view.getCalibrationBtnSave().addActionListener(this);
        view.getBtnDateCalibraton().addActionListener(this);
        view.getBtnPDFCalibration().addActionListener(this);
        view.getBtnSearchCalibration().addActionListener(this);
        view.setLocationRelativeTo(null);
        view.setVisible(true);
        //view.getCalibrationTxtNumber().setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(view.getCalibrationBtnSave())){
            try{
                if(view.getCalibrationTxtDate().getText().trim().isEmpty()){
                    viewController.showMessage("Debe ingresar la fecha del instrumento");
                }else if(view.getCalibrationTxtMeasurement().getText().trim().isEmpty()){
                    viewController.showMessage("Debe ingresar la calibración del instrumento");
                }else{
                    try{
                        Calibration newCalibration = new Calibration(view.getCalibrationTxtDate().getText(), 
                                Integer.parseInt(view.getCalibrationTxtNumber().getText()), 
                                Integer.parseInt(view.getCalibrationTxtMeasurement().getText()));
                        
                        calibrationList.getList().add(newCalibration);
                        XMLLoader.saveToXMLCalibration(filePath, calibrationList.getList());
                        DefaultTableModel tableModel = (DefaultTableModel) view.getTblCalibrations().getModel();
                        tableModel.insertRow(0, new Object[]{newCalibration.getId(),newCalibration.getDate(),newCalibration.getMeasuring()});
                    }catch(Exception ex){
                        viewController.showMessage("Error al guardar en el archivo XML: " + ex.getMessage());
                    }
                }
            }catch(Exception ex){
                viewController.showMessage(ex.getMessage());
            }
        }
    }
    
}
