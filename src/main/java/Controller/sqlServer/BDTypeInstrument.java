/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller.sqlServer;

import Model.InstrumentType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author james
 */
public class BDTypeInstrument {

    ConexionBD conexion = new ConexionBD();
    ResultSet resultado = null;

    public ArrayList<InstrumentType> getAllRecords() {
        ArrayList<InstrumentType> instrumentList = new ArrayList<InstrumentType>();
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

    public void deleteRecord(String code) {
        try {
            conexion.setConexion();
            conexion.setConsulta("DELETE FROM InstrumentType WHERE code=?");
            conexion.getConsulta().setString(1, code);

            if (conexion.getConsulta().executeUpdate() > 0) {
                //Respuesta positiva
                System.out.println("Se eliminó el tipo de instrumento!");
            } else {
                System.out.println("Error en la inserción de tipo de instrumento!");
            }
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    public void saveOrUpdateInstrument(String code, String unit, String name) {
        try {
            conexion.setConexion();
            conexion.setConsulta("SELECT * FROM InstrumentType WHERE code=?");
            conexion.getConsulta().setString(1, code);
            ResultSet resultSet = conexion.getConsulta().executeQuery();

            if (resultSet.next()) {
                // El registro existe, entonces actualiza los valores
                updateRecord(code, unit, name);
            } else {
                // El registro no existe, entonces crea un nuevo registro
                saveTypeOfInstrument(code, unit, name);
            }
        } catch (SQLException error) {
            error.printStackTrace();
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

}
