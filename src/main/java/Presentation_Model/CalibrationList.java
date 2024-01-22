/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentation_Model;

import Presentation_Model.Calibration;
import java.util.ArrayList;

/**
 *
 * @author james
 */
public class CalibrationList {
    private ArrayList<Calibration> list;
    

    public CalibrationList(ArrayList<Calibration> list) {
        this.list = list;
    }

    public CalibrationList() {
        this.list = new ArrayList<Calibration> ();
    }

    public ArrayList<Calibration> getList() {
        return list;
    }

    public void setList(ArrayList<Calibration> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        String mediciones =" ";
        for (int i = 0; i < list.size(); i++) {
            mediciones+=list.get(i).toString()+"\n";
        }
        return "Mediciones" + list ;
    }
}
