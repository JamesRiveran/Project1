/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

import java.util.ArrayList;

/**
 *
 * @author 50686
 */
public class UnidList {
        private ArrayList<UnidsType> list;

    public UnidList(ArrayList<UnidsType> list) {
        this.list = list;
    }
    
    public UnidList() {
        this.list = new ArrayList<UnidsType> ();
    }

    public ArrayList<UnidsType> getList() {
        return list;
    }

    public void setList(ArrayList<UnidsType> list) {
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
