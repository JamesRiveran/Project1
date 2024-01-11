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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLCreator {
private static final String NOMBRE_ARCHIVO = "Laboratorio.xml";

    public void createLaboratorioXML() {
        try {
            // Verificar si el archivo ya existe
            if (archivoExiste()) {
                return;
            }

            // Crear un objeto DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Crear un objeto DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Crear un documento XML
            Document document = builder.newDocument();

            // Crear el elemento raíz
            Element rootElement = document.createElement("Laboratorio");

            // Agregar el elemento raíz al documento
            document.appendChild(rootElement);

            // Crear un objeto TransformerFactory
            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            // Crear un objeto Transformer
            Transformer transformer = transformerFactory.newTransformer();

            // Especificar la codificación y la indentación
            transformer.setOutputProperty("encoding", "UTF-8");
            transformer.setOutputProperty("indent", "yes");

            // Crear una fuente DOM para la transformación
            DOMSource source = new DOMSource(document);

            // Crear un resultado de transmisión para escribir en un archivo XML
            StreamResult result = new StreamResult(new File(NOMBRE_ARCHIVO));

            // Realizar la transformación y escribir el documento en el archivo XML
            transformer.transform(source, result);

            System.out.println("Archivo XML '" + NOMBRE_ARCHIVO + "' creado con éxito.");
        } catch (ParserConfigurationException | TransformerException | DOMException  e) {
            e.printStackTrace();
        }
    }

    private boolean archivoExiste() {
        File archivo = new File(NOMBRE_ARCHIVO);
        return archivo.exists();
    }
}
