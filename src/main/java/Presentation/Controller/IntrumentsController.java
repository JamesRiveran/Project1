/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentation.Controller;

import Logic.Data_logic;
import Logic.GeneratorPDF;
import static Logic.GeneratorPDF.loadInstrument;
import Logic.InstrumentModulo2;
import Logic.InstrumentType;
import Logic.IntrumentListModulo2;
import Presentation.View.Modulo;
import com.itextpdf.text.DocumentException;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author 50686
 */
public final class IntrumentsController extends Controller {

    ViewController viewController;
    IntrumentListModulo2 listModulo2;
    private ArrayList<InstrumentModulo2> ListOfXml;
    private ArrayList<InstrumentType> listName;
    private InstruSelectionListener instruSelectionListener;
    static Modulo view;
    Data_logic data_logic;
    boolean update = false;
    int confirmResult;
    Color colorOriginal;

    public IntrumentsController(Modulo views) throws ParserConfigurationException, SAXException {
        this.listModulo2 = new IntrumentListModulo2();
        this.view = views;
        this.data_logic = new Data_logic();
        colorOriginal = view.getBtnDeleteInstru().getBackground();
        view.getBtnDeleteInstru().setEnabled(false);

        updateComboBoxModel();
        clickTable();
        updateTable();
        this.view.setIntrumentsController(this);

    }

    public void setInstruSelectionListener(InstruSelectionListener listener) {
        this.instruSelectionListener = listener;
    }

