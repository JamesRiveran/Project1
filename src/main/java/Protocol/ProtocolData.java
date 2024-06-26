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

    public static final String SERVER = "localhost";//Cambiar por el que tenga el MySQL
    public static final int PORT = 1234;//Cambiar por el que tenga el MySQL

    public static final int LOGIN = 1;
    public static final int LOGOUT = 2;
    public static final int POST = 3;

    public static final int DELIVER = 10;

    public static final int DoECHO = 100;

    public static final int GET_INFROMATION_MODULO_1 = 200;
    public static final int GET_INFROMATION_MODULO_2 = 210;
    public static final int GET_ID_CALIBRATION = 220;
    public static final int GET_INFORMATION_MODULO_3 = 230;

    public static final int SAVE_TYPEINSTRUMENTS = 300;
    public static final int SAVE_INSTRUMENTS = 310;
    public static final int SAVE_CALIBRATION = 320;
    public static final int SAVE_MEASUREMENT = 330;
    public static final int SAVE_READING = 340;

    public static final int DELETE_TYPEINSTRUMENTS = 400;
    public static final int DELETE_INSTRUMENTS = 410;
    public static final int DELETE_CALIBRATIONS = 420;
    public static final int DELETE_MEASUREMENT = 430;

    /*Si recibo un comando informar como parte del message cual mensaje se mando, insert, update delete,
    pasa por flujo de control y ver a quien tengo que notificar. solo no notifica con los read.*/
 /*Modificar estructura de server.delivery message y comando que se ejecutó, 
    del lado del cliente si recibe un 1 que es un create, todos los user deben recibir ese create 
    y se les debe añadir a la tabla de ellos. La idea es que el usuario no pierda la info si lo cambia otro usuario
    desde otro lado.*/
    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_LOGIN = 1;
    public static final int ERROR_LOGOUT = 2;
    public static final int ERROR_POST = 3;
    public static final int ERROR_GET_INFROMATION_MODULO_1 = 200;
    public static final int ERROR_GET_INFROMATION_MODULO_2 = 210;
    public static final int ERROR_GET_ID_CALIBRATION = 220;
    public static final int ERROR_GET_INFORMATION_MODULO_3 = 230;
    public static final int ERROT_SAVE_TYPEINSTRUMENTS = 300;
    public static final int ERROT_SAVE_INSTRUMENTS = 310;
    public static final int ERROT_SAVE_CALIBRATION = 320;
    public static final int ERROT_SAVE_MEASUREMENT = 330;

    public static final int ERROT_DELETE_TYPEINSTRUMENTS = 400;
    public static final int ERROT_DELETE_INSTRUMENTS = 410;
    public static final int ERROT_DELETE_CALIBRATIONS = 420;
    public static final int ERROT_DELETE_MEASUREMENT = 430;

}
