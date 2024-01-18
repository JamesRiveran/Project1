
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Controller.sqlServer.BDCalibration;
import Controller.sqlServer.BDMeasurement;
import Model.Calibration;
import Model.CalibrationList;
import Model.ColorCelda;
import Model.GeneratorPDF;
import static Model.GeneratorPDF.loadCalibration;
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
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

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
    public String serieInstrument = "";
    BDCalibration calibration = new BDCalibration();
    BDMeasurement measurement = new BDMeasurement();

    public CalibrationController(Modulo view) {
        this.view = view;
        this.calibration = new BDCalibration();
        this.view.setCalibrationController(this);
        this.calibrationList = new CalibrationList();
        this.measurement = new BDMeasurement();
        this.number = "0";
        updateTable();

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
            int measurementNumber = Integer.parseInt(view.getCalibrationTxtMeasurement().getText());
            if (view.getCalibrationDateChooser().getDate() == null) {
                viewController.showMessage(view, "Debe ingresar la fecha del instrumento", "error");
            } else if (view.getCalibrationTxtMeasurement().getText().trim().isEmpty()) {
                viewController.showMessage(view, "Debe ingresar la calibración del instrumento", "error");
            } else if (Integer.parseInt(view.getCalibrationTxtMeasurement().getText()) < 2) {
                viewController.showMessage(view, "La cantidad minima de mediciones que se permite ingresar es de 2", "error");
            } else if (measurementNumber > Integer.parseInt(max) + 1) {
                viewController.showMessage(view, "La cantidad de mediciones es mayor a la cantidad de enteros que hay en el rango del instrumento", "error");
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
                    XMLLoader.saveToXMLCalibration(filePath, calibrationList.getList(), serie);
                    calibration.saveCalibration(Integer.parseInt(view.getCalibrationTxtNumber().getText()), date, Integer.parseInt(view.getCalibrationTxtMeasurement().getText()), serie);
                    updateTable();
                    List<Measurement> measurements = generateMeasurements(Integer.parseInt(view.getCalibrationTxtMeasurement().getText()), Integer.parseInt(max));
                    XMLLoader.saveToXMLMeasurement(filePath, measurements, Integer.parseInt(view.getCalibrationTxtNumber().getText()));
                    measurement.saveMeasurement(measurements);
                    newIdNumber = idCounter();
                    view.getCalibrationTxtNumber().setText(String.valueOf(newIdNumber));
                    viewController.showMessage(view, "Se guardo con exito", "success");
                    clean();
                } catch (Exception ex) {
                    viewController.showMessage(view, "Error al guardar en el archivo XML: " + ex.getMessage(), "error");
                }
            }
        } catch (Exception ex) {
            viewController.showMessage(view, ex.getMessage(), "error");
        }
    }

    @Override
    public void clean() {
        view.getCalibrationDateChooser().setDate(null);
        view.getCalibrationTxtMeasurement().setText("");
        view.getCalibrationDateChooser().setEnabled(true);
        view.getCalibrationTxtMeasurement().setEnabled(true);
        String id = null;
        id = String.valueOf(XMLLoader.getIdCounter(filePath));
        view.getCalibrationTxtNumber().setText(id);
    }

    @Override
    public void search() {
        String searchNumber = view.getTxtNumberSearch().getText();
        filterByNumber(searchNumber);
    }

    @Override
    public void reportPdf() {
        try {
            ArrayList<Calibration> calibrationList = new ArrayList<>();
            calibrationList.clear();
            calibrationList = loadCalibration(view.getTblCalibrations());
            String pdfFilePath = "Reporte_Calibraciones.pdf";
            GeneratorPDF.generatePDFReport(calibrationList, pdfFilePath, "modulo_3");
            viewController.showMessage(view, "Generado con exito", "success");
        } catch (IOException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            System.out.println(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void saveMeasurement() {

        int columna0 = 0;
        int columna2 = 2;  // Columna 2
        int columna3 = 3;  // Columna 3
        int columnaReference = 1;
        DefaultTableModel modelo = (DefaultTableModel) view.getTblMeasurement().getModel();
        int rowCount = modelo.getRowCount();
        List<String> datosColumna = new ArrayList<>();
        List<String> datosColumnaId = new ArrayList<>();

        int tolerance = Integer.parseInt(tolerancia);
        for (int fila = 0; fila < rowCount; fila++) {

            Object valorCelda0 = modelo.getValueAt(fila, columna0);
            String textoCelda0 = (valorCelda0 != null) ? valorCelda0.toString() : "";

            Object valorCelda = modelo.getValueAt(fila, columna2);
            Object valueReference = modelo.getValueAt(fila, columnaReference);
            String textoCelda = (valorCelda != null) ? valorCelda.toString() : "";

            if (!textoCelda.trim().isEmpty() && isNumeric(textoCelda)) {
                int reference = (int) valueReference;

                int validation = (int) reference + tolerance;
                int validationFew = (int) reference - tolerance;

                int intTextoCelda = Integer.parseInt(textoCelda);
                Object valorCelda3 = modelo.getValueAt(fila, columna3);
                String textoCelda3 = (valorCelda3 != null) ? valorCelda3.toString() : "";

                if (intTextoCelda > validation || intTextoCelda < validationFew) {
                    viewController.showMessage(view, "Lectura fuera de rango, ingrese otra lectura", "error");

                    final int finalFila = fila;

                    final ColorCelda colorCelda = new ColorCelda(columna2, finalFila, tolerance, reference);

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            view.getTblMeasurement().getColumnModel().getColumn(columna2).setCellRenderer(colorCelda);
                            modelo.fireTableCellUpdated(finalFila, columna2);
                        }
                    });

                    break;
                } else {
                    DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
                    datosColumna.add(textoCelda);
                    datosColumnaId.add(textoCelda0);
                    System.out.println(textoCelda3);
                    System.out.println(textoCelda3);
                    System.out.println(textoCelda);

                    measurement.updateReading(datosColumna, datosColumnaId, textoCelda3);
                    view.getTblMeasurement().getColumnModel().getColumn(columna2).setCellRenderer(defaultRenderer);

                    if ((rowCount - 1) == fila) {
                        viewController.showMessage(view, "Guardados con exito", "success");
                    }

                }

            } else {
                if ((rowCount - 1) == fila || (rowCount - 2) == fila || (rowCount - 3) == fila) {
                    viewController.showMessage(view, "Sin lecturas registradas", "error");
                }
                break;
            }
        }

    }

    public void cleanMeasurement() {

        int columna = 2;  // El número de la columna que deseas limpiar
        DefaultTableModel modelo = (DefaultTableModel) view.getTblMeasurement().getModel();
        int rowCount = modelo.getRowCount();

        for (int fila = 0; fila < rowCount; fila++) {
            modelo.setValueAt("", fila, columna); // Establece un valor vacío en la celda
        }
    }

    private static String dateToString(JDateChooser dateChooser) {
        Date fechaSeleccionada = dateChooser.getDate();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        return formatoFecha.format(fechaSeleccionada);
    }

    private int idCounter() throws IOException, SAXException, ParserConfigurationException {
        int idCounter = XMLLoader.getIdCounterFromXML(filePath);
        return idCounter;
    }

    private int idMedicion() throws SAXException, ParserConfigurationException, TransformerException, IOException {
        int idMedicion = XMLLoader.getIdMedicionFromXML(filePath);
        return idMedicion;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public List<Measurement> generateMeasurements(int numMeasurements, int maxValue) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        if (numMeasurements <= 0 || maxValue <= 0) {
            throw new IllegalArgumentException("La cantidad de mediciones y el valor máximo deben ser mayores que cero.");
        }

        List<Measurement> measurements = new ArrayList<>();
        int step = maxValue / numMeasurements;
        int currentReference = 0;
        int newIdMedicion = 0;
        for (int i = 1; i <= numMeasurements; i++) {
            double medida = i;
            int referencia = currentReference;
            String lectura = ""; // Inicializar lectura como 0
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
                        setNumber(id.toString());
                        if (dateObject instanceof String) {
                            String dateString = (String) dateObject;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

                        view.getCalibrationTxtMeasurement().setText(measurement.toString());
                        view.getCalibrationTxtNumber().setText(String.valueOf(id));
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
        tableModel.setRowCount(0);
        int id = Integer.parseInt(view.getCalibrationTxtNumber().getText());
        ArrayList<Measurement> loadedMeasurements = measurement.getAllMeasurement();

        for (Measurement measurements : loadedMeasurements) {
            if (Integer.parseInt(measurements.getCode()) == Integer.parseInt(getNumber())) {
                Object[] rowData = {measurements.getId(), measurements.getReference(), measurements.getReading(), measurements.getCode()};
                tableModel.addRow(rowData);
            }
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
        //listCalibrations = XMLLoader.loadFromCalibrations(filePath);
        listCalibrations = calibration.getAllCalibration();
        DefaultTableModel tableModel = (DefaultTableModel) view.getTblCalibrations().getModel();
        tableModel.setRowCount(0);
        for (int i = listCalibrations.size() - 1; i >= 0; i--) {
            Calibration newCalibration = listCalibrations.get(i);
            tableModel.insertRow(0, new Object[]{newCalibration.getId(), newCalibration.getDate(), newCalibration.getMeasuring()});
        }
        clickTable();
    }

    public String getSerieInstrument() {
        return serieInstrument;
    }

    public void setSerieInstrument(String serieInstrument) {
        this.serieInstrument = serieInstrument;
    }

    private void filterByNumber(String searchNumber) {
//        listCalibrations = XMLLoader.loadFromCalibrations(filePath);
        listCalibrations = calibration.getAllCalibration();
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
    }

    @Override
    public void delete() {
        DefaultTableModel tableModel = (DefaultTableModel) view.getTblMeasurement().getModel();
        String serie = getSerieInstrument();
        int number = Integer.parseInt(view.getCalibrationTxtNumber().getText());
        XMLLoader.delete(filePath, serie, number);
        calibration.deleteCalibration(number);
        viewController.showMessage(view, "Eliminado con exito", "success");
        updateTable();
        clearTable(tableModel);
    }

    @Override
    public void onInstruSelected(String serie, String tolerancia, String descri, String mini, String max, boolean pass) {
        view.getLbNombreInstru().setText(serie + " - " + descri + " (" + mini + "-" + max + ") - " + "Tolerancia: " + tolerancia);
        this.serie = serie;
        this.tolerancia = tolerancia;
        this.max = max;
        this.pass = true;

        setSerieInstrument(serie);

        CalibrationController cali = new CalibrationController(this.view, serie, max, pass);
        List<org.w3c.dom.Element> calibracionesEncontradas = XMLLoader.findCalibrationsByNumber(filePath, serie);
        DefaultTableModel tableModel = (DefaultTableModel) view.getTblCalibrations().getModel();
        tableModel.setRowCount(0);

        for (org.w3c.dom.Element calibracion : calibracionesEncontradas) {
            String id = calibracion.getElementsByTagName("Numero").item(0).getTextContent(); // Cambia "Id" al nombre correcto
            String date = calibracion.getElementsByTagName("Fecha").item(0).getTextContent(); // Cambia "Referencia" al nombre correcto
            String measurement = calibracion.getElementsByTagName("Mediciones").item(0).getTextContent();
            Object[] rowData = {id, date, measurement};
            tableModel.addRow(rowData);
        }
        DefaultTableModel tableModels = (DefaultTableModel) view.getTblMeasurement().getModel();
        clearTable(tableModels);
    }

}
