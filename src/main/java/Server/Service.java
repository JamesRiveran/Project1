/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

import Protocol.Message;
import Protocol.User;

/**
 *
 * @author james
 */
public class Service {
    
    
    public Service() {
        
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
