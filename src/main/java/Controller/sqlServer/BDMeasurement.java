/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller.sqlServer;

import Model.Calibration;
import Model.InstrumentType;
import Model.Measurement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author james
 */
public class BDMeasurement {
     ConexionBD conexion = new ConexionBD();
    ResultSet resultado = null;

    public ArrayList<Measurement> getAllMeasurement() {
        ArrayList<Measurement> measurementList = new ArrayList<>();
        try {
            conexion.setConexion();
            conexion.setConsulta("SELECT * FROM measurement");
            resultado = conexion.getResultado();

            while (resultado.next()) {
                Measurement measurement = new Measurement();
                measurement.setId(Integer.parseInt(resultado.getString(1)));
                measurement.setReference(Integer.parseInt(resultado.getString(2)));
                measurement.setReading(resultado.getString(3));
                measurementList.add(measurement);
            }

            conexion.cerrarConexion();
        } catch (SQLException error) {
            error.printStackTrace();
        }
        
        return measurementList;
    }


    public void saveMeasurement(List<Measurement> measurements) {
    if (measurements == null || measurements.isEmpty()) {
        System.out.println("La lista de mediciones no puede ser nula o estar vacía");
        return;
    }

    try {
        conexion.setConexion();
        conexion.setConsulta("INSERT INTO measurement (id, referenceData, reading) VALUES (?, ?, ?)");

        for (Measurement measurement : measurements) {
            conexion.getConsulta().setString(1, String.valueOf(measurement.getId()));
            conexion.getConsulta().setInt(2, measurement.getReference());
            conexion.getConsulta().setString(3, measurement.getReading());

            if (conexion.getConsulta().executeUpdate() > 0) {
                // Respuesta positiva
                System.out.println("Se insertó la medición con id " + measurement.getIdMeasure());
            } else {
                System.out.println("Error en la inserción de la medición con id " + measurement.getIdMeasure());
            }
        }
    } catch (SQLException error) {
        error.printStackTrace();
    } finally {
        conexion.cerrarConexion(); // Asegúrate de cerrar la conexión después de usarla
    }
}

    public void deleteMeasurement(String id) {//Borrar todos los registros de esas calibración
        try {
            conexion.setConexion();
            conexion.setConsulta("DELETE FROM measurement WHERE id=?");
            conexion.getConsulta().setString(1, id);

            if (conexion.getConsulta().executeUpdate() > 0) {
                //Respuesta positiva
                System.out.println("Se eliminó la medición!");
            } else {
                System.out.println("Error en la inserción de la medición!");
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }
}
