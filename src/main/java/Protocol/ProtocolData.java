/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Protocol;

/**
 *
 * @author james
 */
public class ProtocolData {
    public static final String SERVER = "127.0.0.1";//Cambiar por el que tenga el MySQL
    public static final int PORT = 3306;//Cambiar por el que tenga el MySQL

    
    public static final int LOGIN=1;
    public static final int LOGOUT=2;    
    public static final int POST=3;

    public static final int DELIVER=10;
    
    /*Si recibo un comando informar como parte del message cual mensaje se mando, insert, update delete,
    pasa por flujo de control y ver a quien tengo que notificar. solo no notifica con los read.*/
    
    /*Modificar estructura de server.delivery message y comando que se ejecutó, 
    del lado del cliente si recibe un 1 que es un create, todos los user deben recibir ese create 
    y se les debe añadir a la tabla de ellos. La idea es que el usuario no pierda la info si lo cambia otro usuario
    desde otro lado.*/
    
    public static final int ERROR_NO_ERROR=0;
    public static final int ERROR_LOGIN=1;
    public static final int ERROR_LOGOUT=2;    
    public static final int ERROR_POST=3;  
}
