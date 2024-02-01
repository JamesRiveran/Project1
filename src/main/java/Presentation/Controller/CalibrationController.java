
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentation.Controller;

import static Presentation.Controller.ViewController.proxy;
import static Presentation.Controller.ViewController.user;
import Presentation.Model.Calibration;
import Presentation.Model.CalibrationList;
import Presentation.Model.ColorCelda;
import Presentation.Model.GeneratorPDF;
import static Presentation.Model.GeneratorPDF.loadCalibration;
import Presentation.Model.Measurement;
import Presentation.View.Modulo;
import Protocol.Message;
import com.itextpdf.text.DocumentException;
import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author james
 */
public class CalibrationController extends Controller implements InstruSelectionListener {

    String serie;
    String min;
    String max;
    String tolerancia;
    Boolean pass = false;
    CalibrationList calibrationList;
    ViewController viewController;
    Modulo view;
    private static ArrayList<Calibration> listCalibrations;
    private static ArrayList<Measurement> loadedMeasurements;
    private String number;
    public String serieInstrument = "";
    boolean update = false;
    Color colorOriginal;
    String default_label;
    int confirmResult;

    Timer timer;

    public static String get_Id = "";


    public CalibrationController(Modulo views) {
        this.view = views;
        view.getCalibrationBtnDelete().setEnabled(false);
        colorOriginal = view.getCalibrationBtnDelete().getBackground();
        this.view.setCalibrationController(this);
        this.calibrationList = new CalibrationList();
        this.number = "0";
        default_label = view.getLbNombreInstru().getText();
        timer = new Timer();
        clickTable();
    }

    // Constructor con argumentos, incluyendo la serie
    public CalibrationController(Modulo view, String serie, String max, boolean pass) {

        this.view = view;
        this.view.setCalibrationController(this);
        this.calibrationList = new CalibrationList();
        this.serie = serie; // Asigna la serie recibida
        this.max = max;
        clickTable();

    }

