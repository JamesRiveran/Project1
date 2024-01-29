package Protocol;

import Presentation.Model.InstrumentModulo2;
import Presentation.Model.InstrumentType;
import Presentation.Model.IntrumentListModulo2;
import Presentation.Model.UnidsType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {

    private User sender;
    private String message;
    private boolean update;
    private String[] data;
    private ArrayList<UnidsType> units;
    private ArrayList<InstrumentType> typeIntruments;
    private List<InstrumentType> saveTypeIntruments;
    private ArrayList<InstrumentModulo2> instruments;
    private List<InstrumentModulo2> saveInstru;

    public Message() {
    }

    public Message(User sender, String message, boolean update, String[] data, ArrayList<UnidsType> units, ArrayList<InstrumentType> typeIntruments, List<InstrumentType> saveTypeIntruments, ArrayList<InstrumentModulo2> instruments, List<InstrumentModulo2> saveInstru) {
        this.sender = sender;
        this.message = message;
        this.update = update;
        this.data = data;
        this.units = units;
        this.typeIntruments = typeIntruments;
        this.saveTypeIntruments = saveTypeIntruments;
        this.instruments = instruments;
        this.saveInstru = saveInstru;
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
        return "Message{" + "sender=" + sender + ", message=" + message + ", update=" + update + ", data=" + data + ", units=" + units + ", typeIntruments=" + typeIntruments + ", saveTypeIntruments=" + saveTypeIntruments + ", instruments=" + instruments + ", saveInstru=" + saveInstru + '}';
    }

}
