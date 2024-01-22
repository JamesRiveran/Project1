/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Protocol;

import java.util.Objects;

/**
 *
 * @author james
 */
public class User {
    String id;
    String clave;
    String nombre;

    public User(String id, String clave, String nombre) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }//verifica que sea el 
        if (obj == null) {
            return false;
        }//que no sea nulo
        if (getClass() != obj.getClass()) {
            return false;
        }// 
        final User other = (User) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }// es un mensaje pero no es mio, crea un usuario. haga un casteo para buscar el que lo envió, si no es igual  
        return true;
    }
}
