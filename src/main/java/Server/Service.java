package Server;

import Presentation.Model.Data;
import Protocol.IService;
import Protocol.Message;
import Protocol.User;


public class Service implements IService{

    private Data data;
    
    public Service() {
        data =  new Data();
    }
    
    public void post(Message m){
        // if wants to save messages, ex. recivier no logged on
    }
    
    public User login(User p) throws Exception{
        //for(User u:data.getUsers()) if(p.equals(u)) return u;
        //throw new Exception("User does not exist");

        p.setNombre(p.getId()); return p;
    } 

    public void logout(User p) throws Exception{
        //nothing to do
    }    
}
