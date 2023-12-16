/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author james
 */
public class XMLLoader {

    public static ArrayList<InstrumentType> loadFromXML(String filePath) throws FileNotFoundException, IOException, JDOMException {
        ArrayList<InstrumentType> instrumentList = new ArrayList<>();

        SAXBuilder saxBuilder = new SAXBuilder(); // Puedes manejar estas excepciones de manera más específica según tus necesidades.
        Document document;
        try (FileReader reader = new FileReader(filePath)) {
            document = saxBuilder.build(reader);
        }
        Element rootElement = document.getRootElement();
        List<Element> instrumentElements = rootElement.getChildren("Tipo_de_instrumento");
        for (Element instrumentElement : instrumentElements) {
            String code = instrumentElement.getChildText("Codigo");
            String name = instrumentElement.getChildText("Nombre");
            String unit = instrumentElement.getChildText("Unidad");

            // Crea un objeto InstrumentType y agrégalo a la lista
            InstrumentType instrumentType = new InstrumentType(code, name, unit);
            instrumentList.add(instrumentType);
        }

        return instrumentList;
    }

    public static void saveToXML(String filePath, List<InstrumentType> instrumentList) {
        if (instrumentList == null || instrumentList.isEmpty()) {
            throw new IllegalArgumentException("La lista de instrumentos no puede ser nula ni estar vacía");
        }

        try {
            Document doc;

            // Verificar si el archivo ya existe
            File file = new File(filePath);
            if (file.exists()) {
                // Si el archivo existe, cargar el contenido existente
                SAXBuilder saxBuilder = new SAXBuilder();
                doc = saxBuilder.build(file);
            } else {
                // Si el archivo no existe, crear uno nuevo
                doc = new Document(new Element("Instrumentos"));
            }

            Element instruments = doc.getRootElement();

            // Agregar los nuevos instrumentos
            for (InstrumentType instrument : instrumentList) {
                // Verificar si el tipo de instrumento ya existe en el archivo
                Element existingInstrument = findInstrumentByCode(instruments, instrument.getCode());

                if (existingInstrument == null) {
                    // Si no existe, crear uno nuevo y agregarlo al elemento raíz
                    Element typeInstrument = new Element("Tipo_de_instrumento");

                    Element code = new Element("Codigo");
                    code.setText(instrument.getCode());
                    Element name = new Element("Nombre");
                    name.setText(instrument.getName());
                    Element unit = new Element("Unidad");
                    unit.setText(instrument.getUnit());

                    typeInstrument.addContent(code);
                    typeInstrument.addContent(name);
                    typeInstrument.addContent(unit);

                    instruments.addContent(typeInstrument);
                } else {
                    // Si ya existe, actualizar sus datos según sea necesario
                    existingInstrument.getChild("Nombre").setText(instrument.getName());
                    existingInstrument.getChild("Unidad").setText(instrument.getUnit());
                }
            }

            XMLOutputter xml = new XMLOutputter();
            xml.setFormat(Format.getPrettyFormat());

            try (FileWriter writer = new FileWriter(filePath)) {
                xml.output(doc, writer);
            }
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }
    }

    private static Element findInstrumentByCode(Element instruments, String code) {
        // Buscar un elemento por su código dentro del elemento raíz
        for (Element typeInstrument : instruments.getChildren("Tipo_de_instrumento")) {
            if (typeInstrument.getChildText("Codigo").equals(code)) {
                return typeInstrument;
            }
        }
        return null;
    }

