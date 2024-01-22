/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentation_Model;

import Presentation_Model.InstrumentModulo2;
import java.util.ArrayList;

/**
 *
 * @author 50686
 */
public class IntrumentListModulo2 {
         private ArrayList<InstrumentModulo2> list;

    public IntrumentListModulo2(ArrayList<InstrumentModulo2> list) {
        this.list = list;
    }
    public IntrumentListModulo2(){  
        this.list=new ArrayList<InstrumentModulo2> ();
    }

    public void setList(ArrayList<InstrumentModulo2> list) {
        this.list = list;
    }

    public ArrayList<InstrumentModulo2> getList() {
        return list;
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
