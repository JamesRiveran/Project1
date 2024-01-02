
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.Calibration;
import Model.CalibrationList;
import Model.ColorCelda;
import Model.GeneratorPDF;
import Model.Measurement;
import Model.XMLLoader;
import View.Modulo;
import com.itextpdf.text.DocumentException;
import com.toedter.calendar.JDateChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jdom2.Element;
import org.jdom2.JDOMException;

/**
 *
 * @author james
 */
public class CalibrationController extends Controller implements ActionListener, InstruSelectionListener {

    String serie;
    int min;
    String max;
    String tolerancia;
    CalibrationList calibrationList;
    ViewController viewController;
    Modulo view;
    String filePath = "Laboratorio.xml";
    private ArrayList<Calibration> listCalibrations;
    boolean updateInstruments = false;
    boolean pass = false;
    private String number;

    public CalibrationController(Modulo view) {
        this.view = view;
        this.view.setCalibrationController(this);
        this.calibrationList = new CalibrationList();
        this.number = "0";
    }

    // Constructor con argumentos, incluyendo la serie
    public CalibrationController(Modulo view, String serie, String max, boolean pass) {
        this.view = view;
        this.view.setCalibrationController(this);
        this.calibrationList = new CalibrationList();
        this.serie = serie; // Asigna la serie recibida
        this.max = max;
        clickTable();
        updateTable();

    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public void save() {
        try {
            int measurement = Integer.parseInt(view.getCalibrationTxtMeasurement().getText());
            if (view.getCalibrationDateChooser().getDate() == null) {
                showMessage("Debe ingresar la fecha del instrumento", "error");
            } else if (view.getCalibrationTxtMeasurement().getText().trim().isEmpty()) {
                showMessage("Debe ingresar la calibración del instrumento", "error");
            } else if (Integer.parseInt(view.getCalibrationTxtMeasurement().getText()) < 2) {
                showMessage("La cantidad minima de mediciones que se permite ingresar es de 2", "error");
            } else if (measurement > Integer.parseInt(max) + 1) {
                showMessage("La cantidad de mediciones es mayor a la cantidad de enteros que hay en el rango del instrumento", "error");
            } else {
                try {
                    int newIdNumber = 0;
                    calibrationList.getList().clear();
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
        view.getCalibrationDateChooser().setEnabled(true);
        view.getCalibrationTxtMeasurement().setEnabled(true);
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

        int columna2 = 2;  // Columna 2
        int columna3 = 3;  // Columna 3
        int columnaReference = 1;
        DefaultTableModel modelo = (DefaultTableModel) view.getTblMeasurement().getModel();
        int rowCount = modelo.getRowCount();
        List<String> datosColumna = new ArrayList<>();

        double tolerance = Double.parseDouble(tolerancia);
        for (int fila = 0; fila < rowCount; fila++) {

            Object valorCelda = modelo.getValueAt(fila, columna2);
            Object valueReference = modelo.getValueAt(fila, columnaReference);
            String textoCelda = (valorCelda != null) ? valorCelda.toString() : "";
            Double integerObject = (double) valueReference;
            int reference = integerObject.intValue();

            double validation = (double) reference + tolerance;
            double validationFew = (double) reference - tolerance;

            double intTextoCelda = Double.parseDouble(textoCelda);

            Object valorCelda3 = modelo.getValueAt(fila, columna3);
            String textoCelda3 = (valorCelda3 != null) ? valorCelda3.toString() : "";

            if (intTextoCelda > validation || intTextoCelda < validationFew) {

                showMessage("Lectura fuera de rango, ingrese otra lectura", "error");
                ColorCelda colorCelda = new ColorCelda(columna2, tolerance, integerObject);
                view.getTblMeasurement().getColumnModel().getColumn(columna2).setCellRenderer(colorCelda);
                break;

            } else {
                DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
                datosColumna.add(textoCelda);
                datosColumna.add(textoCelda3);
                XMLLoader.updateMeasurement(filePath, datosColumna);
                view.getTblMeasurement().getColumnModel().getColumn(columna2).setCellRenderer(defaultRenderer);
                showMessage("Guardados con exito", "success");
            }

        }
    }

    public void cleanMeasurement() {

        int columna = 2;  // El número de la columna que deseas limpiar
        DefaultTableModel modelo = (DefaultTableModel) view.getTblMeasurement().getModel();
        int rowCount = modelo.getRowCount();

        for (int fila = 0; fila < rowCount; fila++) {
            modelo.setValueAt(0.0, fila, columna); // Establece un valor vacío en la celda
        }
    }
    //
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

    private int idMedicion() throws JDOMException, IOException {
        int idMedicion = XMLLoader.getIdMedicionFromXML(filePath);
        return idMedicion;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public List<Measurement> generateMeasurements(int numMeasurements, double maxValue) throws JDOMException, IOException {
        if (numMeasurements <= 0 || maxValue <= 0) {
            throw new IllegalArgumentException("La cantidad de mediciones y el valor máximo deben ser mayores que cero.");
        }

        List<Measurement> measurements = new ArrayList<>();
        double step = maxValue / numMeasurements;
        double currentReference = 0.0;
        int newIdMedicion = 0;
        for (int i = 1; i <= numMeasurements; i++) {
            double medida = i;
            double referencia = currentReference;
            double lectura = 0.0; // Inicializar lectura como 0
            newIdMedicion = idMedicion();

            Measurement measurement = new Measurement(view.getCalibrationTxtNumber().getText(), medida, referencia, lectura, newIdMedicion);
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
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                            try {
                                Date date = dateFormat.parse(dateString);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                view.getCalibrationDateChooser().setCalendar(calendar);
                            } catch (ParseException parseException) {
                                parseException.printStackTrace();
                            }
                        } else {
                            System.out.println("El objeto no es de tipo String");
                        }

                        setNumber(id.toString());
                        view.getCalibrationTxtMeasurement().setText(measurement.toString());
                        view.getCalibrationTxtNumber().setEnabled(false);
                        view.getCalibrationDateChooser().setEnabled(false);
                        view.getCalibrationTxtMeasurement().setEnabled(false);
                        updateTableMeasurement();

                    }
                }
            }
        });
        return true;
    }

