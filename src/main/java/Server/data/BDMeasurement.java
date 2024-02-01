/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.data;

import Presentation.Model.Calibration;
import Presentation.Model.InstrumentType;
import Presentation.Model.Measurement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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

    public void saveMeasurement(String id_measurement, String reference, String reading, String idCalibration) {
        String[] data = {id_measurement,reference,reading,idCalibration};

        try {
            conexion.setConexion();
            conexion.setConsulta("INSERT INTO measurement (id_measurement, referenceData, reading, idCalibration) VALUES (?, ?, ?, ?)");

                conexion.getConsulta().setString(1, data[0]);
                conexion.getConsulta().setString(2, data[1]);
                conexion.getConsulta().setString(3,  data[2]);
                conexion.getConsulta().setString(4,  data[3]);

                if (conexion.getConsulta().executeUpdate() <= 0) {
                    // Respuesta negativa
                    System.out.println("Error en la inserción de la medición con id " +  data[0]);
                    // Puedes agregar el ID de la medición a la cadena de retorno
                    
                }
            


            // Si llegamos a este punto, todas las inserciones fueron exitosas
            System.out.println("Todas las mediciones fueron insertadas correctamente");

        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    public String updateReading(List<String> readings, List<String> id, String idToUpdate) {
        System.out.println(readings);
        System.out.println(id);

        if (readings == null || readings.isEmpty()) {
            return "La lista de lecturas no puede ser nula o estar vacía";
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
                    return "Error en la actualización de la medición con idCalibration " + idToUpdate;
                }
            }

            // Return fuera del bucle
            return "Se actualizaron todas las mediciones correctamente para la Calibración: " + idToUpdate;

        } catch (SQLException error) {
            error.printStackTrace();
            return "Error en la implementacion";
        }
    }

    public String deleteMeasurement(String idCalibration) {
        try {
            conexion.setConexion();
            conexion.setConsulta("DELETE FROM measurement WHERE idCalibration=? ");
            conexion.getConsulta().setString(1, idCalibration);

            if (conexion.getConsulta().executeUpdate() > 0) {
                //Respuesta positiva
                System.out.println("Se eliminó la medición!");
                return "Mediciones registradas eliminadas exitosamente, Calibracion ID: "+ idCalibration;
            } else {
                System.out.println("Error en la inserción de la medición!");
                return "Error al eliminar: Medición no encontrado, Calibracion ID" + idCalibration;
            }
        } catch (SQLException error) {
            if (error instanceof SQLIntegrityConstraintViolationException) {
                // Manejo de la excepción específica para clave foránea
                return "Error al eliminar: Este instrumento está siendo referenciado por otros registros.";
            } else {
                // Manejo de otras excepciones
                error.printStackTrace();
                return "Error al procesar la operación: " + error.getMessage();
            }
        }
    }
}
