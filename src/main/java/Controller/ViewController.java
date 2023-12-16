
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

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
public class ViewController implements ActionListener {

    InstrumentsList listInstrument;
    IntrumentListModulo2 listModulo2;
    ArrayList<String> listName;
    String filePath = "Laboratorio.xml";
    private ArrayList<InstrumentModulo2> ListOfXml;
    Modulo view;
    boolean updateInstruments = false;

    public ViewController() {
        this.listInstrument = new InstrumentsList();
        this.view = new Modulo();
        this.listModulo2 = new IntrumentListModulo2();
        listName = new ArrayList<>();
        clickTable();
        updateTable();
        this.view.setViewController(this);
    }

    public void start() {
        view.getBtnClean().addActionListener(this);
        view.getBtnDelete().addActionListener(this);
        view.getBtnPDF().addActionListener(this);
        view.getBtnSave().addActionListener(this);
        view.getBtnSearch().addActionListener(this);
        view.setLocationRelativeTo(null);
        view.setVisible(true);
        
        /*Los del modulo 2*/
        view.getBtnSaveInstru().addActionListener(this);
        view.getBtnReport().addActionListener(this);
        view.getBtnCleanInstru().addActionListener(this);
        view.getBtnDeleteInstru().addActionListener(this);
        view.getBtnSearchInstru().addActionListener(this);

    }

