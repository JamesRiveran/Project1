/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.data;

import Presentation.Model.Calibration;
import Presentation.Model.InstrumentType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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
                calibration.setNumber(String.valueOf(resultado.getString(4)));

                calibrationList.add(calibration);
            }

            conexion.cerrarConexion();
        } catch (SQLException error) {
            error.printStackTrace();
        }

        return calibrationList;
    }

    public String saveCalibration(int id, String date, int measurement, String serieForeing) {
        try {
            conexion.setConexion();
            conexion.setConsulta("INSERT INTO calibration (id, date, measuring,serieForeing) VALUES (?, ?,?,?)");
            conexion.getConsulta().setInt(1, id);
            conexion.getConsulta().setString(2, date);
            conexion.getConsulta().setInt(3, measurement);
            conexion.getConsulta().setString(4, serieForeing);

            if (conexion.getConsulta().executeUpdate() > 0) {
                return "Se insertó la calibración!";
            } else {
                return "Error en la inserción de la calibración!";
            }

        } catch (SQLException error) {
            error.printStackTrace();
            return "Error al procesar la operación: " + error.getMessage();

        }
    }

    public String deleteCalibration(int id) {
        try {
            conexion.setConexion();
            conexion.setConsulta("DELETE FROM calibration WHERE id=?");
            conexion.getConsulta().setInt(1, id);

            if (conexion.getConsulta().executeUpdate() > 0) {
                //Respuesta positiva
                System.out.println("Se eliminó la calibración!");
                return "Eliminado exitosamente";
            } else {
                System.out.println("Error en la inserción de la calibración!");
                return "Error al eliminar: Registro no encontrado";
            }
        } catch (SQLException error) {
            if (error instanceof SQLIntegrityConstraintViolationException) {
                // Manejo de la excepción específica para clave foránea
                return "Error al eliminar: Este tipo de instrumento está siendo referenciado por otros registros.";
            } else {
                // Manejo de otras excepciones
                error.printStackTrace();
                return "Error al procesar la operación: " + error.getMessage();
            }
        }
    }

    public int getId() {
        int nextID = 0;
        try {
            conexion.setConexion();
            conexion.setConsulta("SELECT MAX(id) AS ultimo_id FROM calibration");
            resultado = conexion.getResultado();
            if (resultado.next()) {
                int ultimoID = resultado.getInt("ultimo_id");
                nextID = ultimoID + 1;
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return nextID;
    }

    public boolean instrumentTypeExists(String id) throws SQLException {
        conexion.setConexion();
        conexion.setConsulta("SELECT COUNT(*) AS count FROM calibration WHERE id = ?");
        conexion.getConsulta().setString(1, id);

        ResultSet resultSet = conexion.getConsulta().executeQuery();
        resultSet.next();
        int count = resultSet.getInt("count");

        return count > 0;
    }
    
    
}
