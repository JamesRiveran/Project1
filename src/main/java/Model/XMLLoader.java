/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Controller.ViewController;
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

            if (file.exists()) {
                // Si el archivo existe, cargar el contenido existente
                doc = (Document) builder.parse(file);
            } else {
                // Si el archivo no existe, crear uno nuevo
                DocumentBuilderFactory newFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder newBuilder = newFactory.newDocumentBuilder();
                doc = (Document) newBuilder.newDocument();
                doc.appendChild(doc.createElement("Instrumentos"));
            }

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

    public static void addToXML(String filePath, List<InstrumentModulo2> instrumentList) {
        if (instrumentList == null || instrumentList.isEmpty()) {
            throw new IllegalArgumentException("La lista de instrumentos no puede ser nula ni estar vacía");
        }

        try {
            Document doc;

            // Verificar si el archivo ya existe
            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            if (file.exists()) {
                // Si el archivo existe, cargar el contenido existente
                doc = (Document) builder.parse(file);
            } else {
                // Si el archivo no existe, crear uno nuevo
                doc = (Document) builder.newDocument();
                Element rootElement = doc.createElement("Instrumentos");
                    doc.appendChild(rootElement);
            }

            Element instruments = doc.getDocumentElement();

            // Agregar los nuevos instrumentos
            for (InstrumentModulo2 instrument : instrumentList) {
                // Verificar si el tipo de instrumento ya existe en el archivo
                Element existingInstrument = findInstrumentBySerie(instruments, instrument.getSerie());

                if (existingInstrument == null) {
                    // Si no existe, crear uno nuevo y agregarlo al elemento raíz
                    Element typeInstrument = doc.createElement("Instrumento");
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
                    type.setTextContent(instrument.getType().toString());

                    typeInstrument.appendChild(serie);
                    typeInstrument.appendChild(min);
                    typeInstrument.appendChild(tole);
                    typeInstrument.appendChild(descrip);
                    typeInstrument.appendChild(max);
                    typeInstrument.appendChild(type);

                    instruments.appendChild(typeInstrument);
                } else {
                    // Si ya existe, actualizar sus datos según sea necesario
                    existingInstrument.getElementsByTagName("Serie").item(0).setTextContent(instrument.getSerie());
                    existingInstrument.getElementsByTagName("Minimo").item(0).setTextContent(instrument.getMini());
                    existingInstrument.getElementsByTagName("Tolerancia").item(0).setTextContent(instrument.getTole());
                    existingInstrument.getElementsByTagName("Descripcion").item(0).setTextContent(instrument.getDescri());
                    existingInstrument.getElementsByTagName("Maximo").item(0).setTextContent(instrument.getMaxi());
                    existingInstrument.getElementsByTagName("Tipo").item(0).setTextContent(instrument.getType().toString());
                }
            }

            // Guardar los cambios en el archivo
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource((Node) doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

            System.out.println("Archivo XML guardado correctamente con codificación UTF-8.");
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
                ViewController.showMessage(view,"El archivo no existe.", "error");
            }
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void saveToXMLCalibration(String filePath, List<Calibration> calibrationList) {
       if (calibrationList == null || calibrationList.isEmpty()) {
        throw new IllegalArgumentException("La lista de instrumentos no puede ser nula ni estar vacía");
    }

    try {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc;
        File file = new File(filePath);

        if (file.exists()) {
            // Si el archivo existe, cargar el contenido existente
            doc = builder.parse(file);
        } else {
            // Si el archivo no existe, crear uno nuevo
            doc = builder.newDocument();
            Element instrumentos = doc.createElement("Instrumentos");
            doc.appendChild(instrumentos);
        }

        Element instruments = doc.getDocumentElement();
        // Agregar los nuevos instrumentos
        for (Calibration calibration : calibrationList) {
            Element calibracion = doc.createElement("Calibracion");
            Element serie = doc.createElement("Serie");
            serie.setTextContent(calibration.getNumber());
            Element fecha = doc.createElement("Fecha");
            fecha.setTextContent(calibration.getDate());
            Element numero = doc.createElement("Numero");
            numero.setTextContent(Integer.toString(calibration.getId()));
            Element mediciones = doc.createElement("Mediciones");
            mediciones.setTextContent(Integer.toString(calibration.getMeasuring()));

            calibracion.appendChild(serie);
            calibracion.appendChild(fecha);
            calibracion.appendChild(numero);
            calibracion.appendChild(mediciones);

            instruments.appendChild(calibracion);
        }

        // Buscar la etiqueta <idCounter>
        Element idCounterElement = (Element) doc.getDocumentElement().getElementsByTagName("idCounter").item(0);
        int updatedIdCounter;
        if (idCounterElement == null) {
            // Si la etiqueta <idCounter> no existe, crear una nueva con valor 1
            idCounterElement = doc.createElement("idCounter");
            updatedIdCounter = 1;
            doc.getDocumentElement().appendChild(idCounterElement);
        } else {
            // Si la etiqueta <idCounter> existe, obtener el valor actual y aumentarlo en 1
            int currentIdCounter = Integer.parseInt(idCounterElement.getTextContent());
            updatedIdCounter = currentIdCounter + 1;
        }

        // Actualizar el valor del contador
        idCounterElement.setTextContent(Integer.toString(updatedIdCounter));

        // Guardar los cambios en el archivo XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        System.out.println("Archivo XML guardado correctamente con codificación UTF-8.");
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    public static void deleteInstrumentsFromXML(String filePath, InstrumentModulo2 instrumentToDelete) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));

            Element rootElement = doc.getDocumentElement();
            NodeList instrumentElements = rootElement.getElementsByTagName("Instrumento");

            for (int i = 0; i < instrumentElements.getLength(); i++) {
                Element instrumentElement = (Element) instrumentElements.item(i);
                String code = instrumentElement.getElementsByTagName("Serie").item(0).getTextContent();

                // Verifica si el código coincide con el que se quiere eliminar
                if (code.equals(instrumentToDelete.getSerie())) {
                    // Elimina el elemento del XML
                    rootElement.removeChild(instrumentElement);
                    break;  // Puedes salir del bucle si se elimina el elemento
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

    // Método para obtener el valor de idCounter desde XML
    public static int getIdCounterFromXML(String filePath) {
    try {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        try (InputStream inputStream = new FileInputStream(filePath)) {
            Document document = builder.parse(inputStream);

            Element rootElement = document.getDocumentElement();
            Element idCounterElement = (Element) rootElement.getElementsByTagName("idCounter").item(0);

            if (idCounterElement != null) {
                String idCounterText = idCounterElement.getTextContent();
                return Integer.parseInt(idCounterText);
            } else {
                throw new RuntimeException("La etiqueta <idCounter> no está presente en el archivo XML.");
            }
        }
    } catch (IOException | SAXException | ParserConfigurationException e) {
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

        if (file.exists()) {
            // Si el archivo existe, cargar el contenido existente
            doc = builder.parse(file);
        } else {
            // Si el archivo no existe, crear uno nuevo
            doc = builder.newDocument();
            Element laboratorio = doc.createElement("Laboratorio");
            doc.appendChild(laboratorio);
        }

        // Buscar la etiqueta <IdMedicionCounter>
        Element idMedicionCounterElement = (Element) doc.getDocumentElement().getElementsByTagName("IdMedicionCounter").item(0);

        if (idMedicionCounterElement == null) {
            // Si la etiqueta <IdMedicionCounter> no existe, crear una nueva y asignarle el valor predeterminado
            idMedicionCounterElement = doc.createElement("IdMedicionCounter");
            idMedicionCounterElement.setTextContent("1"); // Valor predeterminado
            doc.getDocumentElement().appendChild(idMedicionCounterElement);
        }

        // Guardar los cambios en el archivo XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        System.out.println("Se ha actualizado el valor de <IdMedicionCounter> en el archivo XML.");

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
            newIdCounterElement.appendChild(doc.createTextNode("1")); // Valor predeterminado
            rootElement.appendChild(newIdCounterElement);
        }

        // Guardar los cambios en el archivo XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);

        System.out.println("Se ha actualizado el valor de <idCounter> en el archivo XML.");
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

    public static void saveToXMLMeasurement(String filePath, List<Measurement> measurementList) {
    if (measurementList == null || measurementList.isEmpty()) {
        throw new IllegalArgumentException("La lista de mediciones no puede ser nula ni estar vacía");
    }

    try {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc;
        File file = new File(filePath);

        if (file.exists()) {
            // Si el archivo existe, cargar el contenido existente
            doc = dBuilder.parse(file);
        } else {
            // Si el archivo no existe, crear uno nuevo
            doc = dBuilder.newDocument();
            Element medicionesElement = doc.createElement("Mediciones");
            doc.appendChild(medicionesElement);
        }

        // Buscar el elemento <IdMedicionCounter>
        Element idMedicionElement = (Element) doc.getElementsByTagName("IdMedicionCounter").item(0);
        int updatedIdMedicion;

        if (idMedicionElement == null) {
            // Si el elemento <IdMedicionCounter> no existe, crear uno nuevo con valor 1
            idMedicionElement = doc.createElement("IdMedicionCounter");
            updatedIdMedicion = 1;
            doc.getDocumentElement().appendChild(idMedicionElement);
        } else {
            // Si el elemento <IdMedicionCounter> existe, obtener el valor actual y aumentarlo en 1
            int currentIdMedicion = Integer.parseInt(idMedicionElement.getTextContent());
            updatedIdMedicion = currentIdMedicion + 1;
        }

        // Actualizar el valor del contador
        idMedicionElement.setTextContent(Integer.toString(updatedIdMedicion));

        Element mediciones = doc.getDocumentElement();

        // Agregar las nuevas mediciones
        for (Measurement measurement : measurementList) {
            Element measurementElement = doc.createElement("Medicion");

            Element idMedi = doc.createElement("IdMedicion");
            idMedi.setTextContent(String.valueOf(measurement.getIdMeasure()));

            Element number = doc.createElement("Numero");
            number.setTextContent(measurement.getCode());

            Element medidaElement = doc.createElement("Medida");
            medidaElement.setTextContent(Double.toString(measurement.getId()));

            Element referenciaElement = doc.createElement("Referencia");
            referenciaElement.setTextContent(Double.toString(measurement.getReference()));

            Element lecturaElement = doc.createElement("Lectura");
            lecturaElement.setTextContent(Double.toString(measurement.getReading()));

            measurementElement.appendChild(idMedi);
            measurementElement.appendChild(number);
            measurementElement.appendChild(medidaElement);
            measurementElement.appendChild(referenciaElement);
            measurementElement.appendChild(lecturaElement);

            mediciones.appendChild(measurementElement);
        }

        // Guardar los cambios en el archivo XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        System.out.println("Se ha guardado el archivo XML de mediciones.");
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    }

       // Método para cargar mediciones desde XML
    public static ArrayList<Measurement> loadFromMeasurement(String filePath) throws IOException, SAXException, ParserConfigurationException {
        ArrayList<Measurement> measurementList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(filePath));

        Element rootElement = document.getDocumentElement();
        NodeList measurementElements = rootElement.getElementsByTagName("Medicion");

        for (int i = 0; i < measurementElements.getLength(); i++) {
            Element measurementElement = (Element) measurementElements.item(i);
            int idMedi = Integer.parseInt(measurementElement.getElementsByTagName("IdMedicion").item(0).getTextContent());
            String code = measurementElement.getElementsByTagName("Numero").item(0).getTextContent();
            double id = Double.parseDouble(measurementElement.getElementsByTagName("Medida").item(0).getTextContent());
            double measurement = Double.parseDouble(measurementElement.getElementsByTagName("Referencia").item(0).getTextContent());
            double reading = Double.parseDouble(measurementElement.getElementsByTagName("Lectura").item(0).getTextContent());

            Measurement measurements = new Measurement(code, id, measurement, reading, idMedi);
            measurementList.add(measurements);
        }

        return measurementList;
    }

    // Método para actualizar mediciones en XML
    public static void updateMeasurement(String filePath, List<String> list) {
        try {
            File archivoXML = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documento = builder.parse(archivoXML);
            Element raiz = documento.getDocumentElement();

            ArrayList<Measurement> listM = loadFromMeasurement(filePath);

            System.out.println("Esti es en el xml " + list);
            NodeList elementosMedicion = raiz.getElementsByTagName("Medicion");
            if (list.size() <= elementosMedicion.getLength()) {
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < listM.size(); j++) {
                        Measurement m = listM.get(j);
                        if (m.getIdMeasure() == Double.valueOf(list.get(i))){
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
                System.out.println("Valores de Lectura actualizados con éxito.");
                list.clear();
            } else {
                System.err.println("La cantidad de elementos en la lista no coincide con la cantidad de elementos en el XML.");
                list.clear();
            }
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

    // Método para eliminar mediciones del archivo XML
    public static void deleteDataMensu(String filePath, String serie) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));

            Element rootElement = document.getDocumentElement();
            NodeList instrumentElements = rootElement.getElementsByTagName("Medicion");

            // Almacena las mediciones que se eliminarán
            List<Element> elementsToRemove = new ArrayList<>();

            for (int i = 0; i < instrumentElements.getLength(); i++) {
                Element instrumentElement = (Element) instrumentElements.item(i);
                String code = instrumentElement.getElementsByTagName("Numero").item(0).getTextContent();

                if (code.equals(serie)) {
                    elementsToRemove.add(instrumentElement);
                }
            }

            // Elimina las mediciones después de completar la iteración
            for (Element elementToRemove : elementsToRemove) {
                rootElement.removeChild(elementToRemove);
            }

            // Guarda los cambios en el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

    // Método para eliminar registros de calibraciones del archivo XML
    public static void deleteData(String filePath, String serie) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));

            Element rootElement = document.getDocumentElement();
            NodeList instrumentElements = rootElement.getElementsByTagName("Calibracion");

            for (int i = 0; i < instrumentElements.getLength(); i++) {
                Element instrumentElement = (Element) instrumentElements.item(i);
                String code = instrumentElement.getElementsByTagName("Numero").item(0).getTextContent();

                if (code.equals(serie)) {
                    rootElement.removeChild(instrumentElement);
                }
            }

            // Guardar los cambios en el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
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

}
