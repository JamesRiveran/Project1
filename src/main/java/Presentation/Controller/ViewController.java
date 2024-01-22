
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentation.Controller;

import Data.BDCalibration;
import Logic.Data_logic;
import Logic.GeneratorPDF;
import static Logic.GeneratorPDF.loadTypeOfInstrument;
import Logic.InstrumentType;
import Logic.InstrumentsList;
import Presentation.View.Modulo;
import com.itextpdf.text.DocumentException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author james
 */
public class ViewController extends Controller implements ActionListener {

    InstrumentsList listInstrument;
    private ArrayList<InstrumentType> ListOfIModu1o1;
    CalibrationController calibrationController;
    IntrumentsController intrumentsController;
    static Modulo view;
    protected Modulo viewError;
    Data_logic data_logic;
    boolean update = false;

    public ViewController() throws ParserConfigurationException, SAXException {

        this.listInstrument = new InstrumentsList();
        this.view = new Modulo();
        this.calibrationController = new CalibrationController(this.view);
        this.intrumentsController = new IntrumentsController(this.view);
        intrumentsController.setInstruSelectionListener(calibrationController);
        this.data_logic = new Data_logic();
        clickTable();
        updateTable();
        this.view.setViewController(this);
    }

    public void start() throws IOException, SAXException, ParserConfigurationException {

        view.getBtnClean().addActionListener(e -> clean());
        view.getBtnDelete().addActionListener(e -> delete());
        view.getBtnPDF().addActionListener(e -> reportPdf());
        view.getBtnSave().addActionListener(e -> save());
        view.getBtnSearch().addActionListener(e -> search());
        view.setLocationRelativeTo(null);
        view.setVisible(true);

        /*Los del modulo 2*/
        view.getBtnSaveInstru().addActionListener(e -> intrumentsController.save());
        view.getBtnReport().addActionListener(e -> intrumentsController.reportPdf());
        view.getBtnCleanInstru().addActionListener(e -> intrumentsController.clean());
        view.getBtnDeleteInstru().addActionListener(e -> intrumentsController.delete());
        view.getBtnSearchInstru().addActionListener(e -> intrumentsController.search());

        /*modulo 3*/
        view.getCalibrationBtnDelete().addActionListener(this);
        view.getCalibrationBtnSave().addActionListener(e -> calibrationController.save());
        view.getCalibrationBtnClean().addActionListener(e -> calibrationController.clean());
        view.getBtnPDFCalibration().addActionListener(e -> calibrationController.reportPdf());
        view.getBtnSearchCalibration().addActionListener(e -> calibrationController.search());
        view.getBtnSaveMeasurement().addActionListener(e -> calibrationController.saveMeasurement());
        view.getBtnCleanMeasurement().addActionListener(e -> calibrationController.cleanMeasurement());

        view.getCalibrationTxtNumber().setText(String.valueOf(data_logic.getId()));
        view.getCalibrationTxtNumber().setEnabled(false);
        view.getCalibrationBtnDelete().addActionListener(e -> calibrationController.delete());

    }

    public static void showMessage(JFrame parent, String message, String info) {
        if (info == "error") {
            JOptionPane.showMessageDialog(parent, message, "Validación", JOptionPane.ERROR_MESSAGE);
        } else if (info == "success") {
            JOptionPane.showMessageDialog(view, message, "Validación", JOptionPane.INFORMATION_MESSAGE);

        }
    }

    public static void conection(boolean m) {
        if (m) {
            modalForRelations("Posee una relacion no se puede borrar", "error");
        } else {
            modalForRelations("Borrado exitosamente", "success");
        }
    }

