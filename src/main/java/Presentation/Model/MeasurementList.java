/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentation.Model;

import Presentation.Model.Measurement;
import java.util.ArrayList;

/**
 *
 * @author james
 */
public class MeasurementList {
    private ArrayList<Measurement> list;

    public MeasurementList(ArrayList<Measurement> list) {
        this.list = list;
    }
    public MeasurementList() {
        this.list = new ArrayList<Measurement> ();
    }

    public ArrayList<Measurement> getList() {
        return list;
    }

    public void setList(ArrayList<Measurement> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "MeasurementList{" + "list=" + list + '}';
    }
    
    
    
}