    private void showMessage(String errorMessage, String info) {
        if (info == "error") {
            JOptionPane.showMessageDialog(view, errorMessage, "Validación", JOptionPane.ERROR_MESSAGE);
        } else if (info == "success") {
            JOptionPane.showMessageDialog(view, errorMessage, "Validación", JOptionPane.INFORMATION_MESSAGE);

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Guardar
        if (e.getSource().equals(view.getBtnSave())) {
            try {
                if (view.getTxtCode().getText().trim().isEmpty()) {
                    showMessage("Debe ingresar el código del instrumento");
                } else if (view.getTxtName().getText().trim().isEmpty()) {
                    showMessage("Debe ingresar el nombre del instrumento");
                } else if (view.getTxtUnit().getText().trim().isEmpty()) {
                    showMessage("Debe ingresar la unidad de medida del instrumento");
                } else {
                    try {
                        InstrumentType newInstrumentForSave = new InstrumentType(
                                view.getTxtCode().getText(), view.getTxtUnit().getText(), view.getTxtName().getText());
                        listInstrument.getList().add(newInstrumentForSave);
                        XMLLoader.saveToXML(filePath,listInstrument.getList());
                        DefaultTableModel tableModel = (DefaultTableModel) view.getTblListInstruments().getModel();
                        tableModel.insertRow(0, new Object[]{newInstrumentForSave.getCode(),newInstrumentForSave.getName(),newInstrumentForSave.getUnit()});
                    } catch (Exception ex) {
                        showMessage("Error al guardar en el archivo XML: " + ex.getMessage());
                    }

                }
            } catch (NullPointerException ex) {
                showMessage(ex.getMessage());
            } catch (Exception ex) {
                showMessage(ex.getMessage());
            }
        }
        //Buscar
        if (e.getSource().equals(view.getBtnSearch())) {
            try {
                ArrayList<InstrumentType> loadedList = XMLLoader.loadFromXML(filePath);
                if(loadedList.isEmpty()){
                    showMessage("No hay tipos de instrumentos registrados");
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
        //Selección de un instrumento de la tabla 
        view.getTblListInstruments().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editRegister(evt);
            }
        });
        //Limpiar
        if(e.getSource().equals(view.getBtnClean())){
            view.getBtnDelete().setEnabled(false);
            view.getTxtCode().setEnabled(true);
            view.getTxtCode().setText("");
            view.getTxtName().setText("");
            view.getTxtUnit().setText("");
        }
        //Eliminar
        if (e.getSource().equals(view.getBtnDelete())) {
            InstrumentType instrumentToDelete = new InstrumentType(
                    view.getTxtCode().getText(), view.getTxtName().getText(), view.getTxtUnit().getText());
            try {
                XMLLoader.deleteFromXML(filePath, instrumentToDelete);
            } catch (JDOMException | IOException ex) {
                Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Reporte
        if(e.getSource().equals(view.getBtnPDF())){
            try {
                ArrayList<InstrumentType> instrumentList = XMLLoader.loadFromXML(filePath);
                String pdfFilePath = "Reporte.pdf";
                GeneratorPDF.generatePDFReport(instrumentList, pdfFilePath);
            } catch (IOException ex) {
                Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JDOMException ex) {
                Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void editRegister(MouseEvent evt){
        int rowSelected = view.getTblListInstruments().getSelectedRow();
        
        if(rowSelected !=-1){
            String codeName = view.getTblListInstruments().getValueAt(rowSelected, 0).toString();
            String instrumentName =view.getTblListInstruments().getValueAt(rowSelected, 1).toString();
            String unitName =view.getTblListInstruments().getValueAt(rowSelected, 2).toString();
            
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
        /**
         * *******************************************************************************************************************************************************
         */

        /*para modulo 2*/
        if (e.getSource().equals(view.getBtnSaveInstru())) {

            try {
                String serie = view.getTxtSerie().getText();

                // Verifica si el número de serie ya existe en la lista
                if (view.getTxtSerie().getText().isEmpty() || view.getTxtDescri().getText().isEmpty() || view.getTxtMaxi().getText().isEmpty()) {
                    showMessage("Debe llenar todos los campos", "error");
                } else if (serieExists(serie, ListOfXml)) {
                    if (updateInstruments == true) {
                        informationForXml();
                        updateTable();
                        JOptionPane.showMessageDialog(view, "Datos Actualizados");
                        updateInstruments = false;
                    } else {
                        showMessage("Ya ese numero de serie existe", "error");
                    }
                } else {
                    informationForXml();
                    // Actualizar la tabla después de agregar el nuevo instrumento
                    updateTable();
                    updateInstruments = false;
                    JOptionPane.showMessageDialog(view, "Datos registrados");
                }
            } catch (Exception ex) {
                // Manejar la excepción (puedes imprimir el mensaje o realizar otras acciones)
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "Error al procesar los datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (e.getSource().equals(view.getBtnSearchInstru())) {
            String searchLetter = view.getTxtSearchInstru().getText();
            // Realiza la búsqueda y actualiza la tabla
            filterByDescription(searchLetter);
        }

        //Limpiar
        if (e.getSource().equals(view.getBtnCleanInstru())) {
            clean();
        }

        //Reporte
        if (e.getSource().equals(view.getBtnReport())) {
            try {
                ArrayList<InstrumentModulo2> instrumentListModulo2 = XMLLoader.loadFromXMLS(filePath);
                String pdfFilePath = "Reporte.pdf";
                GeneratorPDF.generatePDFReport(instrumentListModulo2, pdfFilePath, "modulo_2");
            } catch (IOException ex) {
                Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JDOMException ex) {
                Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (e.getSource().equals(view.getBtnDeleteInstru())) {
            InstrumentModulo2 instrumentToDelete = new InstrumentModulo2(
                    view.getTxtSerie().getText(),
                    view.getTxtMini().getText(),
                    view.getTxtTole().getText(),
                    view.getTxtDescri().getText(),
                    view.getTxtMaxi().getText(),
                    view.getCmbType().getItemAt(0));
            try {
                XMLLoader.deleteInstrumentsFromXML(filePath, instrumentToDelete);
                showMessage("Se borro exitosamente", "succes");
                clean();
                updateTable();

            } catch (JDOMException | IOException ex) {
                Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        clickTable();
    }

    public void informationForXml() {
        InstrumentModulo2 newInstrument = new InstrumentModulo2(
                view.getTxtSerie().getText(),
                view.getTxtMini().getText(),
                view.getTxtTole().getText(),
                view.getTxtDescri().getText(),
                view.getTxtMaxi().getText(),
                view.getCmbType().getItemAt(0)
        );

        listModulo2.getList().add(newInstrument);
        XMLLoader.addToXML(filePath, listModulo2.getList());
    }

    public void updateTable() {
        try {
            ListOfXml = XMLLoader.loadFromXMLS(filePath);
            DefaultTableModel tableModel = (DefaultTableModel) view.getTbInstru().getModel();
            tableModel.setRowCount(0);

            for (int i = ListOfXml.size() - 1; i >= 0; i--) {
                InstrumentModulo2 newInstrument = ListOfXml.get(i);
                tableModel.insertRow(0, new Object[]{newInstrument.getSerie(), newInstrument.getDescri(), newInstrument.getMini(), newInstrument.getMaxi(), newInstrument.getTole()});
            }
        } catch (IOException | JDOMException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean clickTable() {
        view.getTbInstru().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbInstruMouseClicked(evt);
                updateInstruments = true;
            }

        });
        return true;

    }

    public void clean() {
        view.getBtnDelete().setEnabled(false);
        view.getTxtSerie().setEnabled(true);
        view.getTxtSerie().setText("");
        view.getTxtMini().setText("0");
        view.getTxtTole().setText("0");
        view.getTxtMaxi().setText("");
        view.getTxtDescri().setText("");
    }

    public void tbInstruMouseClicked(MouseEvent evt) {
        int selectedRow = view.getTbInstru().getSelectedRow();

        // Verifica si se hizo clic en una fila válida
        if (selectedRow != -1) {
            // Obtén los valores de la fila seleccionada
            String serie = view.getTbInstru().getValueAt(selectedRow, 0).toString();
            String descri = view.getTbInstru().getValueAt(selectedRow, 1).toString();
            String mini = view.getTbInstru().getValueAt(selectedRow, 2).toString();
            String maxi = view.getTbInstru().getValueAt(selectedRow, 3).toString();
            String tole = view.getTbInstru().getValueAt(selectedRow, 4).toString();

            // Asigna los valores a los campos correspondientes
            view.getTxtSerie().setText(serie);
            view.getTxtDescri().setText(descri);
            view.getTxtMini().setText(mini);
            view.getTxtMaxi().setText(maxi);
            view.getTxtTole().setText(tole);

            view.getBtnDeleteInstru().setEnabled(true);

        }
    }

    /* Método para verificar si el número de serie ya existe en la lista*/
    private boolean serieExists(String serie, ArrayList<InstrumentModulo2> instrumentList) {
        for (InstrumentModulo2 instrument : instrumentList) {
            if (instrument.getSerie().equals(serie)) {
                return true; // El número de serie ya existe en la lista
            }
        }
        return false; // El número de serie no existe en la lista
    }


    /*Metodo para rellenar el comboBox*/
    public void updateComboBoxModel(ArrayList<String> listName) {
        if (view != null) {
            JComboBox<String> cmbType = view.getCmbType();

            if (cmbType != null) {
                DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();

                // Agrega los elementos de listName al modelo del JComboBox
                for (String name : listName) {
                    comboBoxModel.addElement(name);
                }

                // Establece el modelo en el JComboBox cmbType
                cmbType.setModel(comboBoxModel);
            }
        }
    }

    /*Para filtrar*/
    private void filterByDescription(String searchLetter) {
        // Asegúrate de tener la lista cargada antes de realizar la búsqueda
        if (ListOfXml == null) {
            // Si la lista no está cargada, intenta cargarla
            updateTable();
        }

        DefaultTableModel tableModel = (DefaultTableModel) view.getTbInstru().getModel();
        tableModel.setRowCount(0);

        // Si la cadena de búsqueda está vacía, muestra todos los elementos
        if (searchLetter.isEmpty()) {
            for (InstrumentModulo2 instrument : ListOfXml) {
                tableModel.addRow(new Object[]{instrument.getSerie(), instrument.getDescri(), instrument.getMini(), instrument.getMaxi(), instrument.getTole()});
            }
        } else {
            // Itera sobre la lista de instrumentos y agrega las coincidencias al modelo de la tabla
            for (InstrumentModulo2 instrument : ListOfXml) {
                // Convierte la descripción a minúsculas para hacer la búsqueda insensible a mayúsculas y minúsculas
                String description = instrument.getDescri().toLowerCase();

                // Verifica si la descripción contiene la letra ingresada
                if (description.contains(searchLetter.toLowerCase())) {
                    tableModel.addRow(new Object[]{instrument.getSerie(), instrument.getDescri(), instrument.getMini(), instrument.getMaxi(), instrument.getTole()});
                }
            }
        }
    }

}

