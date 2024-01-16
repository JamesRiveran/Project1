/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller.sqlServer;

import Model.Calibration;
import Model.InstrumentType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author james
 */
public class BDCalibration {
    ConexionBD conexion = new ConexionBD();
    ResultSet resultado = null;

    public ArrayList<Calibration> getAllCalibration() {
        ArrayList<Calibration> calibrationList = new ArrayList<Calibration>();
        try {
            conexion.setConexion();
            conexion.setConsulta("SELECT * FROM calibration");
            resultado = conexion.getResultado();

            while (resultado.next()) {
                Calibration calibration = new Calibration();
                calibration.setId(Integer.parseInt(resultado.getString(1)));
                calibration.setDate(resultado.getString(2));
                calibration.setMeasuring(Integer.parseInt(resultado.getString(3)));
                calibrationList.add(calibration);
            }

            conexion.cerrarConexion();
        } catch (SQLException error) {
            error.printStackTrace();
        }
        for (Calibration instrument : calibrationList) {
            System.out.println(instrument);
        }
        return calibrationList;
    }

    public void saveCalibration(int number, String date, int measurement) {
        try {
            conexion.setConexion();
            conexion.setConsulta("INSERT INTO calibration (id, date, measuring) VALUES (?, ?,?)");
            conexion.getConsulta().setInt(1, number);
            
            conexion.getConsulta().setString(2,date);
            conexion.getConsulta().setInt(3, measurement);

            if (conexion.getConsulta().executeUpdate() > 0) {
                //Respuesta positiva
                System.out.println("Se insertó la calibración!");
            } else {
                System.out.println("Error en la inserción de la calibración!");
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    public void deleteCalibration(int id) {
        try {
            conexion.setConexion();
            conexion.setConsulta("DELETE FROM calibration WHERE id=?");
            conexion.getConsulta().setInt(1, id);

            if (conexion.getConsulta().executeUpdate() > 0) {
                //Respuesta positiva
                System.out.println("Se eliminó la calibración!");
            } else {
                System.out.println("Error en la inserción de la calibración!");
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }
}
