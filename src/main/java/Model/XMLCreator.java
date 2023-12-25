/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author 50686
 */
import java.io.File;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileWriter;
import java.io.IOException;

public class XMLCreator {

    private static final String NOMBRE_ARCHIVO = "Laboratorio.xml";

    public void createLaboratorioXML() {
        // Verificar si el archivo ya existe
        if (archivoExiste()) {
            System.out.println("El archivo '" + NOMBRE_ARCHIVO + "' ya existe. No se sobrescribirá.");
            return;
        }

        // Crear un elemento raíz
        Element rootElement = new Element("Laboratorio");

        // Crear un documento con el elemento raíz
        Document document = new Document(rootElement);

        // Crear un formato para la salida XML (puedes ajustar la indentación y otros detalles)
        Format format = Format.getPrettyFormat();

        // Crear un escritor para escribir el documento en un archivo XML
        try (FileWriter fileWriter = new FileWriter(NOMBRE_ARCHIVO)) {
            // Crear un objeto XMLOutputter
            XMLOutputter xmlOutputter = new XMLOutputter(format);

            // Escribir el documento en el archivo XML
            xmlOutputter.output(document, fileWriter);

            System.out.println("Archivo XML '" + NOMBRE_ARCHIVO + "' creado con éxito.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean archivoExiste() {
        File archivo = new File(NOMBRE_ARCHIVO);
        return archivo.exists();
    }
}
