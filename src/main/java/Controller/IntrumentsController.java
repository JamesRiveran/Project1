/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.GeneratorPDF;
import Model.InstrumentModulo2;
import Model.IntrumentListModulo2;
import Model.XMLLoader;
import View.Modulo;
import com.itextpdf.text.DocumentException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author 50686
 */
public class IntrumentsController extends Controller implements ActionListener {

    ViewController viewController;
    IntrumentListModulo2 listModulo2;
    String filePath = "Laboratorio.xml";
    private ArrayList<InstrumentModulo2> ListOfXml;
    CalibrationController calibrationController;
    private InstruSelectionListener instruSelectionListener;
    static Modulo view;
    boolean updateInstruments = false;

    public IntrumentsController(Modulo view) throws ParserConfigurationException, SAXException {
        this.listModulo2 = new IntrumentListModulo2();
        this.view = view;
        this.calibrationController = new CalibrationController(this.view);
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
            if (view.getTxtSerie().getText().isEmpty() || view.getTxtDescri().getText().isEmpty() || view.getTxtMaxi().getText().isEmpty()) {
                viewController.showMessage(view, "Debe llenar todos los campos", "error");
            } else if (view.getCmbType().getItemCount()==0) {
                viewController.showMessage(view, "Debe primero inscribir un tipo de instrumento", "error");
            } else if (serieExists(serie, ListOfXml)) {
                if (updateInstruments == true) {
                    if (Integer.parseInt(view.getTxtMini().getText()) > Integer.parseInt(view.getTxtMaxi().getText())) {
                        viewController.showMessage(view, "El minimo es mayor que el maximo", "error");
                    } else {
                        informationForXml();
                        updateTable();
                        viewController.showMessage(view, "Datos Actualizados", "success");
                        updateInstruments = false;
                    }
                } else {

                    viewController.showMessage(view, "Ya ese numero de serie existe", "error");
                }
            } else if (Integer.parseInt(view.getTxtMini().getText()) > Integer.parseInt(view.getTxtMaxi().getText())) {
                viewController.showMessage(view, "El minimo es mayor que el maximo", "error");
            } else {
                informationForXml();
                // Actualizar la tabla después de agregar el nuevo instrumento
                updateTable();
                updateInstruments = false;
                viewController.showMessage(view, "Datos registrados", "success");
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
        view.getBtnDelete().setEnabled(false);
        view.getTxtSerie().setEnabled(true);
        view.getBtnDeleteInstru().setEnabled(false);
        view.getTxtSerie().setText("");
        view.getTxtMini().setText("");
        view.getTxtTole().setText("");
        view.getTxtMaxi().setText("");
        view.getTxtDescri().setText("");
    }
    
    

    @Override
    public void reportPdf() {
        try {
            ArrayList<InstrumentModulo2> instrumentListModulo2 = new ArrayList<>();
            instrumentListModulo2.clear();
            //instrumentListModulo2 = 
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
        InstrumentModulo2 instrumentToDelete = new InstrumentModulo2(
                view.getTxtSerie().getText(),
                view.getTxtMini().getText(),
                view.getTxtTole().getText(),
                view.getTxtDescri().getText(),
                view.getTxtMaxi().getText(),
                view.getCmbType().getSelectedItem().toString());
        List<org.w3c.dom.Element> calibracionesEncontradas = XMLLoader.findCalibrationsByNumber(filePath, instrumentToDelete.getSerie());
        DefaultTableModel tableModel = (DefaultTableModel) view.getTblCalibrations().getModel();
        tableModel.setRowCount(0);
        if (calibracionesEncontradas.isEmpty()) {
            XMLLoader.deleteInstrumentsFromXML(filePath, instrumentToDelete);
            viewController.showMessage(view, "Se borro exitosamente", "success");
            clean();
            try {
                updateTable();
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(IntrumentsController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(IntrumentsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            viewController.showMessage(view, "Error no se puede eliminar porque el instrumento tiene calibraciones registradas", "error");
        }
    }

    public void informationForXml() {
        InstrumentModulo2 newInstrument = new InstrumentModulo2(
                view.getTxtSerie().getText(),
                view.getTxtMini().getText(),
                view.getTxtTole().getText(),
                view.getTxtDescri().getText(),
                view.getTxtMaxi().getText(),
                view.getCmbType().getSelectedItem().toString()
        );

        listModulo2.getList().add(newInstrument);
        XMLLoader.addToXML(filePath, listModulo2.getList());
        listModulo2.getList().clear();
    }

    public void updateTable() throws ParserConfigurationException, SAXException {
        try {
            ListOfXml = XMLLoader.loadFromXMLS(filePath);
            DefaultTableModel tableModel = (DefaultTableModel) view.getTbInstru().getModel();
            tableModel.setRowCount(0);

            for (int i = ListOfXml.size() - 1; i >= 0; i--) {
                InstrumentModulo2 newInstrument = ListOfXml.get(i);
                tableModel.insertRow(0, new Object[]{newInstrument.getSerie(), newInstrument.getDescri(), newInstrument.getMini(), newInstrument.getMaxi(), newInstrument.getTole(), newInstrument.getType()});
            }
        } catch (IOException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean clickTable() {
        view.getTbInstru().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbInstruMouseClicked(evt);
                updateInstruments = true;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        clickTable();
    }

}