    public CalibrationController(boolean enter) {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void tab() {
        getInformation();
    }

    public static void getInformation() {
        Message msg = new Message();
        msg.setCalibration(listCalibrations);
        msg.setMeasure(loadedMeasurements);
        msg.setId(get_Id);
        msg.setSender(user);
        proxy.getInformationModulo3(msg);
    }

    public static void saveMeasure(String id_measure, String reference, String reading, String idCalibration) {
        Message msg = new Message();
        String[] datos = {id_measure, reference, reading, idCalibration};
        msg.setData(datos);
        msg.setSender(user);
        proxy.saveMeasure(msg);
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
                    calibrationList.getList().clear();
                    JDateChooser dateChooser = view.getCalibrationDateChooser();
                    String date = dateToString(dateChooser);
                    Calibration newCalibration = new Calibration(
                            serie,
                            Integer.parseInt(view.getCalibrationTxtNumber().getText()),
                            date,
                            Integer.parseInt(view.getCalibrationTxtMeasurement().getText()));
                    calibrationList.getList().add(newCalibration);

                    
                        informationCalibration(calibrationList.getList(), serie);
                        getInformation();
                        List<Measurement> measurements = generateMeasurements(Integer.parseInt(view.getCalibrationTxtMeasurement().getText()), Integer.parseInt(max), Integer.parseInt(min));
                        informationMeasurement(measurements);
                        ViewController.showMessage(view,"Todas las mediciones fueron insertadas correctamente", "success");
                        getInformation();
                        
                        clean();


                } catch (Exception ex) {
                    viewController.showMessage(view, "Error al guardar en el archivo XML: " + ex.getMessage(), "error");
                }
            }
        } catch (Exception ex) {
            viewController.showMessage(view, ex.getMessage(), "error");
        }
    }

    public void informationCalibration(ArrayList<Calibration> calibrationList, String serie) {
        for (Calibration cali : calibrationList) {
            saveCalibration(cali.getId(), cali.getDate(), cali.getMeasuring(), serie);
        }
    }

    public void informationMeasurement(List<Measurement> measurementList) {
        for (Measurement measurement : measurementList) {
            saveMeasurement(measurement.getId(), measurement.getReference(), measurement.getReading(), Integer.parseInt(measurement.getCode()));
        }
    }

    public void saveCalibration(int number, String date, int measurement, String serie) {
        Message msg = new Message();
        String[] dataCalibration = {String.valueOf(number), date, String.valueOf(measurement), serie};
        msg.setDataCalibration(dataCalibration);
        msg.setSender(user);
        proxy.saveCalibration(msg);
    }

    public void saveMeasurement(int medida, int reference, String reading, int numberSerie) {
        Message msg = new Message();
        String[] dataMeasurement = {String.valueOf(medida), String.valueOf(reference), reading, String.valueOf(numberSerie)};
        msg.setData(dataMeasurement);
        msg.setSender(user);
        proxy.saveMeasurement(msg);
    }

    @Override
    public void clean() {
        update = false;
        DefaultTableModel tableModel = (DefaultTableModel) view.getTblMeasurement().getModel();
        view.getCalibrationBtnDelete().setEnabled(false);
        view.getCalibrationBtnDelete().setBackground(colorOriginal);
        view.getCalibrationDateChooser().setDate(null);
        view.getCalibrationTxtMeasurement().setText("");
        view.getCalibrationDateChooser().setEnabled(true);
        view.getCalibrationTxtMeasurement().setEnabled(true);
//        String id = "";
//        id = String.valueOf(data_logic.getId());
//        view.getCalibrationTxtNumber().setText(id);
        getInformation();
        view.getCalibrationTxtNumber().setText(get_Id);
        clearTable(tableModel);
        updateTable();
    }

    @Override
    public void search() {
        if (pass == true) {
            String searchNumber = view.getTxtNumberSearch().getText();
            filterByNumber(searchNumber);
        }

    }

    @Override
    public void reportPdf() {
        try {
            ArrayList<Calibration> calibrationList = new ArrayList<>();
            ArrayList<Measurement> measurementList = new ArrayList<>();
            measurementList = loadedMeasurements;
            calibrationList.clear();
            calibrationList = loadCalibration(view.getTblCalibrations());
            String pdfFilePath = "Reporte_Calibraciones.pdf";
            GeneratorPDF.generatePDFReportForMeasurementsAndCalibration(calibrationList, measurementList, pdfFilePath, "modulo_3");
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
        int contadorFilasEnColumna = 0;
        int tolerance = Integer.parseInt(tolerancia);

        DefaultTableModel modelo = (DefaultTableModel) view.getTblMeasurement().getModel();
        int rowCount = modelo.getRowCount();
        List<String> datosColumna = new ArrayList<>();
        List<String> datosColumnaId = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            Object valorCelda = modelo.getValueAt(i, columna2);
            if (valorCelda != null) {
                contadorFilasEnColumna++;
            }
        }

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

                    final int finalFila = fila;

                    final ColorCelda colorCelda = new ColorCelda(columna2, finalFila, tolerance, reference);

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            view.getTblMeasurement().getColumnModel().getColumn(columna2).setCellRenderer(colorCelda);
                            modelo.fireTableCellUpdated(finalFila, columna2);
                        }
                    });
                }
                DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
                datosColumna.add(textoCelda);
                datosColumnaId.add(textoCelda0);

                updateReading(datosColumna, datosColumnaId, textoCelda3);
                view.getTblMeasurement().getColumnModel().getColumn(columna2).setCellRenderer(defaultRenderer);

                if ((rowCount - 1) == fila) {
                    viewController.showMessage(view, "Guardados con exito", "success");
                    viewController.showMessage(view, "Se guardaron lecturas fuera de rango inexactas, las celdas están en rojo", "error");
                }

            } else {
                if (fila >= (rowCount - contadorFilasEnColumna) && fila < rowCount) {
                    viewController.showMessage(view, "Sin lecturas registradas", "error");
                }
                break;
            }
        }

    }

    public void updateReading(List<String> readings, List<String> id, String idToUpdate) {
        Message msg = new Message();

        msg.setReading(readings);
        msg.setNewId(id);
        msg.setIdToUpdate(idToUpdate);

        msg.setSender(user);

        proxy.saveReading(msg);
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

    public List<Measurement> generateMeasurements(int numMeasurements, int maxValue, int minValue) {

        if (numMeasurements <= 0 || maxValue <= 0) {
            throw new IllegalArgumentException("La cantidad de mediciones y el valor máximo deben ser mayores que cero.");
        }
        List<Measurement> measurements = new ArrayList<>();
        int step = maxValue / numMeasurements;
        int currentReference = minValue;
        int newIdMedicion = 0;
        for (int i = 1; i <= numMeasurements; i++) {
            int medida = i;
            int referencia = currentReference;
            String lectura = ""; // Inicializar lectura como 0
            newIdMedicion += 1;

            Measurement measurement = new Measurement(view.getCalibrationTxtNumber().getText(), medida, referencia, lectura, newIdMedicion);
            measurements.add(measurement);

            // Actualizar la referencia para la siguiente medición
            currentReference += step;
        }

        return measurements;
    }

    public boolean clickTable() {
        update = true;
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
                        view.getCalibrationBtnDelete().setEnabled(true);
                        view.getCalibrationBtnDelete().setBackground(Color.RED);

                        updateTableMeasurement();

                    }
                }
            }
        });
        return true;
    }

    public void updateTableMeasurement() {
        getInformation();
        DefaultTableModel tableModel = (DefaultTableModel) view.getTblMeasurement().getModel();
        tableModel.setRowCount(0);
        int id = Integer.parseInt(view.getCalibrationTxtNumber().getText());

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
        DefaultTableModel tableModel = (DefaultTableModel) view.getTblCalibrations().getModel();
        tableModel.setRowCount(0);
        System.out.println("Esto es calibration list " + listCalibrations);
        for (int i = listCalibrations.size() - 1; i >= 0; i--) {
            Calibration newCalibration = listCalibrations.get(i);
            if (newCalibration.getNumber().equals(serie)) {
                tableModel.insertRow(0, new Object[]{newCalibration.getId(), newCalibration.getDate(), newCalibration.getMeasuring()});
            }
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

        confirmResult = JOptionPane.showConfirmDialog(view, "Corrabora si no hay mediciones importantes hechas!\nSe borrarán las mediciones de esta calibración.\n¿Deseas continuar?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmResult == JOptionPane.YES_OPTION) {
            DefaultTableModel tableModel = (DefaultTableModel) view.getTblMeasurement().getModel();
            int number = Integer.parseInt(view.getCalibrationTxtNumber().getText());

            deleteMeasurement(number);
            deleteCalibration(number);
            getInformation();
            pass = true;

            clearTable(tableModel);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    clean();
                }
            }, 1000);
        }
    }

    public static void deleteMeasurement(int number) {
        Message msg = new Message();
        msg.setMessage(String.valueOf(number));
        msg.setSender(user);
        proxy.deleteMeasurement(msg);
    }

    public static void deleteCalibration(int number) {
        Message msg = new Message();
        msg.setMessage(String.valueOf(number));
        msg.setSender(user);
        proxy.deleteCalibration(msg);
    }

    @Override
    public void onInstruSelected(String serie, String tolerancia, String descri, String mini, String max, String simbol, boolean pass) {
        this.pass = pass;
        if (pass == false) {
            view.getLbNombreInstru().setText(default_label);
            DefaultTableModel tableModel = (DefaultTableModel) view.getTblCalibrations().getModel();
            tableModel.setRowCount(0);
        } else {
            view.getLbNombreInstru().setText(serie + " - " + descri + " (" + mini + " " + simbol + " a " + max + " " + simbol + "),  " + "Tolerancia: " + tolerancia);
            this.serie = serie;
            this.tolerancia = tolerancia;
            this.max = max;
            this.min = mini;

            setSerieInstrument(serie);

            CalibrationController cali = new CalibrationController(this.view, serie, max, pass);

            DefaultTableModel tableModel = (DefaultTableModel) view.getTblCalibrations().getModel();
            tableModel.setRowCount(0);

            for (Calibration calibracion : listCalibrations) {
                if (String.valueOf(calibracion.getNumber()).equals(serie)) {
                    System.out.println(calibracion.toString());
                    Object[] rowData = {calibracion.getId(), calibracion.getDate(), calibracion.getMeasuring()};
                    tableModel.addRow(rowData);
                }
            }
            DefaultTableModel tableModels = (DefaultTableModel) view.getTblMeasurement().getModel();
            clearTable(tableModels);
        }

    }

    public void tableCalibrations() {
        DefaultTableModel tableModel = (DefaultTableModel) view.getTblCalibrations().getModel();
        tableModel.setRowCount(0);
        getInformation();
        for (Calibration calibracion : listCalibrations) {
            if (String.valueOf(calibracion.getNumber()).equals(serie)) {
                System.out.println(calibracion.toString());
                Object[] rowData = {calibracion.getId(), calibracion.getDate(), calibracion.getMeasuring()};
                tableModel.addRow(rowData);
            }
        }
        DefaultTableModel tableModels = (DefaultTableModel) view.getTblMeasurement().getModel();
        clearTable(tableModels);
    }

    public void deliver(Message message) {
        System.out.println("Esto ya esta en el controller se guardo clibration " + message.getCalibration());
        listCalibrations = message.getCalibration();
        loadedMeasurements = message.getMeasure();
        get_Id = message.getId();
        if (listCalibrations == null || loadedMeasurements == null) {
            System.err.println("estan null");
        } else {
            if (pass) {
                updateTable();
            }
        }
//    
    }

}
