/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Controller.ViewController;
import static Controller.ViewController.showMessage;
import View.Modulo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.*;

/**
 *
 * @author james
 */
public class XMLLoader {

    String filePath = "Laboratorio.xml";
    static Modulo view;

    public static ArrayList<InstrumentType> loadFromXML(String filePath) throws ParserConfigurationException, SAXException, IOException {
        ArrayList<InstrumentType> instrumentList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = (Document) builder.parse(new File(filePath));

        Element rootElement = document.getDocumentElement();
        NodeList instrumentElements = rootElement.getElementsByTagName("Tipo_de_instrumento");

        for (int i = 0; i < instrumentElements.getLength(); i++) {
            Element instrumentElement = (Element) instrumentElements.item(i);
            String code = getChildText(instrumentElement, "Codigo");
            String name = getChildText(instrumentElement, "Nombre");
            String unit = getChildText(instrumentElement, "Unidad");

            InstrumentType instrumentType = new InstrumentType(code, unit, name);
            instrumentList.add(instrumentType);
        }

        return instrumentList;
    }

    private static String getChildText(Element parentElement, String childTagName) {
        NodeList nodeList = parentElement.getElementsByTagName(childTagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        } else {
            return "";
        }
    }

    public static void saveToXML(String filePath, List<InstrumentType> instrumentList) {
        if (instrumentList == null || instrumentList.isEmpty()) {
            throw new IllegalArgumentException("La lista de instrumentos no puede ser nula ni estar vacía");
        }

        try {
            Document doc;

            // Verificar si el archivo ya existe
            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = (Document) builder.parse(file);
            
            Element instruments = doc.getDocumentElement();

            // Agregar los nuevos instrumentos
            for (InstrumentType instrument : instrumentList) {
                // Verificar si el tipo de instrumento ya existe en el archivo
                Element existingInstrument = findInstrumentByCode(instruments, instrument.getCode());

                if (existingInstrument == null) {
                    // Si no existe, crear uno nuevo y agregarlo al elemento raíz
                    Element typeInstrument = doc.createElement("Tipo_de_instrumento");

                    Element code = doc.createElement("Codigo");
                    code.setTextContent(instrument.getCode());
                    Element name = doc.createElement("Nombre");
                    name.setTextContent(instrument.getName());
                    Element unit = doc.createElement("Unidad");
                    unit.setTextContent(instrument.getUnit());

                    typeInstrument.appendChild(code);
                    typeInstrument.appendChild(name);
                    typeInstrument.appendChild(unit);

                    instruments.appendChild(typeInstrument);
                } else {
                    // Si ya existe, actualizar sus datos según sea necesario
                    Element nameElement = (Element) existingInstrument.getElementsByTagName("Nombre").item(0);
                    Element unitElement = (Element) existingInstrument.getElementsByTagName("Unidad").item(0);
                    nameElement.setTextContent(instrument.getName());
                    unitElement.setTextContent(instrument.getUnit());
                }
            }

            // Guardar el documento actualizado en el archivo
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

            System.out.println("Archivo XML guardado correctamente con codificación UTF-8.");
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFromXML(String filePath, InstrumentType instrumentToDelete) throws IOException, SAXException, TransformerConfigurationException, TransformerException {
        try {
            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            ArrayList<InstrumentModulo2> listOfInstruments3 = loadFromXMLS(filePath);

            if (listOfInstruments3.isEmpty()) {
                Element rootElement = document.getDocumentElement();
                NodeList instrumentElements = rootElement.getElementsByTagName("Tipo_de_instrumento");

                for (int i = 0; i < instrumentElements.getLength(); i++) {
                    Element instrumentElement = (Element) instrumentElements.item(i);
                    String code = getChildText(instrumentElement, "Codigo");

                    // Verifica si el código coincide con el que se quiere eliminar
                    if (code.equals(instrumentToDelete.getCode())) {
                        // Elimina el elemento del XML
                        instrumentElement.getParentNode().removeChild(instrumentElement);
                        ViewController.conection(false);
                        break;
                    }
                }
            } else {
                Element rootElement = document.getDocumentElement();
                NodeList instrumentElements = rootElement.getElementsByTagName("Tipo_de_instrumento");
                boolean foundMatch = false;

                for (InstrumentModulo2 name : listOfInstruments3) {
                    System.out.println(name.toString());
                    String type = name.getType();

                    // Verifica si hay una coincidencia con el tipo
                    if (type.equals(instrumentToDelete.getName())) {
                        foundMatch = true;
                        break;
                    }
                }

                if (foundMatch) {
                    ViewController.conection(true);
                } else {
                    for (int i = 0; i < instrumentElements.getLength(); i++) {
                        Element instrumentElement = (Element) instrumentElements.item(i);
                        String code = getChildText(instrumentElement, "Codigo");

                        // Verifica si el código coincide con el que se quiere eliminar
                        if (code.equals(instrumentToDelete.getCode())) {
                            // Elimina el elemento del XML
                            instrumentElement.getParentNode().removeChild(instrumentElement);
                            ViewController.conection(false);
                            break;
                        }
                    }
                }
            }

            // Guardar el documento actualizado en el archivo
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource((Node) document);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

            System.out.println("Archivo XML guardado correctamente con codificación UTF-8.");
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    // Validación para comprobar si el tipo de instrumento ya existe, se actualizarán sus datos en lugar de crear uno nuevo.
    private static Element findInstrumentByCode(Element instruments, String code) {
        NodeList instrumentElements = instruments.getElementsByTagName("Tipo_de_instrumento");

        for (int i = 0; i < instrumentElements.getLength(); i++) {
            Element typeInstrument = (Element) instrumentElements.item(i);
            Element codeElement = (Element) typeInstrument.getElementsByTagName("Codigo").item(0);

            if (codeElement.getTextContent().equals(code)) {
                return typeInstrument;
            }
        }

        return null;
    }

    public static void addToXML(String filePath, List<InstrumentModulo2> instrumentList, String tipoInstrumentoSeleccionado) {
        if (instrumentList == null || instrumentList.isEmpty()) {
            throw new IllegalArgumentException("La lista de instrumentos no puede ser nula ni estar vacía");
        }

        try {
            Document doc;

            // Verificar si el archivo ya existe
            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            doc = builder.parse(file);

            Element laboratorio = doc.getDocumentElement();

            // Buscar el elemento <Tipo_de_instrumento> correspondiente al tipo seleccionado
            Element tipoInstrumento = null;
            NodeList tipoInstrumentoList = laboratorio.getElementsByTagName("Tipo_de_instrumento");
            for (int i = 0; i < tipoInstrumentoList.getLength(); i++) {
                Element tipo = (Element) tipoInstrumentoList.item(i);
                if (tipoInstrumentoSeleccionado.equals(tipo.getElementsByTagName("Nombre").item(0).getTextContent())) {
                    tipoInstrumento = tipo;
                    break;
                }
            }

            // Agregar los nuevos instrumentos dentro del tipo de instrumento correspondiente
            for (InstrumentModulo2 instrument : instrumentList) {
                Element existInstrument = findInstrumentBySerie(laboratorio, instrument.getSerie());
                if (existInstrument == null) {
                    Element instrumentElement = doc.createElement("Instrumento");
                    Element serie = doc.createElement("Serie");
                    serie.setTextContent(instrument.getSerie());
                    Element min = doc.createElement("Minimo");
                    min.setTextContent(instrument.getMini());
                    Element tole = doc.createElement("Tolerancia");
                    tole.setTextContent(instrument.getTole());
                    Element descrip = doc.createElement("Descripcion");
                    descrip.setTextContent(instrument.getDescri());
                    Element max = doc.createElement("Maximo");
                    max.setTextContent(instrument.getMaxi());
                    Element type = doc.createElement("Tipo");
                    type.setTextContent(instrument.getType());

                    instrumentElement.appendChild(serie);
                    instrumentElement.appendChild(min);
                    instrumentElement.appendChild(tole);
                    instrumentElement.appendChild(descrip);
                    instrumentElement.appendChild(max);
                    instrumentElement.appendChild(type);

                    tipoInstrumento.appendChild(instrumentElement);
                } else {
                    // Si ya existe, actualizar sus datos según sea necesario
                    existInstrument.getElementsByTagName("Serie").item(0).setTextContent(instrument.getSerie());
                    existInstrument.getElementsByTagName("Minimo").item(0).setTextContent(instrument.getMini());
                    existInstrument.getElementsByTagName("Tolerancia").item(0).setTextContent(instrument.getTole());
                    existInstrument.getElementsByTagName("Descripcion").item(0).setTextContent(instrument.getDescri());
                    existInstrument.getElementsByTagName("Maximo").item(0).setTextContent(instrument.getMaxi());
                    existInstrument.getElementsByTagName("Tipo").item(0).setTextContent(instrument.getType());
                }

                // Guardar los cambios en el archivo
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(filePath));
                transformer.transform(source, result);

                System.out.println("Archivo XML guardado correctamente con codificación UTF-8.");
            }
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
    }


    public static void updateInstrument(String filePath, String name, String newName) {
        try {
            Document doc;

            // Verificar si el archivo ya existe
            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            if (file.exists()) {
                // Si el archivo existe, cargar el contenido existente
                doc = (Document) builder.parse(file);

                // Obtener la lista de todos los elementos <Instrumento>
                NodeList instrumentos = doc.getDocumentElement().getElementsByTagName("Instrumento");

                // Iterar a través de los elementos <Instrumento> y actualizar <Tipo> si coincide con el nombre
                for (int i = 0; i < instrumentos.getLength(); i++) {
                    Element instrumento = (Element) instrumentos.item(i);
                    Element tipoElement = (Element) instrumento.getElementsByTagName("Tipo").item(0);
                    String tipoValue = tipoElement.getTextContent();

                    if (tipoValue.equals(name)) {
                        tipoElement.setTextContent(newName);
                    }
                }

                // Guardar los cambios en el archivo
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource((Node) doc);
                StreamResult result = new StreamResult(new File(filePath));
                transformer.transform(source, result);
            } else {
                ViewController.showMessage(view, "El archivo no existe.", "error");
            }
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
    }

   public static void saveToXMLCalibration(String filePath, List<Calibration> calibrationList, String serieInstrumento) {
    if (calibrationList == null || calibrationList.isEmpty()) {
        throw new IllegalArgumentException("La lista de calibraciones no puede ser nula ni estar vacía");
    }

    try {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filePath));

        Element laboratorio = doc.getDocumentElement();
        NodeList tipoInstrumentoList = laboratorio.getElementsByTagName("Tipo_de_instrumento");

        for (int i = 0; i < tipoInstrumentoList.getLength(); i++) {
            Element tipoDeInstrumento = (Element) tipoInstrumentoList.item(i);
            NodeList instrumentoList = tipoDeInstrumento.getElementsByTagName("Instrumento");

            for (int j = 0; j < instrumentoList.getLength(); j++) {
                Element instrumento = (Element) instrumentoList.item(j);
                String serie = instrumento.getElementsByTagName("Serie").item(0).getTextContent();

                if (serie.equals(serieInstrumento)) {
                    // Encontramos el instrumento correcto, ahora agregamos las calibraciones
                    for (Calibration calibration : calibrationList) {
                        Element calibracion = doc.createElement("Calibracion");
                        Element serieCalibracion = doc.createElement("Serie");
                        serieCalibracion.setTextContent(calibration.getNumber());
                        Element fecha = doc.createElement("Fecha");
                        fecha.setTextContent(calibration.getDate());
                        Element numero = doc.createElement("Numero");
                        numero.setTextContent(Integer.toString(calibration.getId()));
                        Element mediciones = doc.createElement("Mediciones");
                        mediciones.setTextContent(Integer.toString(calibration.getMeasuring()));

                        calibracion.appendChild(serieCalibracion);
                        calibracion.appendChild(fecha);
                        calibracion.appendChild(numero);
                        calibracion.appendChild(mediciones);

                        instrumento.appendChild(calibracion);
                    }

                    // Guardar los cambios en el archivo XML
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(new File(filePath));
                    transformer.transform(source, result);

                    System.out.println("Calibraciones guardadas correctamente en el instrumento con serie: " + serieInstrumento);
                    return; // Salir después de guardar las calibraciones en el instrumento correcto
                }
            }
        }

        // Si llegamos aquí, significa que no se encontró el instrumento con la serie especificada
        System.out.println("No se encontró el instrumento con serie: " + serieInstrumento);
    } catch (Exception e) {
        e.printStackTrace();
    }
}



    public static void deleteInstrumentFromXML(String filePath, String serieToDelete) {
    try {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filePath));

        Element rootElement = doc.getDocumentElement();
        NodeList tipoInstrumentoList = rootElement.getElementsByTagName("Tipo_de_instrumento");

        for (int i = 0; i < tipoInstrumentoList.getLength(); i++) {
            Element tipoInstrumento = (Element) tipoInstrumentoList.item(i);
            NodeList instrumentElements = tipoInstrumento.getElementsByTagName("Instrumento");

            for (int j = 0; j < instrumentElements.getLength(); j++) {
                Element instrumentElement = (Element) instrumentElements.item(j);
                String serie = instrumentElement.getElementsByTagName("Serie").item(0).getTextContent();

                // Verifica si el código coincide con el que se quiere eliminar
                if (serie.equals(serieToDelete)) {
                    // Elimina el elemento del XML
                    tipoInstrumento.removeChild(instrumentElement);
                    break; // Puedes salir del bucle si se elimina el elemento
                }
            }
        }

        // Guarda los cambios en el archivo XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
        e.printStackTrace();
    }
}
    
    // Método para buscar un instrumento por serie
    private static Element findInstrumentBySerie(Element instruments, String serie) {
        NodeList instrumentElements = instruments.getElementsByTagName("Instrumento");
        for (int i = 0; i < instrumentElements.getLength(); i++) {
            Element instrumentElement = (Element) instrumentElements.item(i);
            String code = instrumentElement.getElementsByTagName("Serie").item(0).getTextContent();
            if (code.equals(serie)) {
                return instrumentElement;
            }
        }
        return null;
    }

    
    // Método para cargar instrumentos desde XML
    public static ArrayList<InstrumentModulo2> loadFromXMLS(String filePath) throws ParserConfigurationException, SAXException, IOException {
        ArrayList<InstrumentModulo2> instruments = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filePath));

