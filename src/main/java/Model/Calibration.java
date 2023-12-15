/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author james
 */
public class Calibration {
    private String date;
    private int id,measuring;

    public Calibration(String date, int id, int measuring) {
        this.date = date;
        this.id = id;
        this.measuring = measuring;
    }

    public Calibration() {
        this.date = "";
        this.id = 0;
        this.measuring = 0;
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
        return  "Fecha: " + date + "\n Número: " + id + "\n Mediciones=" + measuring ;
    }
    
    
    
    
}
