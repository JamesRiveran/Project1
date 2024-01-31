
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentation.Controller;

import Logic.ServiceProxy;
import Presentation.Model.Data;
import Presentation.Model.GeneratorPDF;
import static Presentation.Model.GeneratorPDF.loadTypeOfInstrument;
import Presentation.Model.InstrumentType;
import Presentation.Model.InstrumentsList;
import Presentation.Model.SocketModel;
import Presentation.Model.UnidsType;
import Presentation.View.Modulo;
import Protocol.Message;
import Protocol.User;
import com.itextpdf.text.DocumentException;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author james
 */
public class ViewController extends Controller implements ActionListener {

    InstrumentsList listInstrument;
    private static ArrayList<InstrumentType> ListOfIModu1o1;
    private static ArrayList<UnidsType> ListOfUnids;
    CalibrationController calibrationController;
    IntrumentsController intrumentsController;
    public static Modulo view;
    private static String get_Id = "";
    protected Modulo viewError;
    boolean update = false;
    int confirmResult;
    Color colorOriginal;
    SocketModel socketModel;

    /*Global a la clase para poder consumirlo fuera del method*/
    static ServiceProxy proxy;
    static User user;

    public ViewController() throws ParserConfigurationException, SAXException {
        this.listInstrument = new InstrumentsList();
        this.view = new Modulo();
        this.socketModel = new SocketModel();
        colorOriginal = view.getBtnDelete().getBackground();
        view.getBtnDelete().setEnabled(false);
        this.calibrationController = new CalibrationController(this.view);
        this.intrumentsController = new IntrumentsController(this.view);
        intrumentsController.setInstruSelectionListener(calibrationController);
        clickTable();
        this.view.setViewController(this);
        ControllerSocket controller = new ControllerSocket(view, socketModel);
    }

    public ViewController(boolean enter) {

    }

    public void start() throws IOException, SAXException, ParserConfigurationException {
        view.getBtnClean().addActionListener(e -> clean());
        view.getBtnDelete().addActionListener(e -> delete());
        view.getBtnPDF().addActionListener(e -> reportPdf());
        view.getBtnSave().addActionListener(e -> save());
        view.getBtnSearch().addActionListener(e -> search());
        view.setLocationRelativeTo(null);
        view.setVisible(true);

        /*Los del modulo 2*/
        view.getBtnSaveInstru().addActionListener(e -> intrumentsController.save());
        view.getBtnReport().addActionListener(e -> intrumentsController.reportPdf());
        view.getBtnCleanInstru().addActionListener(e -> intrumentsController.clean());
        view.getBtnDeleteInstru().addActionListener(e -> intrumentsController.delete());
        view.getBtnSearchInstru().addActionListener(e -> intrumentsController.search());

        /*modulo 3*/
        view.getCalibrationBtnDelete().addActionListener(this);
        view.getCalibrationBtnSave().addActionListener(e -> calibrationController.save());
        view.getCalibrationBtnClean().addActionListener(e -> calibrationController.clean());
        view.getBtnPDFCalibration().addActionListener(e -> calibrationController.reportPdf());
        view.getBtnSearchCalibration().addActionListener(e -> calibrationController.search());
        view.getBtnSaveMeasurement().addActionListener(e -> calibrationController.saveMeasurement());
        view.getBtnCleanMeasurement().addActionListener(e -> calibrationController.cleanMeasurement());
        
      
        intrumentsController.tab();
        getInformation();

        view.getCalibrationTxtNumber().setEnabled(false);
        view.getCalibrationBtnDelete().addActionListener(e -> calibrationController.delete());

    }

