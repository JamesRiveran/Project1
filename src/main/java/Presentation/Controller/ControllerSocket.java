/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentation.Controller;

import Logic.ServiceProxy;
import Presentation.View.Modulo;
import Presentation_Model.SocketModel;
import Protocol.Message;
import Protocol.User;
import java.util.ArrayList;

/**
 *
 * @author james
 */
public class ControllerSocket {
    Modulo view;
    SocketModel model;
    
    private ServiceProxy localService;
    
    public ControllerSocket(Modulo view, SocketModel model) {
        this.view = view;
        this.model = model;
        localService = (ServiceProxy) ServiceProxy.instance();
        localService.setController(this);
        view.setControllerSocket(this);
        view.setSocketModel(model);
    }

    public void login(User u) throws Exception{
        User logged=ServiceProxy.instance().login(u);
        model.setCurrentUser(logged);
        model.commit(model.USER);
    }

    public void post(String text){
        Message message = new Message();
        message.setMessage(text);
        message.setSender(model.getCurrentUser());
        ServiceProxy.instance().post(message);
        model.commit(model.CHAT);
    }

    public void logout(){
        try {
            ServiceProxy.instance().logout(model.getCurrentUser());
            model.setMessages(new ArrayList<>());
            model.commit(model.CHAT);
        } catch (Exception ex) {
        }
        model.setCurrentUser(null);
        model.commit(model.USER+model.CHAT);
    }
        
    public void deliver(Message message){
        model.messages.add(message);//intento almacenarla
        model.commit(model.CHAT);//si se pudo almacenar la info es correcta. Por ende puedo guardar sin problema       
    }    
}
