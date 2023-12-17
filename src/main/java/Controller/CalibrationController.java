
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.Calibration;
import Model.CalibrationList;
import Model.GeneratorPDF;
import Model.InstrumentType;
import Model.Measurement;
import Model.XMLLoader;
import View.Modulo;
import com.itextpdf.text.DocumentException;
import com.toedter.calendar.JDateChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private ArrayList<Calibration> listCalibrations;
   
    public CalibrationController() {
        this.calibrationList = new CalibrationList();
        this.view = new Modulo();
        this.view.setCalibrationController(this);
        updateTable();
    }
    
    public void startCalibration() throws JDOMException, IOException{
        view.getCalibrationBtnDelete().addActionListener(this);
        view.getCalibrationBtnSave().addActionListener(this);
        view.getCalibrationBtnClean().addActionListener(this);
        view.getBtnPDFCalibration().addActionListener(this);
        view.getBtnSearchCalibration().addActionListener(this);
        view.getBtnSaveMeasurement().addActionListener(this);
        view.getBtnCleanMeasurement().addActionListener(this);
        view.setLocationRelativeTo(null);
        view.setVisible(true);
        XMLLoader.ensureIdCounterExists(filePath);
        int idCounter = idCounter();
        view.getCalibrationTxtNumber().setText(String.valueOf(idCounter));
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
        //Guardar///
        if (e.getSource().equals(view.getCalibrationBtnSave())){
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
                        Calibration newCalibration = new Calibration(
                                Integer.parseInt(view.getCalibrationTxtNumber().getText()), 
                                date,
                                Integer.parseInt(view.getCalibrationTxtMeasurement().getText()));
                        calibrationList.getList().add(newCalibration);
                        XMLLoader.saveToXMLCalibration(filePath, calibrationList.getList());
                        DefaultTableModel tableModel = (DefaultTableModel) view.getTblCalibrations().getModel();
                        tableModel.insertRow(0, new Object[]{newCalibration.getId(),newCalibration.getDate(),newCalibration.getMeasuring()});
                        newIdNumber=idCounter();
                        view.getCalibrationTxtNumber().setText(String.valueOf(newIdNumber));
                        int max=90; //Cambiar por valor del instrumento
                        List<Measurement> measurements = generateMeasurements(measurement, max);
                        XMLLoader.saveToXMLMeasurement(filePath, measurements);
                        updateTableMeasurement();
                    }catch(Exception ex){
                        showMessage("Error al guardar en el archivo XML: " + ex.getMessage());
                    }
                }
            } catch (Exception ex) {
                viewController.showMessage(ex.getMessage(),"error");
            }
        }
        //Limpiar
        if (e.getSource().equals(view.getCalibrationBtnClean())) {
            view.getCalibrationDateChooser().setDate(null);
            view.getCalibrationTxtMeasurement().setText("");
        }
        //Buscar
        if (e.getSource().equals(view.getBtnSearchCalibration())) {
            String searchNumber = view.getTxtNumberSearch().getText();
            filterByNumber(searchNumber);
        }
        //Reporte
        if (e.getSource().equals(view.getBtnPDFCalibration())) {
            try {
                ArrayList<Calibration> calibrationList = XMLLoader.loadFromCalibrations(filePath);
                String pdfFilePath = "Reporte_Calibraciones.pdf";
                GeneratorPDF.generatePDFReport(calibrationList, pdfFilePath, "modulo_3");
            } catch (IOException ex) {
                Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JDOMException ex) {
                Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Guardar lectura
        if (e.getSource().equals(view.getBtnSaveMeasurement())) {
            int columna = 2;
            DefaultTableModel modelo = (DefaultTableModel) view.getTblMeasurement().getModel();
            int rowCount = modelo.getRowCount();
            List<String> datosColumna = new ArrayList<>();

            for (int fila = 0; fila < rowCount; fila++) {
                Object valorCelda = modelo.getValueAt(fila, columna);
                String textoCelda = (valorCelda != null) ? valorCelda.toString() : "";
                datosColumna.add(textoCelda);
            }
            XMLLoader.updateMeasurement(filePath, datosColumna);
        }
        //Limpiar lectura
        if (e.getSource().equals(view.getBtnCleanMeasurement())) {
            int columna = 2;  // El número de la columna que deseas limpiar
            DefaultTableModel modelo = (DefaultTableModel) view.getTblMeasurement().getModel();
            int rowCount = modelo.getRowCount();

            for (int fila = 0; fila < rowCount; fila++) {
                modelo.setValueAt("", fila, columna); // Establece un valor vacío en la celda
            }
        }

        

    }

    public List<Measurement> generateMeasurements(int numMeasurements, double maxValue) {
        if (numMeasurements <= 0 || maxValue <= 0) {
        throw new IllegalArgumentException("La cantidad de mediciones y el valor máximo deben ser mayores que cero.");
    }

    List<Measurement> measurements = new ArrayList<>();
    double step = maxValue / numMeasurements;
    double currentReference = 0.0;

    for (int i = 1; i <= numMeasurements; i++) {
        double medida = i;
        double referencia = currentReference;
        double lectura = 0.0; // Inicializar lectura como 0

        Measurement measurement = new Measurement(medida, referencia, lectura);
        measurements.add(measurement);

        // Actualizar la referencia para la siguiente medición
        currentReference += step;
    }

    return measurements;
    }

 
    public void updateTableMeasurement() {
        DefaultTableModel tableModel = (DefaultTableModel) view.getTblMeasurement().getModel();
        tableModel.setRowCount(0); // Limpia la tabla antes de cargar los datos

        try {
            ArrayList<Measurement> loadedMeasurements = XMLLoader.loadFromMeasurement(filePath);

            for (Measurement measurement : loadedMeasurements) {
                Object[] rowData = {measurement.getId(), measurement.getReference(), measurement.getReading()};
                tableModel.addRow(rowData);
            }
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }
    }

    
    public void updateTable() {
        try {
            listCalibrations = XMLLoader.loadFromCalibrations(filePath);
            DefaultTableModel tableModel = (DefaultTableModel) view.getTblCalibrations().getModel();
            tableModel.setRowCount(0);
            for (int i = listCalibrations.size() - 1; i >= 0; i--) {
                Calibration newCalibration = listCalibrations.get(i);
                tableModel.insertRow(0, new Object[]{newCalibration.getId(),newCalibration.getDate(), newCalibration.getMeasuring()});
            }
        } catch (IOException | JDOMException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void filterByNumber(String searchNumber) {
        try {
            listCalibrations = XMLLoader.loadFromCalibrations(filePath);
            DefaultTableModel tableModel = (DefaultTableModel) view.getTblCalibrations().getModel();
            tableModel.setRowCount(0);
            // Si la cadena de búsqueda está vacía, muestra todos los elementos
            if (searchNumber.isEmpty()) {
                updateTable();
            } else {
                // Itera sobre la lista de instrumentos y agrega las coincidencias al modelo de la tabla
                for (Calibration calibration : listCalibrations) {
                    String idForSearch = String.valueOf(calibration.getId());
                    if (idForSearch.contains(searchNumber)) {
                        tableModel.addRow(new Object[]{calibration.getId(),calibration.getDate(),calibration.getMeasuring()});
                    }
                }
            }
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }
    }

}
