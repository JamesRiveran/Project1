/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.InstrumentModulo2;
import Model.InstrumentType;
import Model.InstrumentsList;
import Model.IntrumentListModulo2;
import View.Modulo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author james
 */
public class ViewController implements ActionListener {

    InstrumentsList listInstrument;
    IntrumentListModulo2 listModulo2;
    ArrayList<String> listName;

    Modulo view;

    public ViewController() {
        this.listInstrument = new InstrumentsList();
        this.view = new Modulo();
        this.listModulo2 = new IntrumentListModulo2();
        listName = new ArrayList<>();
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(view.getBtnSave())) {
            InstrumentType newInstrument = new InstrumentType(
                    view.getTxtCode().getText(),
                    view.getTxtUnit().getText(),
                    view.getTxtName().getText()
            );
            listInstrument.getList().add(newInstrument);
            listName.add(newInstrument.getName());
            updateComboBoxModel(listName);
            System.out.println(listName);
            JOptionPane.showMessageDialog(view, "Datos registrados\n" + newInstrument.toString());
        }
        if (e.getSource().equals(view.getBtnSearch())) {
            DefaultTableModel template = (DefaultTableModel) view.getTblListInstruments().getModel();
            for (InstrumentType instruments : listInstrument.getList()) {
                template.addRow(new Object[]{instruments.getCode(), instruments.getName(), instruments.getUnit()});
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
                    JOptionPane.showMessageDialog(view, "Error:Existen Campos vacios.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (serieExists(serie)) {
                    JOptionPane.showMessageDialog(view, "Error: El número de serie ya existe en la lista.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    InstrumentModulo2 newInstrument = new InstrumentModulo2(
                            serie,
                            view.getTxtMini().getText(),
                            view.getTxtTole().getText(),
                            view.getTxtDescri().getText(),
                            view.getTxtMaxi().getText(),
                            view.getCmbType().getItemAt(0)
                    );

                    listModulo2.getList().add(newInstrument);

                    // Actualizar la tabla después de agregar el nuevo instrumento
                    DefaultTableModel tableModel = (DefaultTableModel) view.getTbInstru().getModel();
                    tableModel.insertRow(0, new Object[]{newInstrument.getSerie(), newInstrument.getDescri(), newInstrument.getMini(), newInstrument.getMaxi(), newInstrument.getTole()});

                    JOptionPane.showMessageDialog(view, "Datos registrados\n" + newInstrument.toString());
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
        view.getTbInstru().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbInstruMouseClicked(evt);
            }
        });
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
        }
    }

    /* Método para verificar si el número de serie ya existe en la lista*/
    private boolean serieExists(String serie) {
        for (InstrumentModulo2 instrument : listModulo2.getList()) {
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
        DefaultTableModel tableModel = (DefaultTableModel) view.getTbInstru().getModel();

        // Limpia la tabla antes de agregar los resultados de la búsqueda
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }

        // Si la cadena de búsqueda está vacía, muestra todos los elementos
        if (searchLetter.isEmpty()) {
            for (InstrumentModulo2 instrument : listModulo2.getList()) {
                tableModel.addRow(new Object[]{instrument.getSerie(), instrument.getDescri(), instrument.getMini(), instrument.getMaxi(), instrument.getTole()});
            }
        } else {
            // Itera sobre la lista de instrumentos y agrega las coincidencias al modelo de la tabla
            for (InstrumentModulo2 instrument : listModulo2.getList()) {
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
