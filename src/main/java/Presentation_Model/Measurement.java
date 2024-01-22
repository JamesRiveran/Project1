/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentation_Model;

/**
 *
 * @author james
 */
public class Measurement {

    private double id;
    private String code,reading;
    private int idMeasure, reference;

    public Measurement() {
        this.code = "";
        this.id = 0;
        this.reference = 0;
        this.reading = "";
        this.idMeasure = 0;
    }

    public Measurement(String code, double id, int reference, String reading, int idMeasure) {
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

    public int getReference() {
        return reference;
    }

    public void setReference(int reference) {
        this.reference = reference;
    }

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    @Override
    public String toString() {
        return "Measurement{" + "id=" + id + ", reference=" + reference + ", reading=" + reading + ", code=" + code + ", idMeasure=" + idMeasure + '}';
    }

  

}
