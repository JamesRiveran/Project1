/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.InstrumentType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author james
 */
public class DataBaseConnection {
    Connection connection;
    private static final Logger logger = LogManager.getLogger();

    
    public void connect(String dbUrl,String userName,String password){
        try {
            
            connection= (Connection) DriverManager.getConnection(dbUrl,userName,password);
            if(connection!=null){
                logger.debug("Database Connection Successful");
                logger.info("Entering application.");

            }
        }catch (SQLException ex){
            //showing exception in log
            logger.error("Exception in connection: "+ ex.toString());

        }
    }
    public void getAllRecords(){
        //sql statement for inserting record
        String sql = "SELECT * FROM instrumenttype";
        //Creating a collection form employee list for storing all employee record
        ArrayList<InstrumentType> employeeList=new ArrayList<InstrumentType>();

        try {

            //creating and executing our statement
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            //iterating over the rows in the result
            while (result.next()) {

                //storing single result in employee object
                InstrumentType instrumentType=new InstrumentType();
                instrumentType.setCode(String.valueOf(result.getString(1)));
                instrumentType.setUnit(result.getString(2));
                instrumentType.setName(result.getString(3));

                //adding employee in employee list
                employeeList.add(instrumentType);
            }
            //caalling function to display all record
            displayRecord(employeeList);
        }catch (Exception e){
            logger.error("Exception in connection: "+ e.toString());

        }
    }
    public void displayRecord(ArrayList<InstrumentType> instrument){
        //iterating over employee list and displaying all employees data
        for(int i=0;i<instrument.size();i++){
            System.out.println("Code: "+instrument.get(i).getCode());
            System.out.println("Unidad: "+instrument.get(i).getUnit());
            System.out.println("Nombre: "+instrument.get(i).getName());

        }
    }
    
    public void saveTypeOfInstrument(String code, String unit, String name){

        //sql statement for inserting record
        String sql = "INSERT INTO InstrumentType (code, unit, name) VALUES (?, ?,?)";
        
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            //setting parameter values
            statement.setString(1, code);
            statement.setString(2, unit);
            statement.setString(3, name);
            //executing query which will return an integer value
            int rowsInserted = statement.executeUpdate();
            //if rowInserted is greater then 0 mean rows are inserted
            if (rowsInserted > 0) {
                logger.debug("Tipo de instrumento guardado con éxito!");
            }
        }catch (Exception e){
            logger.error("Error en la conección: "+ e.toString());

        }
    }
    
    public void deleteRecord(){
        //sql statement for inserting record
        String sql = "DELETE FROM InstrumentType WHERE code=?";

        //getting input from user
        Scanner input=new Scanner(System.in);
        System.out.println("Codigo del tipo de instrumento que quiere borrar: ");
        String id=input.nextLine();

        try {
            //creating and executing our statement
            PreparedStatement statement = connection.prepareStatement(sql);
            //setting parameter values
            statement.setString(1, id);

            int rowsDeleted = statement.executeUpdate();
            //if rowInserted is greater then 0 mean rows are inserted
            if (rowsDeleted > 0) {
                logger.debug("Tipo de instrumento eliminado con éxito!");
            }else {
                logger.debug("Tipo de instrumento no encontrado.");

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void updateRecord(){
        //sql statement for inserting record
        String sql = "UPDATE instrumenttype SET unit=?, name=? WHERE code=?";
        //getting input from user
        Scanner input=new Scanner(System.in);
        System.out.println("Enter code of instrument to update");
        String code=input.nextLine();
        System.out.println("Enter name");
        String name=input.nextLine();
        System.out.println("Enter unit");
        String unit=input.nextLine();
        

        try {
            //creating and executing our statement
            PreparedStatement statement = connection.prepareStatement(sql);
            //setting parameter values
            statement.setString(1, unit);
            statement.setString(2, name);
            statement.setString(3, code);

            int rowsUpdated = statement.executeUpdate();
            //if rowInserted is greater then 0 mean rows are inserted
            if (rowsUpdated > 0) {
                logger.debug("Tipo de instrumento actualizado con éxito!");
            }
        }catch (Exception e){
            logger.error("ERROR en la conexión: "+ e.toString());

        }
    }
    public void closeConnection(){
        try {
            if(connection!=null) {
                connection.close();
            }
        }catch (Exception e){
            logger.error("Exception in connection: "+ e.toString());

        }
    }
}
