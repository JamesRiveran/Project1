package Presentation.Model;


import Protocol.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.mindrot.jbcrypt.BCrypt;

public class Data {
    private List<String> nombresDisponibles;
    private List<String> nombresEscogidos;

    public Data() {
        this.nombresDisponibles = new ArrayList<>();
        this.nombresEscogidos = new ArrayList<>();
    }
    
    public String seleccionarUsuarioAlAzar() {
        if (nombresDisponibles.isEmpty()) {
            return "Todos los nombres han sido escogidos.";
        }

        Random random = new Random();
        int indice = random.nextInt(nombresDisponibles.size());
        String nombreElegido = nombresDisponibles.get(indice);
        nombresDisponibles.remove(nombreElegido);
        nombresEscogidos.add(nombreElegido);

        return nombreElegido;
    }//AÑADDIR VALIDACION EN CASO DE MAS CLIENTES DE LOS QUE SE PUEDE


    public void cargarNombres() {
        nombresDisponibles.add("James Rivera");
        nombresDisponibles.add("Jose Vindas");
        nombresDisponibles.add("Gabriel Madrigal");
        nombresDisponibles.add("Kevin Fallas");
        nombresDisponibles.add("Carlos Carranza");
    }
 
    public static String generarIdLocal() {
        Random random = new Random();
        StringBuilder consecutivo = new StringBuilder();

        // Generar cadena de 6 números aleatorios
        for (int i = 0; i < 6; i++) {
            consecutivo.append(random.nextInt(10)); // Números del 0 al 9
        }

        return consecutivo.toString();
    }

    public static String generarClaveSegura() {
        // Generar una clave segura con BCrypt
        String clavePlana = generarClaveAleatoria();
        return BCrypt.hashpw(clavePlana, BCrypt.gensalt());
    }
    
    private static String generarClaveAleatoria() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < 9;i++) {
            int indice = (int) (Math.random() * caracteres.length());
            clave.append(caracteres.charAt(indice));
        }
        return clave.toString();
    }
}
