/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

/**
 *
 * @author james
 */
public class Calibration {

    private String date;
    private String number;
    private int id, measuring,measurement,numberId;

    public Calibration(String number, int id, String date, int measuring) {
        this.date = date;
        this.number = number;
        this.id = id;
        this.measuring = measuring;
    }

    public Calibration(String date, int measurement, int numberId) {
        this.date = date;
        this.measurement = measurement;
        this.numberId = numberId;
    }

    public int getMeasurement() {
        return measurement;
    }

    public void setMeasurement(int measurement) {
        this.measurement = measurement;
    }

    public int getNumberId() {
        return numberId;
    }

    public void setNumberId(int numberId) {
        this.numberId = numberId;
    }

    
    
    public Calibration() {
        this.date = "";
        this.number = "";
        this.id = 0;
        this.measuring = 0;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMeasuring() {
        return measuring;
    }

    public void setMeasuring(int measuring) {
        this.measuring = measuring;
    }

    @Override
    public String toString() {
        return "Serie: " + number + "\n Fecha: " + date + "\n Número: " + id + "\n Mediciones=" + measuring;
    }

}
