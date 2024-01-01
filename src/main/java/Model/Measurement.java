/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author james
 */
public class Measurement {

    private double id, reference, reading;
    private String code;
    private int idMeasure;

    public Measurement() {
        this.code = "";
        this.id = 0;
        this.reference = 0;
        this.reading = 0;
        this.idMeasure = 0;
    }

    public Measurement(String code, double id, double reference, double reading, int idMeasure) {
        this.code = code;
        this.id = id;
        this.reference = reference;
        this.reading = reading;
        this.idMeasure = idMeasure;
    }

    public void setIdMeasure(int idMeasure) {
        this.idMeasure = idMeasure;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getIdMeasure() {
        return idMeasure;
    }

    public String getCode() {
        return code;
    }

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public double getReference() {
        return reference;
    }

    public void setReference(double reference) {
        this.reference = reference;
    }

    public double getReading() {
        return reading;
    }

    public void setReading(double reading) {
        this.reading = reading;
    }

    @Override
    public String toString() {
        return "Measurement{" + "id=" + id + ", reference=" + reference + ", reading=" + reading + ", code=" + code + ", idMeasure=" + idMeasure + '}';
    }

  

}
