
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import static Controller.IntrumentsController.view;
import Model.GeneratorPDF;
import Model.InstrumentModulo2;
import Model.InstrumentType;
import Model.InstrumentsList;
import Model.IntrumentListModulo2;
import Model.XMLLoader;
import View.Modulo;
import com.itextpdf.text.DocumentException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jdom2.JDOMException;

/**
 *
 * @author james
 */
public class ViewController extends Controller implements ActionListener {

    InstrumentsList listInstrument;
   
    private ArrayList<InstrumentType> listName;
    String filePath = "Laboratorio.xml";
    private ArrayList<InstrumentType> ListOfIModu1o1;
    CalibrationController calibrationController;
    IntrumentsController intrumentsController;
    static Modulo view;

    public ViewController() {
        this.listInstrument = new InstrumentsList();
        this.view = new Modulo();
        this.calibrationController = new CalibrationController(this.view);
        this.intrumentsController = new IntrumentsController(this.view);
        clickTable();
        updateTable();
        updateComboBoxModel();
        this.view.setViewController(this);
    }

    public void start() throws JDOMException, IOException {
        view.getBtnClean().addActionListener(e -> clean());
        view.getBtnDelete().addActionListener(e -> delete());
        view.getBtnPDF().addActionListener(e -> reportPdf());
        view.getBtnSave().addActionListener(e -> save());
        view.getBtnSearch().addActionListener(e -> search());
        view.setLocationRelativeTo(null);
        view.setVisible(true);

        /*Los del modulo 2*/
        view.getBtnSaveInstru().addActionListener(e->intrumentsController.save());
        view.getBtnReport().addActionListener(e->intrumentsController.reportPdf());
        view.getBtnCleanInstru().addActionListener(e->intrumentsController.clean());
        view.getBtnDeleteInstru().addActionListener(e->intrumentsController.delete());
        view.getBtnSearchInstru().addActionListener(e->intrumentsController.search());

        /*modulo 3*/
        view.getCalibrationBtnDelete().addActionListener(this);
        view.getCalibrationBtnSave().addActionListener(e -> calibrationController.save());
        view.getCalibrationBtnClean().addActionListener(e -> calibrationController.clean());
        view.getBtnPDFCalibration().addActionListener(e -> calibrationController.reportPdf());
        view.getBtnSearchCalibration().addActionListener(e -> calibrationController.search());
        view.getBtnSaveMeasurement().addActionListener(e -> calibrationController.saveMeasurement());
        view.getBtnCleanMeasurement().addActionListener(e -> calibrationController.cleanMeasurement());
        XMLLoader.ensureIdCounterExists(filePath);
        int idCounter = idCounter();
        view.getCalibrationTxtNumber().setText(String.valueOf(idCounter));
        view.getCalibrationTxtNumber().setEnabled(false);

    }

    private int idCounter() throws JDOMException, IOException {
        int idCounter = XMLLoader.getIdCounterFromXML(filePath);
        return idCounter;
    }

    public void showMessage(String errorMessage, String info) {
        if (info == "error") {
            JOptionPane.showMessageDialog(view, errorMessage, "Validación", JOptionPane.ERROR_MESSAGE);
        } else if (info == "success") {
            JOptionPane.showMessageDialog(view, errorMessage, "Validación", JOptionPane.INFORMATION_MESSAGE);

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
                showMessage("Debe ingresar el código del instrumento", "error");
            } else if (view.getTxtName().getText().trim().isEmpty()) {
                showMessage("Debe ingresar todos los espacios", "error");
            } else if (view.getTxtUnit().getText().trim().isEmpty()) {
                showMessage("Debe ingresar la unidad de medida del instrumento", "error");
            } else {
                try {
                    InstrumentType newInstrumentForSave = new InstrumentType(
                            view.getTxtCode().getText(), view.getTxtUnit().getText(), view.getTxtName().getText());
                    listInstrument.getList().add(newInstrumentForSave);
                    XMLLoader.saveToXML(filePath, listInstrument.getList());
                    updateTable();
                    updateComboBoxModel();

                } catch (Exception ex) {
                    showMessage("Error al guardar en el archivo XML: " + ex.getMessage(), "error");
                }

            }
        } catch (NullPointerException ex) {
            showMessage(ex.getMessage(), "error");
        } catch (Exception ex) {
            showMessage(ex.getMessage(), "error");
        }
    }
    
    
    /*Metodo para rellenar el comboBox*/
    public void updateComboBoxModel() {
        try {
            listName = XMLLoader.loadFromXML(filePath);

            if (view != null) {
                JComboBox<String> cmbType = view.getCmbType();

                if (cmbType != null) {
                    DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();

                    // Agrega los elementos de listName al modelo del JComboBox
                    for (InstrumentType name : listName) {
                        comboBoxModel.addElement(name.getName());
                    }

                    // Establece el modelo en el JComboBox cmbType
                    cmbType.setModel(comboBoxModel);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void search() {
        try {
            ArrayList<InstrumentType> loadedList = XMLLoader.loadFromXML(filePath);
            if (loadedList.isEmpty()) {
                showMessage("No hay tipos de instrumentos registrados", "error");
            }
        } catch (IOException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        String letterSearch = view.getTxtNameForSearch().getText();
        try {
            filterByName(letterSearch);
        } catch (JDOMException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete() {
        InstrumentType instrumentToDelete = new InstrumentType(
                view.getTxtCode().getText(), view.getTxtUnit().getText(), view.getTxtName().getText());
        try {
            XMLLoader.deleteFromXML(filePath, instrumentToDelete);
            updateTable();
            updateComboBoxModel();
            clean();
        } catch (JDOMException | IOException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void reportPdf() {
        try {
            ArrayList<InstrumentType> instrumentList = XMLLoader.loadFromXML(filePath);
            String pdfFilePath = "Reporte_TiposDeInstrumentos.pdf";
            GeneratorPDF.generatePDFReport(instrumentList, pdfFilePath, "modulo_1");
        } catch (IOException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
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

    private void filterByName(String letterSearch) throws JDOMException, IOException {
        try {
            ArrayList<InstrumentType> loadedList = XMLLoader.loadFromXML(filePath);
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
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }
    }
    public void updateTable() {
        try {
            ListOfIModu1o1 = XMLLoader.loadFromXML(filePath);
            DefaultTableModel tableModule1 = (DefaultTableModel) view.getTblListInstruments().getModel();
            tableModule1.setRowCount(0);

            for (int i = ListOfIModu1o1.size() - 1; i >= 0; i--) {
                InstrumentType module1 = ListOfIModu1o1.get(i);
                tableModule1.insertRow(0, new Object[]{module1.getCode(), module1.getName(), module1.getUnit()});
            }

        } catch (IOException | JDOMException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean clickTable() {
        //Selección de un instrumento de la tabla 
        view.getTblListInstruments().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editRegister(evt);
            }
        });
        return true;

    }

    @Override
    public void clean() {
        view.getBtnDelete().setEnabled(false);
        view.getTxtCode().setEnabled(true);
        view.getTxtCode().setText("");
        view.getTxtName().setText("");
        view.getTxtUnit().setText("");
    }
}
