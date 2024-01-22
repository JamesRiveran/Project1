/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

import Protocol.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author james
 */
public class Data {
      private List<User> users;

    public Data() {
        users = new ArrayList<>();
        users.add(new User("001","001","Juan"));
        users.add(new User("002","002","Maria"));
        users.add(new User("003","003","Pedro"));
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
