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
    private String[] dataCalibration;
    private List<Measurement> dataMeasurement;
    private int numberOfCalibration;
    private List<String> reading;
    private List<String> newId;
    String idToUpdate;
    public Message() {
    }

    public Message(User sender, String message,List<String> reading,String idToUpdate, List<String> actualId,List<String> newId, int numberOfCalibration, String Id, boolean update, String[] data, List<Measurement> dataMeasurement, String[] dataCalibration, ArrayList<UnidsType> units, ArrayList<InstrumentType> typeIntruments, ArrayList<InstrumentModulo2> instruments, ArrayList<Calibration> calibration, ArrayList<Measurement> measure) {
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
        this.dataCalibration = dataCalibration;
        this.dataMeasurement = dataMeasurement;
        this.numberOfCalibration = numberOfCalibration;
        this.reading = reading;
        this.newId = newId;
        this.idToUpdate=idToUpdate;
    }

    public String getIdToUpdate() {
        return idToUpdate;
    }

    public void setIdToUpdate(String idToUpdate) {
        this.idToUpdate = idToUpdate;
    }
    
    

    public List<String> getNewId() {
        return newId;
    }

    public void setNewId(List<String> newId) {
        this.newId = newId;
    }
    
    

    public int getNumberOfCalibration() {
        return numberOfCalibration;
    }

    public void setNumberOfCalibration(int numberOfCalibration) {
        this.numberOfCalibration = numberOfCalibration;
    }

    public List<String> getReading() {
        return reading;
    }

    public void setReading(List<String> reading) {
        this.reading = reading;
    }
    
    

    public String[] getDataCalibration() {
        return dataCalibration;
    }

    public void setDataCalibration(String[] dataCalibration) {
        this.dataCalibration = dataCalibration;
    }

    public List<Measurement> getDataMeasurement() {
        return dataMeasurement;
    }

    public void setDataMeasurement(List<Measurement> dataMeasurement) {
        this.dataMeasurement = dataMeasurement;
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
