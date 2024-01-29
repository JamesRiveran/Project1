/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentation.Model;

import java.io.Serializable;


/**
 *
 * @author james
 */
public class InstrumentType implements Serializable{


    private String code, unit, name;
    
    public InstrumentType(String code, String unit, String name) {
        this.code = code;
        this.unit = unit;
        this.name = name;
    }

    public InstrumentType() {
        this.code = "";
        this.unit = "";
        this.name = "";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "Código: " + code + "\n Unidad: " + unit + "\n Nombre: " + name ;
    }
}
