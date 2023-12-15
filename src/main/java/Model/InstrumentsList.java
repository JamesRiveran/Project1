/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.ArrayList;

/**
 *
 * @author james
 */
public class InstrumentsList {
     private ArrayList<InstrumentType> list;

    public InstrumentsList(ArrayList<InstrumentType> list) {
        this.list = list;
    }
    
    public InstrumentsList() {
        this.list = new ArrayList<InstrumentType> ();
    }

    public ArrayList<InstrumentType> getList() {
        return list;
    }

    public void setList(ArrayList<InstrumentType> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        String datos=" ";
        for (int i = 0; i < list.size(); i++) {
            datos+=list.get(i).toString()+"\n";
        }
        return "Instrumentos" + list ;
    }
}
