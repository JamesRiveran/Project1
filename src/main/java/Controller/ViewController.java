/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.GeneratorPDF;
import Model.InstrumentType;
import Model.InstrumentsList;
import Model.XMLLoader;
import View.Modulo;
import com.itextpdf.text.DocumentException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author james
 */
public class ViewController implements ActionListener {

    InstrumentsList listInstrument;
    Modulo view;
    String filePath = "Laboratorio.xml";

    public ViewController() {
        this.listInstrument = new InstrumentsList();
        this.view = new Modulo();
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
    }

    public void showMessage(String errorMessage) {
        JOptionPane.showMessageDialog(view, errorMessage, "Validación", JOptionPane.ERROR_MESSAGE);
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

}
