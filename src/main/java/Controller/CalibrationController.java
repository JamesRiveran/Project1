
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.Calibration;
import Model.CalibrationList;
import Model.GeneratorPDF;
import Model.InstrumentModulo2;
import Model.Measurement;
import Model.XMLLoader;
import View.Modulo;
import com.itextpdf.text.DocumentException;
import com.toedter.calendar.JDateChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.jdom2.JDOMException;

/**
 *
 * @author james
 */
public class CalibrationController extends Controller implements ActionListener, InstruSelectionListener {

    String serie;
    int min;
    String max;
    CalibrationList calibrationList;
    ViewController viewController;
    Modulo view;
    String filePath = "Laboratorio.xml";
    private ArrayList<Calibration> listCalibrations;
    boolean updateInstruments = false;

    public CalibrationController(Modulo view) {
        this.view = view;
        this.view.setCalibrationController(this);
        this.calibrationList = new CalibrationList();
    }

    // Constructor con argumentos, incluyendo la serie
    public CalibrationController(Modulo view, String serie, String max) {
        this.view = view;
        this.view.setCalibrationController(this);
        this.calibrationList = new CalibrationList();
        this.serie = serie; // Asigna la serie recibida
        this.max = max;
        clickTable();

        updateTable();

    }

    @Override
    public void save() {
        try {
            if (view.getCalibrationDateChooser().getDate() == null) {
                showMessage("Debe ingresar la fecha del instrumento", "error");
            } else if (view.getCalibrationTxtMeasurement().getText().trim().isEmpty()) {
                showMessage("Debe ingresar la calibración del instrumento", "error");
            } else if (Integer.parseInt(view.getCalibrationTxtMeasurement().getText()) < 2) {
                showMessage("La cantidad minima de mediciones que se permite ingresar es de 2", "error");
            } else {
                try {
                    int newIdNumber = 0;
                    JDateChooser dateChooser = view.getCalibrationDateChooser();
                    String date = dateToString(dateChooser);
                    Calibration newCalibration = new Calibration(
                            serie,
                            Integer.parseInt(view.getCalibrationTxtNumber().getText()),
                            date,
                            Integer.parseInt(view.getCalibrationTxtMeasurement().getText()));
                    calibrationList.getList().add(newCalibration);
                    XMLLoader.saveToXMLCalibration(filePath, calibrationList.getList());
                    DefaultTableModel tableModel = (DefaultTableModel) view.getTblCalibrations().getModel();
                    tableModel.insertRow(0, new Object[]{newCalibration.getId(), newCalibration.getDate(), newCalibration.getMeasuring()});
                    List<Measurement> measurements = generateMeasurements(Integer.parseInt(view.getCalibrationTxtMeasurement().getText()), Integer.parseInt(max));
                    XMLLoader.saveToXMLMeasurement(filePath, measurements);
                    newIdNumber = idCounter();
                    view.getCalibrationTxtNumber().setText(String.valueOf(newIdNumber));
                    updateTableMeasurement();
                    showMessage("Se guardo con exito", "success");
                } catch (Exception ex) {
                    showMessage("Error al guardar en el archivo XML: " + ex.getMessage(), "error");
                }
            }
        } catch (Exception ex) {
            showMessage(ex.getMessage(), "error");
        }
    }

    @Override
    public void clean() {
        view.getCalibrationDateChooser().setDate(null);
        view.getCalibrationTxtMeasurement().setText("");
    }

    @Override
    public void search() {
        String searchNumber = view.getTxtNumberSearch().getText();
        filterByNumber(searchNumber);
    }

