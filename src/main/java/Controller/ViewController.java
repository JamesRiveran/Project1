/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.InstrumentType;
import Model.InstrumentsList;
import Model.XMLLoader;
import View.Modulo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
    String filePath = "Tipos de instrumentos.xml";

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
                    try {
                        saveInstrument();
                        XMLLoader.saveToXML(filePath, listInstrument.getList());
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
        if (e.getSource().equals(view.getBtnSearch())) {
            try {
                if (view.getTxtNameForSearch().getText().trim().isEmpty()) {
                    showMessage("Debe ingresar el nombre del instrumento que desea buscar");
                } else {
                    displayInstrumentList();
                }
            } catch (NullPointerException ex) {
                showMessage(ex.getMessage());
            } catch (Exception ex) {
                showMessage(ex.getMessage());
            }
        }
    }

    public void saveInstrument() {
        try {
            String codeText = view.getTxtCode().getText();
            String nameText = view.getTxtName().getText();
            String unitText = view.getTxtUnit().getText();
            InstrumentType newInstrument = new InstrumentType(codeText, unitText, nameText);
            // Verificar si el instrumento ya existe en la lista
            if (!listInstrument.getList().contains(newInstrument)) {
                listInstrument.getList().add(newInstrument);
                JOptionPane.showMessageDialog(view, "Datos registrados\n" + newInstrument.toString());
            } else {
                // Manejar el caso de instrumento duplicado si es necesario
                showMessage("El instrumento ya existe en la lista.");
            }
        } catch (Exception ex) {
            showMessage(ex.getMessage());
        }
    }

    private void displayInstrumentList() {
        try {
            ArrayList<InstrumentType> loadedList = XMLLoader.loadFromXML(filePath);

            DefaultTableModel template = (DefaultTableModel) view.getTblListInstruments().getModel();
            template.setRowCount(0); // Limpia la tabla

            // Agregar los nuevos datos a la tabla
            for (InstrumentType instrument : loadedList) {
                template.addRow(new Object[]{instrument.getCode(), instrument.getName(), instrument.getUnit()});
            }
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace(); // Imprime detalles de la excepción
            // Otro manejo de errores según sea necesario
            showMessage("Error al cargar datos desde el archivo XML: " + ex.getMessage());
        }
    }

}