        Element rootElement = document.getDocumentElement();
        NodeList instrumentElements = rootElement.getElementsByTagName("Instrumento");

        for (int i = 0; i < instrumentElements.getLength(); i++) {
            Element instrumentElement = (Element) instrumentElements.item(i);
            String serie = instrumentElement.getElementsByTagName("Serie").item(0).getTextContent();
            String min = instrumentElement.getElementsByTagName("Minimo").item(0).getTextContent();
            String tole = instrumentElement.getElementsByTagName("Tolerancia").item(0).getTextContent();
            String descrip = instrumentElement.getElementsByTagName("Descripcion").item(0).getTextContent();
            String maxi = instrumentElement.getElementsByTagName("Maximo").item(0).getTextContent();
            String type = instrumentElement.getElementsByTagName("Tipo").item(0).getTextContent();

            InstrumentModulo2 instrumentModulo2 = new InstrumentModulo2(serie, min, tole, descrip, maxi, type);
            instruments.add(instrumentModulo2);
        }

        return instruments;
    }

    // Método para obtener el valor de IdMedicionCounter desde XML
    public static int getIdMedicionFromXML(String filePath) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filePath));

        Element rootElement = document.getDocumentElement();
        Element idMedicionElement = (Element) rootElement.getElementsByTagName("IdMedicionCounter").item(0);

        int updatedIdMedicion;
        if (idMedicionElement == null) {
            // Si la etiqueta <IdMedicionCounter> no existe, crear una nueva con valor 1
            idMedicionElement = document.createElement("IdMedicionCounter");
            idMedicionElement.setTextContent("1"); // Valor predeterminado
            rootElement.appendChild(idMedicionElement);
            updatedIdMedicion = 1;
        } else {
            // Si la etiqueta <IdMedicionCounter> existe, obtener el valor actual y aumentarlo en 1
            int currentIdMedicion = Integer.parseInt(idMedicionElement.getTextContent());
            updatedIdMedicion = currentIdMedicion + 1;
            // Actualizar el valor del contador
            idMedicionElement.setTextContent(Integer.toString(updatedIdMedicion));

            // Guardar los cambios en el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
        }

        return updatedIdMedicion;
    }
    
    public static int getIdCounter(String filePath) {
    try {
        File xmlFile = new File(filePath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        Document document;
        document = builder.parse(xmlFile);
       
        // Obtenemos el valor actual de <idCounter>
        Element rootElement = document.getDocumentElement();
        Element idCounterElement = (Element) rootElement.getElementsByTagName("idCounter").item(0);
        int currentIdCounter = Integer.parseInt(idCounterElement.getTextContent());
        return currentIdCounter;
    } catch (ParserConfigurationException | SAXException | IOException  e) {
        throw new RuntimeException("Error al procesar el archivo XML: " + e.getMessage(), e);
    }
}

    // Método para obtener el valor de idCounter desde XML
   public static int getIdCounterFromXML(String filePath) throws ParserConfigurationException {
       DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
       DocumentBuilder builder = factory.newDocumentBuilder();
       try (InputStream inputStream = new FileInputStream(filePath)) {
           Document document = builder.parse(inputStream);
           
           Element rootElement = document.getDocumentElement();
           Element idCounterElement = (Element) rootElement.getElementsByTagName("idCounter").item(0);
           
           int updatedIdCounter = 0;
           if (idCounterElement != null) {
               // Si la etiqueta <idCounter> existe, obtener el valor actual y aumentarlo en 1
               int currentIdCounter = Integer.parseInt(idCounterElement.getTextContent());
               updatedIdCounter = currentIdCounter + 1;
               // Actualizar el valor del contador
               idCounterElement.setTextContent(Integer.toString(updatedIdCounter));
               
               // Guardar los cambios en el archivo XML
               TransformerFactory transformerFactory = TransformerFactory.newInstance();
               Transformer transformer = transformerFactory.newTransformer();
               DOMSource source = new DOMSource(document);
               StreamResult result = new StreamResult(new File(filePath));
               transformer.transform(source, result);
           }
           
           return updatedIdCounter;
       } catch (IOException | SAXException | TransformerException e) {
           throw new RuntimeException("Error al parsear el archivo XML: " + e.getMessage(), e);
       }
}


    public static String getNameOfInstrument(String filePath, String codigoBuscar) {
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(xmlFile);

            NodeList tipoInstrumentos = document.getElementsByTagName("Tipo_de_instrumento");

            for (int i = 0; i < tipoInstrumentos.getLength(); i++) {
                Node tipoInstrumentoNode = tipoInstrumentos.item(i);
                if (tipoInstrumentoNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element tipoInstrumentoElement = (Element) tipoInstrumentoNode;
                    String codigo = tipoInstrumentoElement.getElementsByTagName("Codigo").item(0).getTextContent();
                    String nombre = tipoInstrumentoElement.getElementsByTagName("Nombre").item(0).getTextContent();

                    if (codigo.equals(codigoBuscar)) {
                        return nombre;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void ensureIdMedicionExists(String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc;
            File file = new File(filePath);
            doc = builder.parse(file);
            

            // Buscar la etiqueta <IdMedicionCounter>
            Element idMedicionCounterElement = (Element) doc.getDocumentElement().getElementsByTagName("IdMedicionCounter").item(0);

            if (idMedicionCounterElement == null) {
                // Si la etiqueta <IdMedicionCounter> no existe, crear una nueva y asignarle el valor predeterminado
                idMedicionCounterElement = doc.createElement("IdMedicionCounter");
                idMedicionCounterElement.setTextContent("1"); // Valor predeterminado
                doc.getDocumentElement().appendChild(idMedicionCounterElement);
                System.out.println("Se ha añadido el valor de <IdMedicionCounter> en el archivo XML.");
            }

            // Guardar los cambios en el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ensureIdCounterExists(String filePath) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(filePath));

            Element rootElement = doc.getDocumentElement();
            NodeList idCounterList = rootElement.getElementsByTagName("idCounter");

            if (idCounterList.getLength() == 0) {
                // Si la etiqueta <idCounter> no existe, crear una nueva y asignarle el valor predeterminado
                Element newIdCounterElement = doc.createElement("idCounter");
                newIdCounterElement.appendChild(doc.createTextNode("0")); // Valor predeterminado
                rootElement.appendChild(newIdCounterElement);

                System.out.println("Se ha añadido el valor de <idCounter> en el archivo XML.");
            }

            // Guardar los cambios en el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Calibration> loadFromCalibrations(String filePath) {
        ArrayList<Calibration> calibrationList = new ArrayList<>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(filePath));

            doc.getDocumentElement().normalize();

            NodeList calibrationElements = doc.getElementsByTagName("Calibracion");

            for (int i = 0; i < calibrationElements.getLength(); i++) {
                Node calibrationNode = calibrationElements.item(i);

                if (calibrationNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element calibrationElement = (Element) calibrationNode;
                    String number = calibrationElement.getElementsByTagName("Serie").item(0).getTextContent();
                    String date = calibrationElement.getElementsByTagName("Fecha").item(0).getTextContent();
                    int id = Integer.parseInt(calibrationElement.getElementsByTagName("Numero").item(0).getTextContent());
                    int measurement = Integer.parseInt(calibrationElement.getElementsByTagName("Mediciones").item(0).getTextContent());

                    Calibration calibration = new Calibration(number, id, date, measurement);
                    calibrationList.add(calibration);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return calibrationList;
    }

public static void saveToXMLMeasurement(String filePath, List<Measurement> measurementList, int calibracionId) throws SAXException, IOException {
    if (measurementList == null || measurementList.isEmpty()) {
        throw new IllegalArgumentException("La lista de mediciones no puede ser nula ni estar vacía");
    }

    try {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc;
        File file = new File(filePath);
        doc = dBuilder.parse(file);

        Element laboratorio = doc.getDocumentElement();
        NodeList tipoInstrumentoList = laboratorio.getElementsByTagName("Tipo_de_instrumento");

        for (int i = 0; i < tipoInstrumentoList.getLength(); i++) {
            Element tipoDeInstrumento = (Element) tipoInstrumentoList.item(i);
            NodeList instrumentoList = tipoDeInstrumento.getElementsByTagName("Instrumento");

            for (int j = 0; j < instrumentoList.getLength(); j++) {
                Element instrumento = (Element) instrumentoList.item(j);
                NodeList calibracionList = instrumento.getElementsByTagName("Calibracion");

                for (int k = 0; k < calibracionList.getLength(); k++) {
                    Element calibracion = (Element) calibracionList.item(k);
                    Element numero = (Element) calibracion.getElementsByTagName("Numero").item(0);

                    // Verifica si el número de calibración coincide con el identificador de la calibración deseada
                    if (Integer.parseInt(numero.getTextContent()) == calibracionId) {
                        for (Measurement measurement : measurementList) {
                            Element measurementElement = doc.createElement("Medicion");

                            Element idMedi = doc.createElement("IdMedicion");
                            idMedi.setTextContent(String.valueOf(measurement.getIdMeasure()));

                            Element number = doc.createElement("Numero");
                            number.setTextContent(measurement.getCode());

                            Element medidaElement = doc.createElement("Medida");
                            medidaElement.setTextContent(Double.toString(measurement.getId()));

                            Element referenciaElement = doc.createElement("Referencia");
                            referenciaElement.setTextContent(Integer.toString(measurement.getReference()));

                            Element lecturaElement = doc.createElement("Lectura");
                            lecturaElement.setTextContent(measurement.getReading());
                            
                            

                            measurementElement.appendChild(idMedi);
                            measurementElement.appendChild(number);
                            measurementElement.appendChild(medidaElement);
                            measurementElement.appendChild(referenciaElement);
                            measurementElement.appendChild(lecturaElement);

                            calibracion.appendChild(measurementElement);

                            // Guardar los cambios en el archivo XML
                            TransformerFactory transformerFactory = TransformerFactory.newInstance();
                            Transformer transformer = transformerFactory.newTransformer();
                            DOMSource source = new DOMSource(doc);
                            StreamResult result = new StreamResult(file);
                            transformer.transform(source, result);

                        }
                        return; // Salir después de guardar las mediciones en la calibración correcta
                    }
                }
            }
        }

        // Si llegamos aquí, significa que no se encontró la calibración con el identificador especificado
        System.out.println("No se encontró la calibración con número: " + calibracionId);
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}


    // Método para cargar mediciones desde XML
   public static ArrayList<Measurement> loadFromMeasurement(String filePath, int calibrationNumber) throws IOException, SAXException, ParserConfigurationException {
    ArrayList<Measurement> measurementList = new ArrayList<>();

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File(filePath));

    Element rootElement = document.getDocumentElement();
    NodeList calibrationElements = rootElement.getElementsByTagName("Calibracion");

    for (int i = 0; i < calibrationElements.getLength(); i++) {
        Element calibrationElement = (Element) calibrationElements.item(i);
        int calibNumber = Integer.parseInt(calibrationElement.getElementsByTagName("Numero").item(0).getTextContent());

        // Verificar si el número de calibración coincide con el número deseado
        if (calibNumber == calibrationNumber) {
            NodeList measurementElements = calibrationElement.getElementsByTagName("Medicion");

            for (int j = 0; j < measurementElements.getLength(); j++) {
                Element measurementElement = (Element) measurementElements.item(j);
                int idMedi = Integer.parseInt(measurementElement.getElementsByTagName("IdMedicion").item(0).getTextContent());
                String code = measurementElement.getElementsByTagName("Numero").item(0).getTextContent();
                double id = Double.parseDouble(measurementElement.getElementsByTagName("Medida").item(0).getTextContent());
                int measurement = Integer.parseInt(measurementElement.getElementsByTagName("Referencia").item(0).getTextContent());
                String reading = measurementElement.getElementsByTagName("Lectura").item(0).getTextContent();

                Measurement measurements = new Measurement(code, id, measurement, reading, idMedi);
                measurementList.add(measurements);
            }
        }
    }

    return measurementList;
}


    // Método para actualizar mediciones en XML
    public static void updateMeasurement(String filePath, List<String> list, int calibrationNumber) {
        try {
            File archivoXML = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documento = builder.parse(archivoXML);
            Element raiz = documento.getDocumentElement();

            ArrayList<Measurement> listM = loadFromMeasurement(filePath,calibrationNumber);

            NodeList elementosMedicion = raiz.getElementsByTagName("Medicion");
            if (list.size() <= elementosMedicion.getLength()) {
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < listM.size(); j++) {
                        Measurement m = listM.get(j);
                        if (m.getIdMeasure() == Integer.valueOf(list.get(i))) {
                            Element elementoMedicion = (Element) elementosMedicion.item(j);
                            Element elementoLectura = (Element) elementoMedicion.getElementsByTagName("Lectura").item(0);
                            elementoLectura.setTextContent(list.get(0));
                            break;
                        }
                    }
                }

                // Guardar el documento XML actualizado en el archivo
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(documento);
                StreamResult result = new StreamResult(new File(filePath));
                transformer.transform(source, result);
                list.clear();
            } else {
                System.err.println("La cantidad de elementos en la lista no coincide con la cantidad de elementos en el XML.");
                list.clear();
            }
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            e.printStackTrace();
        }
    }


   
    // Método para buscar calibraciones por número en el archivo XML
    public static List<Element> findCalibrationsByNumber(String filePath, String numero) {
        List<Element> calibracionesEncontradas = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));

            Element rootElement = document.getDocumentElement();
            NodeList calibracionElements = rootElement.getElementsByTagName("Calibracion");

            for (int i = 0; i < calibracionElements.getLength(); i++) {
                Element calibracion = (Element) calibracionElements.item(i);
                Element serieElement = (Element) calibracion.getElementsByTagName("Serie").item(0);
                String serie = serieElement.getTextContent();

                if (serie.equals(numero)) {
                    calibracionesEncontradas.add(calibracion);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return calibracionesEncontradas;
    }
    
    public static void delete(String filePath, String instrumentCode, int calibrationNumber) {
    try {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc;
        File file = new File(filePath);
        doc = dBuilder.parse(file);

        Element laboratorio = doc.getDocumentElement();
        NodeList tipoInstrumentoList = laboratorio.getElementsByTagName("Tipo_de_instrumento");

        for (int i = 0; i < tipoInstrumentoList.getLength(); i++) {
            Element tipoDeInstrumento = (Element) tipoInstrumentoList.item(i);
            NodeList instrumentoList = tipoDeInstrumento.getElementsByTagName("Instrumento");

            for (int j = 0; j < instrumentoList.getLength(); j++) {
                Element instrumento = (Element) instrumentoList.item(j);
                String code = instrumento.getElementsByTagName("Serie").item(0).getTextContent();

                if (code.equals(instrumentCode)) {
                    NodeList calibracionList = instrumento.getElementsByTagName("Calibracion");

                    for (int k = 0; k < calibracionList.getLength(); k++) {
                        Element calibracion = (Element) calibracionList.item(k);
                        Element numero = (Element) calibracion.getElementsByTagName("Numero").item(0);

                        // Verifica si el número de calibración coincide con el número especificado
                        if (Integer.parseInt(numero.getTextContent()) == calibrationNumber) {
                            // Elimina la calibración
                            instrumento.removeChild(calibracion);

                            // Guarda los cambios en el archivo XML
                            TransformerFactory transformerFactory = TransformerFactory.newInstance();
                            Transformer transformer = transformerFactory.newTransformer();
                            DOMSource source = new DOMSource(doc);
                            StreamResult result = new StreamResult(file);
                            transformer.transform(source, result);

                            System.out.println("Calibración eliminada correctamente del instrumento con código: " + instrumentCode);
                            return; // Salir después de eliminar la calibración
                        }
                    }
                }
            }
        }

        // Si llegamos aquí, significa que no se encontró el instrumento con el código especificado
        System.out.println("No se encontró el instrumento con código: " + instrumentCode);
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}
}