    public void startSocket() {
        ControllerSocket controllerSocket = new ControllerSocket(view, socketModel);

        Data selector = new Data();
        selector.cargarNombres();
        String nameUser = selector.seleccionarUsuarioAlAzar();
        String idLocal = Data.generarIdLocal();
        String claveSegura = Data.generarClaveSegura();
        System.out.println("ID: " + idLocal + "    Nombre: " + nameUser + "        Clave: " + claveSegura);

        view.setIconImage(new javax.swing.ImageIcon("src/main/resources/images/Logo.png").getImage());
        view.setTitle("SILAB: Sistema de Laboratorio Industrial, User: " + nameUser + " ID: " + idLocal);

        user = new User(idLocal, claveSegura, nameUser);

        //ServiceProxy proxy = new ServiceProxy();
        proxy = new ServiceProxy();
        try {
            // Inicio de sesión
            proxy.login(user);
        } catch (Exception ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void saveInformation(String code, String unit, String name, boolean update) {
        Message msg = new Message();
        String[] datos = {code, unit, name};
        msg.setData(datos);
        msg.setUpdate(update);
        msg.setSender(user);
        proxy.saveIntruments(msg);
    }

    public static void getInformation() {
        Message msg = new Message();
        msg.setTypeIntruments(ListOfIModu1o1);
        msg.setUnits(ListOfUnids);
        msg.setId(get_Id);
        msg.setSender(user);
        proxy.getInformation(msg);
    }

    public static void deleteInformation(String code) {
        Message msg = new Message();
        msg.setMessage(code);
        msg.setSender(user);
        proxy.deleteInstruments(msg);
    }

    public void updateComboBoxModelUnids() throws ParserConfigurationException, SAXException {
        if (view != null) {
            JComboBox<String> cmbUnid = view.getCmbUnit();

            if (cmbUnid != null) {
                DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();

                // Agrega los elementos de listName al modelo del JComboBox
                for (UnidsType name : ListOfUnids) {
                    comboBoxModel.addElement(name.getName() + " " + "(" + name.getSimbol() + ")");
                }

                // Establece el modelo en el JComboBox cmbUnids
                cmbUnid.setModel(comboBoxModel);
            }
        }
    }

    public static void showMessage(JFrame parent, String message, String info) {
        if (info == "error") {
            JOptionPane.showMessageDialog(parent, message, "Validación", JOptionPane.ERROR_MESSAGE);
        } else if (info == "success") {
            JOptionPane.showMessageDialog(view, message, "Validación", JOptionPane.INFORMATION_MESSAGE);

        }
    }

    public static void modalForRelations(String errorMessage, String info) {
        if (info == "error") {
            JOptionPane.showMessageDialog(view, errorMessage, "Validación", JOptionPane.ERROR_MESSAGE);
        } else if (info == "success") {
            JOptionPane.showMessageDialog(view, errorMessage, "Validación", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static String getContentOutsideParentheses(String texto) {
        int inicioParentesis = texto.indexOf('(');

        if (inicioParentesis != -1) {
            return texto.substring(0, inicioParentesis).trim();
        } else {
            return texto.trim(); // Si no hay paréntesis, se devuelve el texto original
        }
    }

    @Override
    public void save() {
        try {
            if (view.getTxtCode().getText().trim().isEmpty()) {
                showMessage(viewError, "Debe ingresar el código del instrumento", "error");
            } else if (view.getTxtName().getText().trim().isEmpty()) {
                showMessage(viewError, "Debe ingresar todos los espacios", "error");
            } else {
                if (!update) {
                    confirmResult = JOptionPane.showConfirmDialog(view, "¿Estas seguro que la unidad es la correcta para el instrumento?", "Confirmar", JOptionPane.YES_NO_OPTION);

                } else {
                    confirmResult = JOptionPane.showConfirmDialog(view, "Si cambia los datos debe revisar si se hicieron calibraciones y medciones ya que se cambiara la informacion y los datos hechos ya no serán confiables,¿Deseas continuar?", "Confirmar", JOptionPane.YES_NO_OPTION);
                }

                if (confirmResult == JOptionPane.YES_OPTION) {
                    try {
                        // Actualizar <Instrumento>
                        String code = view.getTxtCode().getText();
                        int unit = view.getCmbUnit().getSelectedIndex();
                        String newName = view.getTxtName().getText();
                        InstrumentType instrument = new InstrumentType(code, String.valueOf(unit + 1), newName);
                        saveInformation(code, String.valueOf(unit + 1), newName, update);
                        clean();
                        intrumentsController.tab();
                        intrumentsController.updateComboBoxModel();
                    } catch (Exception ex) {
                        showMessage(viewError, "Error al guardar en el archivo XML: " + ex.getMessage(), "error");
                    }
                } else {
                    // El usuario seleccionó "No"
                    // Puedes realizar alguna acción adicional o simplemente no hacer nada
                }
            }
        } catch (NullPointerException ex) {
            showMessage(viewError, ex.getMessage(), "error");
        } catch (Exception ex) {
            showMessage(viewError, ex.getMessage(), "error");
        }
    }

    @Override
    public void search() {
        if (ListOfIModu1o1.isEmpty()) {
            showMessage(viewError, "No hay tipos de instrumentos registrados", "error");
        }
        String letterSearch = view.getTxtNameForSearch().getText();
        try {
            filterByName(letterSearch);
        } catch (IOException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete() {
        try {
            deleteInformation(view.getTxtCode().getText());
            clean();
            intrumentsController.tab();
            intrumentsController.updateComboBoxModel();
        } catch (SAXException | ParserConfigurationException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void reportPdf() {
        try {
            ArrayList<InstrumentType> list = new ArrayList<>();
            list.clear();
            list = loadTypeOfInstrument(view.getTblListInstruments());
            String pdfFilePath = "Reporte_TiposDeInstrumentos.pdf";
            GeneratorPDF.generatePDFReport(list, pdfFilePath, "modulo_1");
            showMessage(viewError, "Generado con exito", "success");
        } catch (IOException | DocumentException ex) {
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

            int selectedIndex = -1;

            for (int i = 0; i < view.getCmbUnit().getItemCount(); i++) {
                String comboBoxItem = view.getCmbUnit().getItemAt(i).toString();

                if (unitName.equalsIgnoreCase(getContentOutsideParentheses(comboBoxItem))) {
                    selectedIndex = i;
                    view.getCmbUnit().setSelectedIndex(i);
                    break;
                }
            }

            view.getTxtCode().setText(codeName);
            view.getTxtName().setText(instrumentName);
            view.getCmbUnit().setSelectedItem(unitName);
            view.getTxtCode().setEnabled(false);
            view.getBtnDelete().setEnabled(true);
            view.getBtnDelete().setBackground(Color.RED);

        }
    }

    private void filterByName(String letterSearch) throws IOException, ParserConfigurationException, SAXException {
        DefaultTableModel template = (DefaultTableModel) view.getTblListInstruments().getModel();
        template.setRowCount(0);
        if (letterSearch.isEmpty()) {
            for (InstrumentType instrument : ListOfIModu1o1) {
                template.addRow(new Object[]{instrument.getCode(), instrument.getName(), instrument.getUnit()});
            }
        } else {
            for (InstrumentType instrument : ListOfIModu1o1) {
                String nameInstrumentForSearch = instrument.getName().toLowerCase();
                if (nameInstrumentForSearch.contains(letterSearch.toLowerCase())) {
                    template.addRow(new Object[]{instrument.getCode(), instrument.getName(), instrument.getUnit()});
                }
            }
        }
    }

    public void updateTable() throws ParserConfigurationException, SAXException {
        DefaultTableModel tableModule1 = (DefaultTableModel) view.getTblListInstruments().getModel();
        tableModule1.setRowCount(0);
        for (int i = ListOfIModu1o1.size() - 1; i >= 0; i--) {
            InstrumentType module1 = ListOfIModu1o1.get(i);
            tableModule1.insertRow(0, new Object[]{module1.getCode(), module1.getName(), module1.getUnit()});
        }
    }

    public boolean clickTable() {
        //Selección de un instrumento de la tabla 
        view.getTblListInstruments().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editRegister(evt);
                update = true;
            }
        });
        return true;

    }

    @Override
    public void clean() {
        update = false;
        view.getBtnDelete().setEnabled(false);
        view.getBtnDelete().setBackground(colorOriginal);
        view.getTxtCode().setEnabled(true);
        view.getTxtCode().setText("");
        view.getTxtName().setText("");
        getInformation();
    }

    public void close() {
        String botones[] = {"Cerrar", "Cancelar"};
        int eleccion = JOptionPane.showOptionDialog(view, "¿Desea cerrar la aplicación?", "Cerrar", 0, 0, null, botones, this);
        if (eleccion == JOptionPane.YES_OPTION) {
            System.exit(0);
        } else if (eleccion == JOptionPane.NO_OPTION) {

        }
    }

    public void deliver(Message message) {
        ListOfUnids = message.getUnits();
        ListOfIModu1o1 = message.getTypeIntruments();
        get_Id = message.getId();
        try {
            if (ListOfUnids == null || ListOfIModu1o1 == null) {
                System.err.println("estan null");
            } else {
                updateComboBoxModelUnids();
                updateTable();
                view.getCalibrationTxtNumber().setText(get_Id);

            }
            if (message.getMessage() == null) {
            } else {
                showMessage(view, message.getMessage(), "success");
            }

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
