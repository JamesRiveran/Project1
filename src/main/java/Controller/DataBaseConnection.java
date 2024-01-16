/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.InstrumentType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
//            DOMConfigurator.configure("log4j2.xml");
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
