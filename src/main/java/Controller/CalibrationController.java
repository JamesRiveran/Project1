
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.Calibration;
import Model.CalibrationList;
import Model.XMLLoader;
import View.Modulo;
import com.toedter.calendar.JDateChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jdom2.JDOMException;


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
    
    public void startCalibration() throws JDOMException, IOException{
        view.getCalibrationBtnClean().addActionListener(this);
        view.getCalibrationBtnDelete().addActionListener(this);
        view.getCalibrationBtnSave().addActionListener(this);
        //view.getBtnDateCalibraton().addActionListener(this);
        view.getBtnPDFCalibration().addActionListener(this);
        view.getBtnSearchCalibration().addActionListener(this);
        view.setLocationRelativeTo(null);
        view.setVisible(true);
        XMLLoader.ensureIdCounterExists(filePath);
        int idCounter=idCounter();
        view.getCalibrationTxtNumber().setText(String.valueOf(String.valueOf(idCounter)));
        view.getCalibrationTxtNumber().setEnabled(false);
    }

    public void showMessage(String errorMessage) {
        JOptionPane.showMessageDialog(view, errorMessage, "Validación", JOptionPane.ERROR_MESSAGE);
    }

    private static String dateToString(JDateChooser dateChooser) {
        Date fechaSeleccionada = dateChooser.getDate();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy/MM/dd");
        return formatoFecha.format(fechaSeleccionada);
    }
    
    private int idCounter() throws JDOMException, IOException {
        int idCounter = XMLLoader.getIdCounterFromXML(filePath);
        return idCounter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(view.getCalibrationBtnSave())){
            try{
                int measurement = Integer.parseInt(view.getCalibrationTxtMeasurement().getText());
                if(view.getCalibrationDateChooser().getDate() == null){
                    showMessage("Debe ingresar la fecha del instrumento");
                } else if (view.getCalibrationTxtMeasurement().getText().trim().isEmpty()) {
                    showMessage("Debe ingresar la calibración del instrumento");
                } else if (measurement < 2)  {
                    showMessage("La cantidad minima de mediciones que se permite ingresar es de 2");
                } else {
                    try {
                        int newIdNumber=0;
                        JDateChooser dateChooser = view.getCalibrationDateChooser();
                        String date = dateToString(dateChooser);
                        Calibration newCalibration = new Calibration(date,
                                Integer.parseInt(view.getCalibrationTxtNumber().getText()), 
                                Integer.parseInt(view.getCalibrationTxtMeasurement().getText()));
                        calibrationList.getList().add(newCalibration);
                        XMLLoader.saveToXMLCalibration(filePath, calibrationList.getList());
                        DefaultTableModel tableModel = (DefaultTableModel) view.getTblCalibrations().getModel();
                        tableModel.insertRow(0, new Object[]{newCalibration.getId(),newCalibration.getDate(),newCalibration.getMeasuring()});
                        newIdNumber=idCounter();
                        view.getCalibrationTxtNumber().setText(String.valueOf(newIdNumber));
                    }catch(Exception ex){
                        showMessage("Error al guardar en el archivo XML: " + ex.getMessage());
                    }
                }
            } catch (Exception ex) {
                viewController.showMessage(ex.getMessage());
            }
        }
        if (e.getSource().equals(view.getCalibrationBtnClean())) {
            view.getCalibrationDateChooser().setDate(null);
            view.getCalibrationTxtMeasurement().setText("");
        }
        if (e.getSource().equals(view.getBtnSearchCalibration())) {
            DefaultTableModel model = XMLLoader.loadAndFilterXMLData(filePath, view.getTxtNumberSearch().getText());
            view.getTblCalibrations().setModel(model);
        }
    }
}
