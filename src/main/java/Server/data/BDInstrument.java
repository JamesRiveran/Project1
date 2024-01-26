/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.data;

import Presentation_Model.InstrumentModulo2;
import Presentation_Model.InstrumentType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

/**
 *
 * @author james
 */
public class BDInstrument {

    ConexionBD conexion = new ConexionBD();
    ResultSet resultado = null;

    public ArrayList<InstrumentModulo2> getInstrument() {
        ArrayList<InstrumentModulo2> instrumentList = new ArrayList<>();
        try {
            conexion.setConexion();
            conexion.setConsulta("SELECT * FROM instrument");
            resultado = conexion.getResultado();

            while (resultado.next()) {
                InstrumentModulo2 instrumentType = new InstrumentModulo2();
                instrumentType.setSerie(String.valueOf(resultado.getString(1)));
                instrumentType.setMini(resultado.getString(2));
                instrumentType.setTole(resultado.getString(3));
                instrumentType.setDescri(resultado.getString(4));
                instrumentType.setMaxi(resultado.getString(5));
                instrumentType.setType(resultado.getString(6));
                instrumentList.add(instrumentType);
            }

            conexion.cerrarConexion();
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return instrumentList;
    }

    public void saveInstrument(String serie, String mini, String tole, String descri, String maxi, String type, String idIntrymentType) {
        try {

            conexion.setConexion();
            conexion.setConsulta("INSERT INTO instrument (serie, mini, tole,descri,maxi,type,idTypeInstrument) VALUES (?, ?,?,?, ?,?,?)");
            conexion.getConsulta().setString(1, serie);
            conexion.getConsulta().setString(2, mini);
            conexion.getConsulta().setString(3, tole);
            conexion.getConsulta().setString(4, descri);
            conexion.getConsulta().setString(5, maxi);
            conexion.getConsulta().setString(6, type);
            conexion.getConsulta().setString(7, idIntrymentType);

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

    public String deleteInstrument(String serie) {
        try {
            conexion.setConexion();
            conexion.setConsulta("DELETE FROM instrument WHERE serie=?");
            conexion.getConsulta().setString(1, serie);

            if (conexion.getConsulta().executeUpdate() > 0) {
                //Respuesta positiva
                System.out.println("Se eliminó el tipo de instrumento!");
                return "Eliminado exitosamente";
            } else {
                System.out.println("Error en la inserción de tipo de instrumento!");
                return "Error al eliminar: Registro no encontrado";
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

    public String saveOrUpdateInstrument(String serie, String mini, String tole, String descri, String maxi, String type, String idIntrymentType) {
        try {
            conexion.setConexion();
            conexion.setConsulta("SELECT * FROM instrument WHERE serie=?");
            conexion.getConsulta().setString(1, serie);
            ResultSet resultSet = conexion.getConsulta().executeQuery();

            if (resultSet.next()) {
                // El registro existe, entonces actualiza los valores
                updateInstrument(serie, mini, tole, descri, maxi, type);
                return "Actualizado exitosamente";
            } else {

                // El registro no existe, entonces crea un nuevo registro
                saveInstrument(serie, mini, tole, descri, maxi, type, idIntrymentType);
                return "Guardado exitosamente";

            }
        } catch (SQLException error) {
            error.printStackTrace();
            return "Error al procesar la operación: " + error.getMessage();

        }
    }

    public void updateInstrument(String serie, String mini, String tole, String descri, String maxi, String type) {
        try {
            conexion.setConexion();
            conexion.setConsulta("UPDATE instrument SET mini=?, tole=?, descri=?, maxi=?, type=? WHERE serie=?");
            conexion.getConsulta().setString(1, mini);
            conexion.getConsulta().setString(2, tole);
            conexion.getConsulta().setString(3, descri);
            conexion.getConsulta().setString(4, maxi);
            conexion.getConsulta().setString(5, type);
            conexion.getConsulta().setString(6, serie);

            if (conexion.getConsulta().executeUpdate() > 0) {
                // Respuesta positiva
                System.out.println("Se actualizó el instrumento con serie " + serie + "!");
            } else {
                System.out.println("Error en la actualización del instrumento con serie " + serie + "!");
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    public boolean instrumentTypeExists(String serie) throws SQLException {
        conexion.setConexion();
        conexion.setConsulta("SELECT COUNT(*) AS count FROM instrument WHERE serie = ?");
        conexion.getConsulta().setString(1, serie);

        ResultSet resultSet = conexion.getConsulta().executeQuery();
        resultSet.next();
        int count = resultSet.getInt("count");

        return count > 0;
    }
}
