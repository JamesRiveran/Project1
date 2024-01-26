/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.data;

import Presentation.Model.InstrumentType;
import Presentation.Model.UnidsType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

/**
 *
 * @author james
 */
public class BDTypeInstrument {

    ConexionBD conexion = new ConexionBD();
    ResultSet resultado = null;

    public ArrayList<UnidsType> getAllRecordsOfUnits() {
        ArrayList<UnidsType> unids = new ArrayList<>();
        try {
            conexion.setConexion();
            conexion.setConsulta("SELECT * FROM unidadmedida");
            resultado = conexion.getResultado();

            while (resultado.next()) {
                UnidsType unidsType = new UnidsType();
                unidsType.setIdUnid(Integer.parseInt(resultado.getString(1)));
                unidsType.setName(resultado.getString(2));
                unidsType.setSimbol(resultado.getString(3));
                unids.add(unidsType);
            }

            conexion.cerrarConexion();
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return unids;
    }

    public ArrayList<InstrumentType> getAllRecords() {
        ArrayList<InstrumentType> instrumentList = new ArrayList<>();
        try {
            conexion.setConexion();
            conexion.setConsulta("   SELECT instrumenttype.*,unidadmedida.name,unidadmedida.simbol FROM bd_laboratorio.instrumenttype JOIN bd_laboratorio.unidadmedida ON instrumenttype.idUnidadMedida = unidadmedida.idUnidadMedida");

            resultado = conexion.getResultado();

            while (resultado.next()) {
                InstrumentType instrumentType = new InstrumentType();
                instrumentType.setCode(String.valueOf(resultado.getString(1)));
                String unitName = resultado.getString("unidadmedida.name");
                String unitSymbol = resultado.getString("simbol");
                instrumentType.setUnit(unitName + " " + "("+unitSymbol+")");
                instrumentType.setName(resultado.getString(3));
                instrumentList.add(instrumentType);
            }

            conexion.cerrarConexion();
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return instrumentList;
    }

    public void saveTypeOfInstrument(String code, String idUnidadMedida, String name) {
        try {
            conexion.setConexion();
            conexion.setConsulta("INSERT INTO InstrumentType (code, idUnidadMedida, name) VALUES (?, ?,?)");
            conexion.getConsulta().setString(1, code);
            conexion.getConsulta().setString(2, idUnidadMedida);
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

    public void updateRecord(String code, String idUnidadMedida, String name) {
        try {
            conexion.setConexion();
            conexion.setConsulta("UPDATE instrumenttype SET idUnidadMedida=?, name=? WHERE code=?");
            conexion.getConsulta().setString(1, idUnidadMedida);
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
