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
                measurement.setId(Integer.parseInt(resultado.getString(2)));
                measurement.setReference(Integer.parseInt(resultado.getString(3)));
                measurement.setReading(resultado.getString(4));
                measurement.setCode(resultado.getString(5));

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
        System.out.println(measurements);

        try {
            conexion.setConexion();
            conexion.setConsulta("INSERT INTO measurement (id_measurement, referenceData, reading,idCalibration) VALUES (?, ?, ?,?)");

            for (Measurement measurement : measurements) {
                conexion.getConsulta().setString(1, String.valueOf(measurement.getId()));
                conexion.getConsulta().setInt(2, measurement.getReference());
                conexion.getConsulta().setString(3, measurement.getReading());
                conexion.getConsulta().setInt(4, Integer.parseInt(measurement.getCode()));

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

    public void updateReading(List<String> readings, List<String> id, String idToUpdate) {
        System.out.println(readings);
        System.out.println(id);

        if (readings == null || readings.isEmpty()) {
            System.out.println("La lista de lecturas no puede ser nula o estar vacía");
            return;
        }

        try {
            conexion.setConexion();

            // Utilizamos una consulta de actualización
            conexion.setConsulta("UPDATE measurement SET reading = ? WHERE idCalibration = ? AND id_measurement=?");

            for (int i = 0; i < readings.size(); i++) {
                // Configuramos los parámetros de la consulta
                conexion.getConsulta().setString(1, readings.get(i));
                conexion.getConsulta().setString(2, idToUpdate);
                conexion.getConsulta().setString(3, id.get(i));

                // Imprimir la consulta SQL antes de ejecutarla
                System.out.println("Consulta SQL: " + conexion.getConsulta());

                // Ejecutamos la consulta de actualización
                if (conexion.getConsulta().executeUpdate() > 0) {
                    // Respuesta positiva
                    System.out.println("Se actualizó la medición con idCalibration " + idToUpdate + " y reading " + readings.get(i));
                } else {
                    System.out.println("Error en la actualización de la medición con idCalibration " + idToUpdate);
                }
            }
        } catch (SQLException error) {
            error.printStackTrace();
        } finally {
            conexion.cerrarConexion();
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
