package Server;

import Server.data.BDMeasurement;
import Presentation.Model.InstrumentModulo2;
import Server.data.BDInstrument;
import Server.data.BDTypeInstrument;
import Presentation.Model.InstrumentType;
import Protocol.IService;
import Protocol.Message;
import Protocol.ProtocolData;
import Protocol.User;
import Server.data.BDCalibration;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker {

    Server srv;
    ObjectInputStream in;
    ObjectOutputStream out;
    IService service;
    User user;
    BDTypeInstrument type;
    BDInstrument instruments;
    BDCalibration calibration;
    BDMeasurement measu;

    public Worker(Server srv, ObjectInputStream in, ObjectOutputStream out, User user, IService service) {
        this.srv = srv;
        this.in = in;
        this.out = out;
        this.user = user;
        this.service = service;
        this.type = new BDTypeInstrument();
        this.instruments = new BDInstrument();
        this.calibration = new BDCalibration();
        this.measu = new BDMeasurement();

    }

    boolean continuar;

    public void start() {
        try {
            System.out.println("Worker atendiendo peticiones...");
            Thread t = new Thread(new Runnable() {
                public void run() {
                    listen();
                }
            });
            continuar = true;
            t.start();
        } catch (Exception ex) {
        }
    }

    public void stop() {
        continuar = false;
        System.out.println("Conexion cerrada...");
    }

    public void listen() {
        int method;
        while (continuar) {
            try {

                method = in.readInt();
                System.out.println("Operacion: " + method);
                switch (method) {
                    //case Protocol.LOGIN: done on accept
                    case ProtocolData.LOGOUT:
                    try {
                        srv.remove(user);
                        //service.logout(user); //nothing to do
                    } catch (Exception ex) {
                    }
                    stop();
                    break;
                    case ProtocolData.POST:
                        Message message = null;
                        try {
                            message = (Message) in.readObject();
                            System.out.println(message);
                            message.setSender(user);
                            //Toda la logica de implementacion de ir al crud hacer los cambios y retornar el mensaje que se necesita.
                            srv.deliver(message);
                            //service.post(message); // if wants to save messages, ex. recivier no logged on
                            System.out.println(user.getNombre() + ": " + message.getMessage());
                        } catch (ClassNotFoundException ex) {
                        }
                        break;

                    case ProtocolData.GET_INFROMATION_MODULO_1:
                        Message messagess = null;
                        try {
                            messagess = (Message) in.readObject();
                            messagess.setUnits(type.getAllRecordsOfUnits());
                            messagess.setTypeIntruments(type.getAllRecords());
                            messagess.setId(String.valueOf(calibration.getId()));

                            messagess.setSender(user);

                            // Envía la lista de unidades al cliente a través del método deliver
                            srv.deliver(messagess);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case ProtocolData.SAVE_TYPEINSTRUMENTS:
                        //Esto se tine que arreglar toddavia
                        Message save = null;
                        try {
                            save = (Message) in.readObject();
                            boolean up = save.isUpdate();
                            String[] data = save.getData();
                            try {
                                if (!type.instrumentTypeExists(data[0]) || up) {
                                    String response = type.saveOrUpdateInstrument(data[0], data[1], data[2]);
                                    save.setMessage(response + " ID User: " + user.getId());
                                } else {
                                    save.setMessage("Ya existe ese codigo");
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            save.setSender(user);
                            // Envía la lista de unidades al cliente a través del método deliver
                            srv.deliver(save);

                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case ProtocolData.DELETE_TYPEINSTRUMENTS:
                        Message delete = null;
                        try {
                            delete = (Message) in.readObject();
                            String response = type.deleteRecord(delete.getMessage());
                            delete.setMessage(response);
                            delete.setSender(user);
                            // Envía la lista de unidades al cliente a través del método deliver
                            srv.deliver(delete);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;

                    case ProtocolData.GET_INFROMATION_MODULO_2:
                        Message get_Informaion_modulo2 = null;
                        try {
                            get_Informaion_modulo2 = (Message) in.readObject();
                            get_Informaion_modulo2.setInstruments(instruments.getInstrument());
                            get_Informaion_modulo2.setTypeIntruments(type.getAllRecords());

                            get_Informaion_modulo2.setSender(user);

                            // Envía la lista de unidades al cliente a través del método deliver
                            srv.deliver(get_Informaion_modulo2);

                            System.out.println(user.getNombre() + ": " + get_Informaion_modulo2.getInstruments());
                            System.out.println(get_Informaion_modulo2.getTypeIntruments());

                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case ProtocolData.DELETE_INSTRUMENTS:
                        Message deleteInstruments = null;
                        try {
                            deleteInstruments = (Message) in.readObject();
                            String response = instruments.deleteInstrument(deleteInstruments.getMessage());
                            deleteInstruments.setMessage(response);
                            deleteInstruments.setSender(user);
                            // Envía la lista de unidades al cliente a través del método deliver
                            srv.deliver(deleteInstruments);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case ProtocolData.SAVE_INSTRUMENTS:
                        Message save_instruments = null;
                        try {
                            save_instruments = (Message) in.readObject();
                            boolean up = save_instruments.isUpdate();
                            String[] data = save_instruments.getData();
                            String idIntrymentType = save_instruments.getMessage();
                            try {
                                if (!instruments.instrumentTypeExists(data[0]) || up) {
                                    String response = instruments.saveOrUpdateInstrument(data[0], data[2], data[4], data[1], data[3], idIntrymentType);
                                    save_instruments.setMessage(response);
                                } else {
                                    save_instruments.setMessage("Ya existe ese codigo");
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            save_instruments.setSender(user);
                            // Envía la lista de unidades al cliente a través del método deliver
                            srv.deliver(save_instruments);

                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;

                    case ProtocolData.GET_INFORMATION_MODULO_3:
                        Message get_Informaion_modulo3 = null;
                        try {
                            get_Informaion_modulo3 = (Message) in.readObject();
                            get_Informaion_modulo3.setCalibration(calibration.getAllCalibration());
                            get_Informaion_modulo3.setMeasure(measu.getAllMeasurement());

                            get_Informaion_modulo3.setSender(user);

                            // Envía la lista de unidades al cliente a través del método deliver
                            srv.deliver(get_Informaion_modulo3);

                            System.out.println(user.getNombre() + ": " + get_Informaion_modulo3.getCalibration());
                            System.out.println(get_Informaion_modulo3.getMeasure());

                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case ProtocolData.SAVE_MEASUREMENTS:
                        Message save_measurements = null;
                        try {
                            save_measurements = (Message) in.readObject();
                            String[] data = save_measurements.getData();
                            String response = measu.saveMeasurement(data[0], data[1], data[2], data[3]);
                            save_measurements.setMessage(response);
                            save_measurements.setSender(user);
                            // Envía la lista de unidades al cliente a través del método deliver
                            srv.deliver(save_measurements);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;

                }
                out.flush();
            } catch (IOException ex) {
                System.out.println(ex);
                continuar = false;
            }
        }
    }

    //Metodo para entregar solo a su propio cliente, el de arriba entrega a todos.
    public void deliver(Message message) {
        try {
            System.out.println("Esto es en el worker deliver " + message);
            out.writeInt(ProtocolData.DELIVER);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
        }
    }
}
