/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

/**
 *
 * @author 50686
 */
public class UnidsType {

    private int idUnid;
    private String name, simbol;

    public UnidsType(int idUnid, String name, String simbol) {
        this.idUnid = idUnid;
        this.name = name;
        this.simbol = simbol;
    }

    public UnidsType() {
        this.idUnid = 0;
        this.name = "";
        this.simbol = "";
    }

    public void setIdUnid(int idUnid) {
        this.idUnid = idUnid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSimbol(String simbol) {
        this.simbol = simbol;
    }

    public int getIdUnid() {
        return idUnid;
    }

    public String getName() {
        return name;
    }

    public String getSimbol() {
        return simbol;
    }

    @Override
    public String toString() {
        return "UnidsType{" + "idUnid=" + idUnid + ", name=" + name + ", simbol=" + simbol + '}';
    }
    
    
}
