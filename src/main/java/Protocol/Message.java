package Protocol;

import Presentation.Model.Calibration;
import Presentation.Model.InstrumentModulo2;
import Presentation.Model.InstrumentType;
import Presentation.Model.IntrumentListModulo2;
import Presentation.Model.Measurement;
import Presentation.Model.UnidsType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {

    private User sender;
    private String message;
    private String Id;
    private boolean update;
    private String[] data;
    private ArrayList<UnidsType> units;
    private ArrayList<InstrumentType> typeIntruments;
    private ArrayList<InstrumentModulo2> instruments;
    private ArrayList<Calibration> calibration;
      private ArrayList<Measurement> measure;
    public Message() {
    }

    public Message(User sender, String message, String Id, boolean update, String[] data, ArrayList<UnidsType> units, ArrayList<InstrumentType> typeIntruments, ArrayList<InstrumentModulo2> instruments, ArrayList<Calibration> calibration, ArrayList<Measurement> measure) {
        this.sender = sender;
        this.message = message;
        this.Id = Id;
        this.update = update;
        this.data = data;
        this.units = units;
        this.typeIntruments = typeIntruments;
        this.instruments = instruments;
        this.calibration = calibration;
        this.measure = measure;
    }



    public void setCalibration(ArrayList<Calibration> calibration) {
        this.calibration = calibration;
    }



    public void setMeasure(ArrayList<Measurement> measure) {
        this.measure = measure;
    }

  
    public ArrayList<Calibration> getCalibration() {
        return calibration;
    }

 

    public ArrayList<Measurement> getMeasure() {
        return measure;
    }

    

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getId() {
        return Id;
    }

 



    public void setInstruments(ArrayList<InstrumentModulo2> instruments) {
        this.instruments = instruments;
    }

    public ArrayList<InstrumentModulo2> getInstruments() {
        return instruments;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

 
   
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public ArrayList<UnidsType> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<UnidsType> units) {
        this.units = units;
    }

    public ArrayList<InstrumentType> getTypeIntruments() {
        return typeIntruments;
    }

    public void setTypeIntruments(ArrayList<InstrumentType> typeIntruments) {
        this.typeIntruments = typeIntruments;
    }

    @Override
    public String toString() {
        return "Message{" + "sender=" + sender + ", message=" + message + ", Id=" + Id + ", update=" + update + ", data=" + data + ", units=" + units + ", typeIntruments=" + typeIntruments + ", instruments=" + instruments + ", calibration=" + calibration + ", measure=" + measure + '}';
    }


  

}
