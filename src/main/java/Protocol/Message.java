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
    private List<InstrumentType> saveTypeIntruments;
    private ArrayList<InstrumentModulo2> instruments;
    private List<InstrumentModulo2> saveInstru;
    private ArrayList<Calibration> calibration;
    private List<Calibration> saveCalibration;
      private ArrayList<Measurement> measure;
    private List<Measurement> saveMeasure;
    public Message() {
    }

    public Message(User sender, String message, String Id, boolean update, String[] data, ArrayList<UnidsType> units, ArrayList<InstrumentType> typeIntruments, List<InstrumentType> saveTypeIntruments, ArrayList<InstrumentModulo2> instruments, List<InstrumentModulo2> saveInstru, ArrayList<Calibration> calibration, List<Calibration> saveCalibration, ArrayList<Measurement> measure, List<Measurement> saveMeasure) {
        this.sender = sender;
        this.message = message;
        this.Id = Id;
        this.update = update;
        this.data = data;
        this.units = units;
        this.typeIntruments = typeIntruments;
        this.saveTypeIntruments = saveTypeIntruments;
        this.instruments = instruments;
        this.saveInstru = saveInstru;
        this.calibration = calibration;
        this.saveCalibration = saveCalibration;
        this.measure = measure;
        this.saveMeasure = saveMeasure;
    }

    public void setCalibration(ArrayList<Calibration> calibration) {
        this.calibration = calibration;
    }

    public void setSaveCalibration(List<Calibration> saveCalibration) {
        this.saveCalibration = saveCalibration;
    }

    public void setMeasure(ArrayList<Measurement> measure) {
        this.measure = measure;
    }

    public void setSaveMeasure(List<Measurement> saveMeasure) {
        this.saveMeasure = saveMeasure;
    }

    public ArrayList<Calibration> getCalibration() {
        return calibration;
    }

    public List<Calibration> getSaveCalibration() {
        return saveCalibration;
    }

    public ArrayList<Measurement> getMeasure() {
        return measure;
    }

    public List<Measurement> getSaveMeasure() {
        return saveMeasure;
    }
    
    

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getId() {
        return Id;
    }

    public void setSaveInstru(List<InstrumentModulo2> saveInstru) {
        this.saveInstru = saveInstru;
    }

    public List<InstrumentModulo2> getSaveInstru() {
        return saveInstru;
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

    public void setSaveTypeIntruments(List<InstrumentType> saveTypeIntruments) {
        this.saveTypeIntruments = saveTypeIntruments;
    }

    public List<InstrumentType> getSaveTypeIntruments() {
        return saveTypeIntruments;
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
        return "Message{" + "sender=" + sender + ", message=" + message + ", Id=" + Id + ", update=" + update + ", data=" + data + ", units=" + units + ", typeIntruments=" + typeIntruments + ", saveTypeIntruments=" + saveTypeIntruments + ", instruments=" + instruments + ", saveInstru=" + saveInstru + ", calibration=" + calibration + ", saveCalibration=" + saveCalibration + ", measure=" + measure + ", saveMeasure=" + saveMeasure + '}';
    }

}