    public void updateTableMeasurement() {
        DefaultTableModel tableModel = (DefaultTableModel) view.getTblMeasurement().getModel();
        tableModel.setRowCount(0); // Limpia la tabla antes de cargar los datos
        try {
            ArrayList<Measurement> loadedMeasurements = XMLLoader.loadFromMeasurement(filePath);
            for (Measurement measurement : loadedMeasurements) {
                if (Integer.parseInt(measurement.getCode()) == Integer.parseInt(getNumber())) {
                    Object[] rowData = {measurement.getId(), measurement.getReference(), measurement.getReading(), measurement.getIdMeasure()};
                    tableModel.addRow(rowData);
                }
            }

        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }
    }

    public void clearTable(DefaultTableModel tableModel) {
        int rowCount = tableModel.getRowCount();

        // Elimina todas las filas de la tabla
        for (int i = rowCount - 1; i >= 0; i--) {
            tableModel.removeRow(i);
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

            clickTable();

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
            clickTable();

        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void delete() {
        DefaultTableModel tableModel = (DefaultTableModel) view.getTblMeasurement().getModel();
        XMLLoader.deleteDataMensu(filePath, getNumber());
        XMLLoader.deleteData(filePath, getNumber());
        showMessage("Eliminado con exito", "success");
        updateTable();
        clearTable(tableModel);
    }

    @Override
    public void onInstruSelected(String serie, String tolerancia, String descri, String mini, String max, boolean pass) {
        view.getLbNombreInstru().setText(serie + "-" + "Descripción: " + descri + ", Mínimo: " + mini + ", Máximo: " + max + ", Tolerancia: " + tolerancia);
        this.serie = serie;
        this.tolerancia = tolerancia;
        this.max = max;
        this.pass = true;

        CalibrationController cali = new CalibrationController(this.view, serie, max, pass);
        List<Element> calibracionesEncontradas = XMLLoader.findCalibrationsByNumber(filePath, serie);
        DefaultTableModel tableModel = (DefaultTableModel) view.getTblCalibrations().getModel();
        tableModel.setRowCount(0);

        for (Element calibracion : calibracionesEncontradas) {
            String id = calibracion.getChildText("Numero"); // Cambia "Id" al nombre correcto
            String date = calibracion.getChildText("Fecha"); // Cambia "Referencia" al nombre correcto
            String measurement = calibracion.getChildText("Mediciones");
            Object[] rowData = {id, date, measurement};
            tableModel.addRow(rowData);
        }
        DefaultTableModel tableModels = (DefaultTableModel) view.getTblMeasurement().getModel();
        clearTable(tableModels);

    }

}