    @Override
    public void save() {
        try {
            String serie = view.getTxtSerie().getText();

            // Verifica si el número de serie ya existe en la lista
            if (view.getTxtSerie().getText().isEmpty() || view.getTxtDescri().getText().isEmpty() || view.getTxtMaxi().getText().isEmpty() || view.getTxtMini().getText().isEmpty() || view.getTxtTole().getText().isEmpty()) {
                viewController.showMessage(view, "Debe llenar todos los campos", "error");
            } else if (Integer.parseInt(view.getTxtMini().getText()) > Integer.parseInt(view.getTxtMaxi().getText()) || Integer.parseInt(view.getTxtMini().getText()) == Integer.parseInt(view.getTxtMaxi().getText())) {
                viewController.showMessage(view, "El minimo es mayor que el maximo o el tiene el mismo rango", "error");
            } else {
                if (!update) {
                    confirmResult = JOptionPane.showConfirmDialog(view, "¿Esta seguro que la infromación ingresada sea la correcta?", "Confirmar", JOptionPane.YES_NO_OPTION);

                } else {
                    confirmResult = JOptionPane.showConfirmDialog(view, "Si cambia los datos debe revisar si se hicieron calibraciones y medciones ya que se cambiara la informacion y los datos hechos ya no serán confiables,¿Deseas continuar?", "Confirmar", JOptionPane.YES_NO_OPTION);
                }

                if (confirmResult == JOptionPane.YES_OPTION) {
                    informationForXml();
                    updateTable();
                    clean();
                }
            }
        } catch (Exception ex) {
            // Manejar la excepción (puedes imprimir el mensaje o realizar otras acciones)
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error al procesar los datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void search() {
        String searchLetter = view.getTxtSearchInstru().getText();
        try {
            // Realiza la búsqueda y actualiza la tabla
            filterByDescription(searchLetter);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(IntrumentsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(IntrumentsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void clean() {
        try {
            update = false;
            view.getBtnDelete().setEnabled(false);
            view.getTxtSerie().setEnabled(true);
            view.getBtnDeleteInstru().setEnabled(false);
            view.getBtnDeleteInstru().setBackground(colorOriginal);
            view.getTxtSerie().setText("");
            view.getTxtMini().setText("");
            view.getTxtTole().setText("");
            view.getTxtMaxi().setText("");
            view.getTxtDescri().setText("");
            updateComboBoxModel();
            updateTable();
            view.getCmbType().setSelectedIndex(0);
            
               if (instruSelectionListener != null) {
                instruSelectionListener.onInstruSelected("", "", "", "", "", false);
                
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(IntrumentsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(IntrumentsController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void reportPdf() {
        try {
            ArrayList<InstrumentModulo2> instrumentListModulo2 = new ArrayList<>();
            instrumentListModulo2.clear();
            instrumentListModulo2 = loadInstrument(view.getTbInstru());
            String pdfFilePath = "Reporte_Instrumentos.pdf";
            GeneratorPDF.generatePDFReport(instrumentListModulo2, pdfFilePath, "modulo_2");
            viewController.showMessage(view, "Generado con exito", "success");
        } catch (IOException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete() {
        try {
            data_logic.deletedInstrument(view.getTxtSerie().getText(), view);
            updateTable();
            clean();
        } catch (ParserConfigurationException | SAXException ex) {
            Logger.getLogger(IntrumentsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void informationForXml() {
        listModulo2.getList().clear();
        listName = data_logic.getAllRecordsTypeInstruments();
        String code = "";
        for (InstrumentType name : listName) {
            if (view.getCmbType().getSelectedItem().toString().equals(name.getName())) {
                code = name.getCode();
            }
        }
        InstrumentModulo2 instru = new InstrumentModulo2(view.getTxtSerie().getText(),
                view.getTxtMini().getText(),
                view.getTxtTole().getText(),
                view.getTxtDescri().getText(),
                view.getTxtMaxi().getText(),
                view.getCmbType().getSelectedItem().toString());
        listModulo2.getList().add(instru);
        data_logic.saverOrUpadateInstruModulo2(listModulo2.getList(), code, view, update);
    }

    public void updateTable() throws ParserConfigurationException, SAXException {
        ListOfXml = data_logic.getAllRecordsInstruments();
        DefaultTableModel tableModel = (DefaultTableModel) view.getTbInstru().getModel();
        tableModel.setRowCount(0);
        for (int i = ListOfXml.size() - 1; i >= 0; i--) {
            InstrumentModulo2 newInstrument = ListOfXml.get(i);
            tableModel.insertRow(0, new Object[]{newInstrument.getSerie(), newInstrument.getDescri(), newInstrument.getMini(), newInstrument.getMaxi(), newInstrument.getTole(), newInstrument.getType()});
        }
    }

    /* Método para rellenar el comboBox */
    public void updateComboBoxModel() throws ParserConfigurationException, SAXException {
        listName = data_logic.getAllRecordsTypeInstruments();
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
    }

    public boolean clickTable() {
        view.getTbInstru().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbInstruMouseClicked(evt);
                update = true;
            }

        });
        return true;
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
            String instru = view.getTbInstru().getValueAt(selectedRow, 5).toString();
            // Asigna los valores a los campos correspondientes
            view.getTxtSerie().setText(serie);
            view.getTxtDescri().setText(descri);
            view.getTxtMini().setText(mini);
            view.getTxtMaxi().setText(maxi);
            view.getTxtTole().setText(tole);
            view.getCmbType().setSelectedItem(instru);

            view.getBtnDeleteInstru().setEnabled(true);
            view.getBtnDeleteInstru().setBackground(Color.RED);
            if (instruSelectionListener != null) {
                instruSelectionListener.onInstruSelected(serie, tole, descri, mini, maxi, true);
            }

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

    /*Para filtrar*/
    private void filterByDescription(String searchLetter) throws ParserConfigurationException, SAXException {
        // 
        if (ListOfXml == null) {

            updateTable();
        }

        DefaultTableModel tableModel = (DefaultTableModel) view.getTbInstru().getModel();
        tableModel.setRowCount(0);

        // Si la cadena de búsqueda está vacía, muestra todos los elementos
        if (searchLetter.isEmpty()) {
            updateTable();
        } else {
            // Itera sobre la lista de instrumentos y agrega las coincidencias al modelo de la tabla
            for (InstrumentModulo2 instrument : ListOfXml) {
                // Convierte la descripción a minúsculas para hacer la búsqueda insensible a mayúsculas y minúsculas
                String description = instrument.getDescri().toLowerCase();

                // Verifica si la descripción contiene la letra ingresada
                if (description.contains(searchLetter.toLowerCase())) {
                    tableModel.addRow(new Object[]{instrument.getSerie(), instrument.getDescri(), instrument.getMini(), instrument.getMaxi(), instrument.getTole(), instrument.getType()});
                }
            }
        }
    }
}
