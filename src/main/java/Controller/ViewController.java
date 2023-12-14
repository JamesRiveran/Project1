/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.InstrumentType;
import Model.InstrumentsList;
import View.Modulo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author james
 */
public class ViewController implements ActionListener{
    InstrumentsList listInstrument;
    Modulo view;

    public ViewController() {
        this.listInstrument = new InstrumentsList();
        this.view = new Modulo();
    }
    
    public void start(){
        view.getBtnClean().addActionListener(this);
        view.getBtnDelete().addActionListener(this);
        view.getBtnPDF().addActionListener(this);
        view.getBtnSave().addActionListener(this);
        view.getBtnSearch().addActionListener(this);
        view.setLocationRelativeTo(null);
        view.setVisible(true);
    }

    private void showMessage(String errorMessage) {
        JOptionPane.showMessageDialog(view, errorMessage, "Validación", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(view.getBtnSave())) {
            try {
                if (view.getTxtCode().getText().trim().isEmpty()) {
                    showMessage("Debe ingresar el código del instrumento");
                } else if (view.getTxtName().getText().trim().isEmpty()) {
                    showMessage("Debe ingresar el nombre del instrumento");
                } else if (view.getTxtUnit().getText().trim().isEmpty()) {
                    showMessage("Debe ingresar la unidad de medida del instrumento");
                } else {
                    saveInstrument(); // Ejecutar el método si todas las validaciones son exitosas
                }
            } catch (NullPointerException ex) {
                showMessage(ex.getMessage());
            } catch (Exception ex) {
                showMessage(ex.getMessage());
            }
        }
        if(e.getSource().equals(view.getBtnSearch())){
            try {
                if (view.getTxtNameForSearch().getText().trim().isEmpty()) {
                    showMessage("Debe ingresar el nombre del instrumento que desea buscar");
                } 
                else {
                    displayInstrumentList();
                }
            } catch (NullPointerException ex) {
                showMessage(ex.getMessage());
            } catch (Exception ex) {
                showMessage(ex.getMessage());
            }
        }
    }
    
    /*private void saveInstrument() {
        InstrumentType newInstrument = new InstrumentType(
                view.getTxtCode().getText(),
                view.getTxtUnit().getText(),
                view.getTxtName().getText()
        );
        listInstrument.getList().add(newInstrument);
        JOptionPane.showMessageDialog(view, "Datos registrados\n" + newInstrument.toString());
    }*/
    
        public void saveInstrument() {
        try {
            Element instruments = new Element("Instrumentos");
            Document doc = new Document(instruments);

            Element typeInstrument = new Element("Tipo_de_instrumento");

            Element code = new Element("Codigo");
            code.setText(view.getTxtCode().getText());
            Element name = new Element("Nombre");
            name.setText(view.getTxtName().getText());
            Element unit = new Element("Unidad");
            unit.setText(view.getTxtUnit().getText());

            typeInstrument.addContent(code);
            typeInstrument.addContent(name);
            typeInstrument.addContent(unit);

            instruments.addContent(typeInstrument);

            XMLOutputter xml = new XMLOutputter();
            xml.setFormat(Format.getPrettyFormat());
            xml.output(doc, new FileWriter("Tipos de instrumentos.xml"));

            JOptionPane.showMessageDialog(view, "Datos guardados","Guardar datos",JOptionPane.INFORMATION_MESSAGE);
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Validación", JOptionPane.ERROR_MESSAGE);
        }

    }
    
    private void displayInstrumentList() {
        DefaultTableModel template = (DefaultTableModel) view.getTblListInstruments().getModel();
        // Limpiar la tabla antes de agregar nuevos datos
        template.setRowCount(0);
        for (InstrumentType instrument : listInstrument.getList()) {
            template.addRow(new Object[]{instrument.getCode(), instrument.getName(), instrument.getUnit()});
        }
    }
    
}