    @Override
    public void reportPdf() {
        try {
            ArrayList<Calibration> calibrationList = XMLLoader.loadFromCalibrations(filePath);
            String pdfFilePath = "Reporte_Calibraciones.pdf";
            GeneratorPDF.generatePDFReport(calibrationList, pdfFilePath, "modulo_3");
            showMessage("Generado con exito", "success");
        } catch (IOException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveMeasurement() {
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
        showMessage("Guardados con exito", "success");
    }

    public void cleanMeasurement() {

        int columna = 2;  // El número de la columna que deseas limpiar
        DefaultTableModel modelo = (DefaultTableModel) view.getTblMeasurement().getModel();
        int rowCount = modelo.getRowCount();

        for (int fila = 0; fila < rowCount; fila++) {
            modelo.setValueAt("", fila, columna); // Establece un valor vacío en la celda
        }
    }

    public void showMessage(String errorMessage, String info) {
        if (info == "error") {
            JOptionPane.showMessageDialog(view, errorMessage, "Validación", JOptionPane.ERROR_MESSAGE);
        } else if (info == "success") {
            JOptionPane.showMessageDialog(view, errorMessage, "Validación", JOptionPane.INFORMATION_MESSAGE);

        }
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

            Measurement measurement = new Measurement(view.getCalibrationTxtNumber().getText(), medida, referencia, lectura);
            measurements.add(measurement);

            // Actualizar la referencia para la siguiente medición
            currentReference += step;
        }

        return measurements;
    }

    public boolean clickTable() {

        view.getTblCalibrations().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = view.getTblCalibrations().getSelectedRow();
                    if (selectedRow >= 0) {
                        Object id = view.getTblCalibrations().getValueAt(selectedRow, 0);
                        Object dateObject = view.getTblCalibrations().getValueAt(selectedRow, 1);
                        Object measurement = view.getTblCalibrations().getValueAt(selectedRow, 2);

                        if (dateObject instanceof String) {
                            String dateString = (String) dateObject;
                            // Utiliza el patrón correcto para parsear la cadena de fecha
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                            try {
                                // Parsear la cadena a un objeto Date
                                Date date = dateFormat.parse(dateString);
                                // Crear un objeto Calendar y establecer la fecha
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                // Ahora, 'calendar' es un objeto Calendar que contiene la fecha de 'dateObject'
                                view.getCalibrationDateChooser().setCalendar(calendar);
                            } catch (ParseException parseException) {
                                // Manejar la excepción si la cadena no puede ser parseada
                                parseException.printStackTrace();
                            }
                        } else {
                            // Manejar el caso en el que el objeto no sea de tipo String
                            System.out.println("El objeto no es de tipo String");
                        }

                        // Asigna los valores a los campos correspondientes
                        view.getCalibrationTxtNumber().setText(id.toString());
                        view.getCalibrationTxtMeasurement().setText(measurement.toString());

                        updateTableMeasurement();
                        System.out.println("ID: " + id);
//                        System.out.println("Date: " + date);
                        System.out.println("Measurement: " + measurement);
                    }
                }
            }
        });
        return true;
    }

    public void tbInstruMouseClicked(MouseEvent evt) throws ParseException {
        int selectedRow = view.getTblCalibrations().getSelectedRow();
        System.out.println(selectedRow);

        // Verifica si se hizo clic en una fila válida
        if (selectedRow != -1) {

            // Obtén los valores de la fila seleccionada
            String number = view.getTbInstru().getValueAt(selectedRow, 0).toString();
            String date = view.getTbInstru().getValueAt(selectedRow, 1).toString();
            String mean = view.getTbInstru().getValueAt(selectedRow, 2).toString();

            System.out.println(number);
            System.out.println(date);
            System.out.println(mean);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            // Asigna los valores a los campos correspondientes
            view.getCalibrationTxtNumber().setText(number);
            view.getCalibrationDateChooser().setCalendar(calendar);
            view.getCalibrationTxtMeasurement().setText(mean);

            updateTableMeasurement();
        }
    }

    public void updateTableMeasurement() {
        DefaultTableModel tableModel = (DefaultTableModel) view.getTblMeasurement().getModel();
        tableModel.setRowCount(0); // Limpia la tabla antes de cargar los datos

        try {
            listCalibrations = XMLLoader.loadFromCalibrations(filePath);

            ArrayList<Measurement> loadedMeasurements = XMLLoader.loadFromMeasurement(filePath);
            for (int i = listCalibrations.size() - 1; i >= 0; i--) {
                Calibration newCalibration = listCalibrations.get(i);

                for (Measurement measurement : loadedMeasurements) {
                    if (Integer.parseInt(measurement.getCode()) == newCalibration.getId()) {
                        Object[] rowData = {measurement.getId(), measurement.getReference(), measurement.getReading()};
                        tableModel.addRow(rowData);
                    }
                }
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
                if (newCalibration.getNumber().equals(serie)) {
                    tableModel.insertRow(0, new Object[]{newCalibration.getId(), newCalibration.getDate(), newCalibration.getMeasuring()});
                }
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
                        tableModel.addRow(new Object[]{calibration.getId(), calibration.getDate(), calibration.getMeasuring()});
                    }
                }
            }
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void delete() {
    }

    @Override
    public void onInstruSelected(String serie, String descri, String mini, String max) {
        view.getLbNombreInstru().setText(serie + "-" + "Descripción: " + descri + ", Mínimo: " + mini + ", Máximo: " + max);
        this.serie = serie;
        this.max = max;
        CalibrationController cali = new CalibrationController(this.view, serie, max);
    }

}
