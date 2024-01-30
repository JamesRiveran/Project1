/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentation.Controller;

import static Presentation.Controller.ViewController.proxy;
import static Presentation.Controller.ViewController.showMessage;
import static Presentation.Controller.ViewController.user;
import static Presentation.Controller.ViewController.view;
import Presentation.Model.GeneratorPDF;
import static Presentation.Model.GeneratorPDF.loadInstrument;
import Presentation.Model.InstrumentModulo2;
import Presentation.Model.InstrumentType;
import Presentation.Model.IntrumentListModulo2;
import Presentation.View.Modulo;
import Protocol.Message;
import com.itextpdf.text.DocumentException;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    CalibrationController cali;

    IntrumentListModulo2 listModulo2;
    private static ArrayList<InstrumentModulo2> ListOfXml;
    private static ArrayList<InstrumentType> listName;
    private InstruSelectionListener instruSelectionListener;
    static Modulo view;
    boolean update = false;
    int confirmResult;
    Color colorOriginal;
    String simbol = "";

    public IntrumentsController(Modulo views) throws ParserConfigurationException, SAXException {
        this.listModulo2 = new IntrumentListModulo2();
        this.view = views;
        colorOriginal = view.getBtnDeleteInstru().getBackground();
        view.getBtnDeleteInstru().setEnabled(false);
        clickTable();
        this.view.setIntrumentsController(this);

    }

    public void tab() {
        getInformation();
    }

    public IntrumentsController(boolean enter) {
    }

    public static void getInformation() {
        Message msg = new Message();
        msg.setInstruments(ListOfXml);
        msg.setTypeIntruments(listName);
        msg.setSender(user);
        proxy.getInformationModulo2(msg);
    }

    public static void deleteInformation(String code) {
        Message msg = new Message();
        msg.setMessage(code);
        msg.setSender(user);
        proxy.deleteModulo2(msg);
    }

    public static void saveInformation(List<InstrumentModulo2> instrumen, String code, boolean update) {
        Message msg = new Message();
        msg.setSaveInstru(instrumen);
        msg.setMessage(code);
        msg.setUpdate(update);
        msg.setSender(user);
        proxy.saveModulo2(msg);
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
            } else if (view.getCmbType().getSelectedItem().toString().equals("Tipos de instrumentos")) {
                viewController.showMessage(view, "Seleccione un instrumento", "error");
            } else {
                if (!update) {
                    confirmResult = JOptionPane.showConfirmDialog(view, "¿Esta seguro que la infromación ingresada sea la correcta?", "Confirmar", JOptionPane.YES_NO_OPTION);

                } else {
                    confirmResult = JOptionPane.showConfirmDialog(view, "Si cambia los datos debe revisar si se hicieron calibraciones y medciones ya que se cambiara la informacion y los datos hechos ya no serán confiables,¿Deseas continuar?", "Confirmar", JOptionPane.YES_NO_OPTION);
                }

                if (confirmResult == JOptionPane.YES_OPTION) {
                    informationForXml();
                    getInformation();
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
        getInformation();
        view.getCmbType().setSelectedIndex(0);
        if (instruSelectionListener != null) {
            instruSelectionListener.onInstruSelected("", "", "", "", "", "", false);

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
        deleteInformation(view.getTxtSerie().getText());
        clean();
    }

    public void informationForXml() {
        listModulo2.getList().clear();
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
        saveInformation(listModulo2.getList(), code, update);
    }

    public void updateTable() throws ParserConfigurationException, SAXException {
        DefaultTableModel tableModel = (DefaultTableModel) view.getTbInstru().getModel();
        tableModel.setRowCount(0);
        for (int i = ListOfXml.size() - 1; i >= 0; i--) {
            InstrumentModulo2 newInstrument = ListOfXml.get(i);
            tableModel.insertRow(0, new Object[]{newInstrument.getSerie(), newInstrument.getDescri(), newInstrument.getMini(), newInstrument.getMaxi(), newInstrument.getTole(), newInstrument.getType()});
        }
    }

    public void updateComboBoxModel() throws ParserConfigurationException, SAXException {

        if (view != null) {
            JComboBox<String> cmbType = view.getCmbType();

            if (cmbType != null) {
                DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
                comboBoxModel.addElement("Tipos de instrumentos");

                // Agrega los elementos de listName al modelo del JComboBox
                for (InstrumentType name : listName) {
                    String symbol = extractSymbol(name.getUnit());
                    comboBoxModel.addElement(name.getName());
                }

                // Establece el modelo en el JComboBox cmbType
                cmbType.setModel(comboBoxModel);

                // Agrega un listener al JComboBox para actualizar las etiquetas cuando se selecciona un elemento
                cmbType.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            int selectedIndex = cmbType.getSelectedIndex();

                            // Ignora el índice 0 (elemento "Tipos de instrumentos")
                            if (selectedIndex != 0) {
                                InstrumentType selectedInstrument = listName.get(selectedIndex - 1);
                                String symbol = extractSymbol(selectedInstrument.getUnit());
                                view.getLbSimbol().setText(symbol);
                                view.getLbSimbol1().setText(symbol);
                                simbol = symbol;
                            } else {
                                view.getLbSimbol().setText("");
                                view.getLbSimbol1().setText("");
                            }
                        }
                    }
                });
            }
        }
    }

    private String extractSymbol(String input) {
        int startIndex = input.indexOf("(");
        int endIndex = input.indexOf(")");
        if (startIndex != -1 && endIndex != -1) {
            return input.substring(startIndex + 1, endIndex);
        }
        return ""; // Retorna una cadena vacía si no se encuentra el contenido entre paréntesis
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
                CalibrationController.getInformation();
                instruSelectionListener.onInstruSelected(serie, tole, descri, mini, maxi, simbol, true);
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

    public void deliver(Message message) {
        System.out.println("Esto ya esta en el controller se guardo Intruments " + message.getMessage());

        ListOfXml = message.getInstruments();
        listName = message.getTypeIntruments();

        try {
            if (ListOfXml == null || listName == null) {
                System.err.println("estan null");
            } else {
                updateComboBoxModel();
                updateTable();
            }
//       

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
