/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

/**
 *
 * @author 50686
 */
public class InstrumentModulo2 {

    private String serie, mini, tole, descri, maxi, type;

    public InstrumentModulo2(String serie, String mini, String tole, String descri, String maxi, String type) {
        this.serie = serie;
        this.mini = mini;
        this.tole = tole;
        this.descri = descri;
        this.maxi = maxi;
        this.type = type;
    }
    public InstrumentModulo2(String serie, String mini, String tole, String descri, String maxi) {
        this.serie = serie;
        this.mini = mini;
        this.tole = tole;
        this.descri = descri;
        this.maxi = maxi;
    }

    public InstrumentModulo2() {
        this.serie = "";
        this.mini = "";
        this.tole = "";
        this.descri = "";
        this.maxi = "";
        this.type = "";
    }

    public String getSerie() {
        return serie;
    }

    public String getMini() {
        return mini;
    }

    public String getTole() {
        return tole;
    }

    public String getDescri() {
        return descri;
    }

    public String getMaxi() {
        return maxi;
    }

    public String getType() {
        return type;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public void setMini(String mini) {
        this.mini = mini;
    }

    public void setTole(String tole) {
        this.tole = tole;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    public void setMaxi(String maxi) {
        this.maxi = maxi;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "InstrumentModulo2{" + "serie=" + serie + ", mini=" + mini + ", tole=" + tole + ", descri=" + descri + ", maxi=" + maxi + ", type=" + type + '}';
    }
    
}