    public static void modalForRelations(String errorMessage, String info) {
        if (info == "error") {
            JOptionPane.showMessageDialog(view, errorMessage, "Validación", JOptionPane.ERROR_MESSAGE);
        } else if (info == "success") {
            JOptionPane.showMessageDialog(view, errorMessage, "Validación", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void save() {
        try {
            if (view.getTxtCode().getText().trim().isEmpty()) {
                showMessage(viewError, "Debe ingresar el código del instrumento", "error");
            } else if (view.getTxtName().getText().trim().isEmpty()) {
                showMessage(viewError, "Debe ingresar todos los espacios", "error");
            } else if (view.getTxtUnit().getText().trim().isEmpty()) {
                showMessage(viewError, "Debe ingresar la unidad de medida del instrumento", "error");
            } else {
                try {
                    // Actualizar <Instrumento>
                    String code = view.getTxtCode().getText();
                    String unit = view.getTxtUnit().getText();
                    String newName = view.getTxtName().getText();
                    InstrumentType instrument = new InstrumentType(code, unit, newName);
                    listInstrument.getList().add(instrument);
                    data_logic.saveOrUpdateInstruments(listInstrument.getList(), view,update);
                    listInstrument.getList().clear();
                    updateTable();
                    intrumentsController.updateComboBoxModel();
                    clean();
                } catch (Exception ex) {
                    showMessage(viewError, "Error al guardar en el archivo XML: " + ex.getMessage(), "error");
                }
            }
        } catch (NullPointerException ex) {
            showMessage(viewError, ex.getMessage(), "error");
        } catch (Exception ex) {
            showMessage(viewError, ex.getMessage(), "error");
        }
    }

    @Override
    public void search() {
        ArrayList<InstrumentType> loadedList = data_logic.getAllRecordsTypeInstruments();
        if (loadedList.isEmpty()) {
            showMessage(viewError, "No hay tipos de instrumentos registrados", "error");
        }
        String letterSearch = view.getTxtNameForSearch().getText();
        try {
            filterByName(letterSearch);
        } catch (IOException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete() {
        try {
            data_logic.deletedTypeInstrument(view.getTxtCode().getText(), view);
            updateTable();
            intrumentsController.updateComboBoxModel();
            clean();
        } catch (SAXException | ParserConfigurationException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void reportPdf() {
        try {
            ArrayList<InstrumentType> list = new ArrayList<>();
            list.clear();
            list = loadTypeOfInstrument(view.getTblListInstruments());
            String pdfFilePath = "Reporte_TiposDeInstrumentos.pdf";
            GeneratorPDF.generatePDFReport(list, pdfFilePath, "modulo_1");
            showMessage(viewError, "Generado con exito", "success");
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        clickTable();
    }

    public void editRegister(MouseEvent evt) {
        int rowSelected = view.getTblListInstruments().getSelectedRow();

        if (rowSelected != -1) {
            String codeName = view.getTblListInstruments().getValueAt(rowSelected, 0).toString();
            String instrumentName = view.getTblListInstruments().getValueAt(rowSelected, 1).toString();
            String unitName = view.getTblListInstruments().getValueAt(rowSelected, 2).toString();

            view.getTxtCode().setText(codeName);
            view.getTxtName().setText(instrumentName);
            view.getTxtUnit().setText(unitName);
            view.getTxtCode().setEnabled(false);
            view.getBtnDelete().setEnabled(true);
        }
    }

    private void filterByName(String letterSearch) throws IOException, ParserConfigurationException, SAXException {
        ArrayList<InstrumentType> loadedList = data_logic.getAllRecordsTypeInstruments();
        DefaultTableModel template = (DefaultTableModel) view.getTblListInstruments().getModel();
        template.setRowCount(0);
        if (letterSearch.isEmpty()) {
            for (InstrumentType instrument : loadedList) {
                template.addRow(new Object[]{instrument.getCode(), instrument.getName(), instrument.getUnit()});
            }
        } else {
            for (InstrumentType instrument : loadedList) {
                String nameInstrumentForSearch = instrument.getName().toLowerCase();
                if (nameInstrumentForSearch.contains(letterSearch.toLowerCase())) {
                    template.addRow(new Object[]{instrument.getCode(), instrument.getName(), instrument.getUnit()});
                }
            }
        }
    }

    public void updateTable() throws ParserConfigurationException, SAXException {
        ListOfIModu1o1 = data_logic.getAllRecordsTypeInstruments();
        DefaultTableModel tableModule1 = (DefaultTableModel) view.getTblListInstruments().getModel();
        tableModule1.setRowCount(0);
        for (int i = ListOfIModu1o1.size() - 1; i >= 0; i--) {
            InstrumentType module1 = ListOfIModu1o1.get(i);
            tableModule1.insertRow(0, new Object[]{module1.getCode(), module1.getName(), module1.getUnit()});
        }
    }

    public boolean clickTable() {
        //Selección de un instrumento de la tabla 
        view.getTblListInstruments().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editRegister(evt);
                update=true;
            }
        });
        return true;

    }

    @Override
    public void clean() {
        update=false;
        view.getBtnDelete().setEnabled(false);
        view.getTxtCode().setEnabled(true);
        view.getTxtCode().setText("");
        view.getTxtName().setText("");
        view.getTxtUnit().setText("");
    }

    public void close() {
        String botones[] = {"Cerrar", "Cancelar"};
        int eleccion = JOptionPane.showOptionDialog(view, "¿Desea cerrar la aplicación?", "Cerrar", 0, 0, null, botones, this);
        if (eleccion == JOptionPane.YES_OPTION) {
            System.exit(0);
        } else if (eleccion == JOptionPane.NO_OPTION) {

        }
    }
}