    /*Para el modulo 2*/
    public static void addToXML(String filePath, List<InstrumentModulo2> instrumentList) {
        if (instrumentList == null || instrumentList.isEmpty()) {
            throw new IllegalArgumentException("La lista de instrumentos no puede ser nula ni estar vacía");
        }

        try {
            Document doc;

            // Verificar si el archivo ya existe
            File file = new File(filePath);
            if (file.exists()) {
                // Si el archivo existe, cargar el contenido existente
                SAXBuilder saxBuilder = new SAXBuilder();
                doc = saxBuilder.build(file);
            } else {
                // Si el archivo no existe, crear uno nuevo
                doc = new Document(new Element("Instrumentos"));
            }

            Element instruments = doc.getRootElement();

            // Agregar los nuevos instrumentos
            for (InstrumentModulo2 instrument : instrumentList) {
                // Verificar si el tipo de instrumento ya existe en el archivo
                Element existingInstrument = findInstrumentBySerie(instruments, instrument.getSerie());

                if (existingInstrument == null) {
                    // Si no existe, crear uno nuevo y agregarlo al elemento raíz
                    Element typeInstrument = new Element("Instrumento");
                    Element serie = new Element("Serie");
                    serie.setText(instrument.getSerie());
                    Element min = new Element("Minimo");
                    min.setText(instrument.getMini());
                    Element tole = new Element("Tolerancia");
                    tole.setText(instrument.getTole());
                    Element descrip = new Element("Descripcion");
                    descrip.setText(instrument.getDescri());
                    Element max = new Element("Maximo");
                    max.setText(instrument.getMaxi());
                    Element type = new Element("Tipo");
                    type.setText(instrument.getType().toString());

                    typeInstrument.addContent(serie);
                    typeInstrument.addContent(min);
                    typeInstrument.addContent(tole);
                    typeInstrument.addContent(descrip);
                    typeInstrument.addContent(max);
                    typeInstrument.addContent(type);
                    instruments.addContent(typeInstrument);
                } else {
                    // Si ya existe, actualizar sus datos según sea necesario
                    existingInstrument.getChild("Serie").setText(instrument.getSerie());
                    existingInstrument.getChild("Minimo").setText(instrument.getMini());
                    existingInstrument.getChild("Tolerancia").setText(instrument.getTole());
                    existingInstrument.getChild("Descripcion").setText(instrument.getDescri());
                    existingInstrument.getChild("Maximo").setText(instrument.getMaxi());
                    existingInstrument.getChild("Tipo").setText(instrument.getType());

                }
            }

            XMLOutputter xml = new XMLOutputter();
            xml.setFormat(Format.getPrettyFormat());

            try (FileOutputStream fos = new FileOutputStream(filePath); OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8); BufferedWriter writer = new BufferedWriter(osw)) {
                xml.output(doc, writer);
            }
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteInstrumentsFromXML(String filePath, InstrumentModulo2 instrumentToDelete) throws IOException, JDOMException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(new File(filePath));

        Element rootElement = document.getRootElement();
        List<Element> instrumentElements = rootElement.getChildren("Instrumento");

        for (Element instrumentElement : instrumentElements) {
            String code = instrumentElement.getChildText("Serie");

            // Verifica si el código coincide con el que se quiere eliminar
            if (code.equals(instrumentToDelete.getSerie())) {
                // Elimina el elemento del XML
                instrumentElement.getParentElement().removeContent(instrumentElement);
                break;  // Puedes salir del bucle si se eliminó el elemento
            }
        }
        // Guarda los cambios en el archivo XML
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            xmlOutputter.output(document, new FileWriter(filePath));
        } finally {
            // Cierra el XMLOutputter fuera del bloque try
            try {
                xmlOutputter.output(document, new FileWriter(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Validacion para comprobar si el tipo de instrumento ya existe, se actualizarán sus datos en lugar de crear uno nuevo.
    private static Element findInstrumentBySerie(Element instruments, String code) {
        // Buscar un elemento por su código dentro del elemento raíz
        for (Element typeInstrument : instruments.getChildren("Instrumento")) {
            if (typeInstrument.getChildText("Serie").equals(code)) {
                return typeInstrument;
            }
        }
        return null;
    }

    public static ArrayList<InstrumentModulo2> loadFromXMLS(String filePath) throws FileNotFoundException, IOException, JDOMException {
        ArrayList<InstrumentModulo2> instruments = new ArrayList<>();

        SAXBuilder saxBuilder = new SAXBuilder(); // Puedes manejar estas excepciones de manera más específica según tus necesidades.
        Document document;
        try (FileInputStream fis = new FileInputStream(filePath); InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            document = saxBuilder.build(isr);
        }
        Element rootElement = document.getRootElement();
        List<Element> instrumentElements = rootElement.getChildren("Instrumento");
        for (Element instrumentElement : instrumentElements) {
            String serie = instrumentElement.getChildText("Serie");
            String min = instrumentElement.getChildText("Minimo");
            String descrip = instrumentElement.getChildText("Descripcion");
            String tole = instrumentElement.getChildText("Tolerancia");
            String maxi = instrumentElement.getChildText("Maximo");
            String type = instrumentElement.getChildText("Tipo");

            // Crea un objeto InstrumentType y agrégalo a la lista
            InstrumentModulo2 instrumentModulo2 = new InstrumentModulo2(serie, min, tole, descrip, maxi, type);
            instruments.add(instrumentModulo2);
        }

        return instruments;
    }
}
