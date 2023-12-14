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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

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
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(view.getBtnSave())){
            saveInstrument();
        }
        if(e.getSource().equals(view.getBtnSearch())){
            displayInstrumentList();
        }
    }
    
    private void saveInstrument() {
        InstrumentType newInstrument = new InstrumentType(
                view.getTxtCode().getText(),
                view.getTxtUnit().getText(),
                view.getTxtName().getText()
        );
        listInstrument.getList().add(newInstrument);
        JOptionPane.showMessageDialog(view, "Datos registrados\n" + newInstrument.toString());
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
