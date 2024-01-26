/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentation_Model;

import Server.data.BDCalibration;
import Server.data.BDInstrument;
import Server.data.BDTypeInstrument;
import static Presentation.Controller.ViewController.showMessage;
import Protocol.Message;
import Protocol.User;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author 50686
 */
public class Data_logic {

    BDTypeInstrument dbConnection = new BDTypeInstrument();
    BDInstrument InstrumentsModulo2 = new BDInstrument();
    BDCalibration calibration = new BDCalibration();

    public Data_logic() {
        this.dbConnection = new BDTypeInstrument();
        this.InstrumentsModulo2 = new BDInstrument();
    }

    public void saveOrUpdateInstruments(List<InstrumentType> instrumentList, JFrame parent, boolean update) {
        try {
            for (InstrumentType instru : instrumentList) {
                if (!dbConnection.instrumentTypeExists(instru.getCode()) || update) {
                    String response = dbConnection.saveOrUpdateInstrument(instru.getCode(), instru.getUnit(), instru.getName());
                    showMessage(parent, response, "success");
                } else {
                    showMessage(parent, "Ya existe ese codigo", "error");

                }

            }
        } catch (Exception ex) {
            // Manejo de excepciones
            throw new BusinessException("Error en la lógica de negocio: " + ex.getMessage());
        }
    }

    public void deletedTypeInstrument(String id, JFrame parent) {
        try {
            String deleted = dbConnection.deleteRecord(id);
            showMessage(parent, deleted, "error");
        } catch (Exception ex) {
            throw new BusinessException("Error en la lógica de negocio: " + ex.getMessage());

        }
    }

    public ArrayList<InstrumentType> getAllRecordsTypeInstruments() {
        return dbConnection.getAllRecords();
    }

    public void saverOrUpadateInstruModulo2(List<InstrumentModulo2> instrumentListModulo2, String idIntrymentType, JFrame parent, boolean update) {
        try {
            for (InstrumentModulo2 instru : instrumentListModulo2) {
                if (!InstrumentsModulo2.instrumentTypeExists(instru.getSerie()) || update) {
                    String response = InstrumentsModulo2.saveOrUpdateInstrument(instru.getSerie(), instru.getMini(), instru.getTole(), instru.getDescri(), instru.getMaxi(), instru.getType(), idIntrymentType);
                    showMessage(parent, response, "success");
                } else {
                    showMessage(parent, "Ya existe esa serie", "error");
                }
            }
        } catch (Exception ex) {
            throw new BusinessException("Error en la lógica de negocio: " + ex.getMessage());
        }
    }

    public void deletedInstrument(String id, JFrame parent) {
        try {
            String deleted = InstrumentsModulo2.deleteInstrument(id);
            showMessage(parent, deleted, "error");
        } catch (Exception ex) {
            throw new BusinessException("Error en la lógica de negocio: " + ex.getMessage());

        }
    }

    public ArrayList<InstrumentModulo2> getAllRecordsInstruments() {
        return InstrumentsModulo2.getInstrument();
    }

    public void saveCali(List<Calibration> calibrations, String serieInstrument, JFrame parent, boolean update) {
        try {
            for(Calibration cali: calibrations){
                    String response=calibration.saveCalibration(cali.getId(), cali.getDate(), cali.getMeasuring(), serieInstrument);
                    showMessage(parent, response, "success");
            }
        } catch (Exception ex) {
            throw new BusinessException("Error en la lógica de negocio: " + ex.getMessage());
        }
    }

    public void deletedCali(int id, JFrame parent) {
        try {
            String deleted = calibration.deleteCalibration(id);
            showMessage(parent, deleted, "error");
        } catch (Exception ex) {
            throw new BusinessException("Error en la lógica de negocio: " + ex.getMessage());

        }
    }

    public int getId() {
        int id = calibration.getId();
        return id;
    }

    public ArrayList<Calibration> getAllRecordsCalibration() {
        return calibration.getAllCalibration();
    }
}
