/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import static Presentation.Controller.ViewController.showMessage;
import Logic.InstrumentType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author james
 */
public class BDTypeInstrument {

    ConexionBD conexion = new ConexionBD();
    ResultSet resultado = null;

    public ArrayList<InstrumentType> getAllRecords() {
        ArrayList<InstrumentType> instrumentList = new ArrayList<>();
        try {
            conexion.setConexion();
            conexion.setConsulta("SELECT * FROM instrumenttype");
            resultado = conexion.getResultado();

            while (resultado.next()) {
                InstrumentType instrumentType = new InstrumentType();
                instrumentType.setCode(String.valueOf(resultado.getString(1)));
                instrumentType.setUnit(resultado.getString(2));
                instrumentType.setName(resultado.getString(3));
                instrumentList.add(instrumentType);
            }

            conexion.cerrarConexion();
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return instrumentList;
    }

    public void saveTypeOfInstrument(String code, String unit, String name) {
        try {
            conexion.setConexion();
            conexion.setConsulta("INSERT INTO InstrumentType (code, unit, name) VALUES (?, ?,?)");
            conexion.getConsulta().setString(1, code);
            conexion.getConsulta().setString(2, unit);
            conexion.getConsulta().setString(3, name);

            if (conexion.getConsulta().executeUpdate() > 0) {
                //Respuesta positiva
                System.out.println("Se insertó el tipo de instrumento!");
            } else {
                System.out.println("Error en la inserción de tipo de instrumento!");
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    public String deleteRecord(String code) {
        try {
            conexion.setConexion();
            conexion.setConsulta("DELETE FROM InstrumentType WHERE code=?");
            conexion.getConsulta().setString(1, code);

            if (conexion.getConsulta().executeUpdate() > 0) {
                // Respuesta positiva
                System.out.println("Se eliminó el tipo de instrumento!");
                return "Eliminado exitosamente";
            } else {
                System.out.println("Error en la eliminación de tipo de instrumento!");
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

    public String saveOrUpdateInstrument(String code, String unit, String name) {
        try {
            conexion.setConexion();
            conexion.setConsulta("SELECT * FROM InstrumentType WHERE code=?");
            conexion.getConsulta().setString(1, code);
            ResultSet resultSet = conexion.getConsulta().executeQuery();
            if (resultSet.next()) {
                // El registro existe, entonces actualiza los valores
                updateRecord(code, unit, name);
                return "Actualizado exitosamente";
            } else {
                // El registro no existe, entonces crea un nuevo registro
                saveTypeOfInstrument(code, unit, name);
                return "Guardado exitosamente";
            }
        } catch (SQLException error) {
            error.printStackTrace();
            return "Error al procesar la operación: " + error.getMessage();
        }
    }

    public void updateRecord(String code, String unit, String name) {
        try {
            conexion.setConexion();
            conexion.setConsulta("UPDATE instrumenttype SET unit=?, name=? WHERE code=?");
            conexion.getConsulta().setString(1, unit);
            conexion.getConsulta().setString(2, name);
            conexion.getConsulta().setString(3, code);

            if (conexion.getConsulta().executeUpdate() > 0) {
                //Respuesta positiva
                System.out.println("Se actualizó el tipo de instrumento!");
            } else {
                System.out.println("Error en la actualización de tipo de instrumento!");
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    public boolean instrumentTypeExists(String code) throws SQLException {
        conexion.setConexion();
        conexion.setConsulta("SELECT COUNT(*) AS count FROM InstrumentType WHERE code = ?");
        conexion.getConsulta().setString(1, code);

        ResultSet resultSet = conexion.getConsulta().executeQuery();
        resultSet.next();
        int count = resultSet.getInt("count");

        return count > 0;
    }
}
