/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Controller.ViewController;
import View.Modulo;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
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
public class XMLLoader extends ViewController {

    String filePath = "Laboratorio.xml";
    Modulo view;

    public static ArrayList<InstrumentType> loadFromXML(String filePath) throws FileNotFoundException, IOException, JDOMException {
        ArrayList<InstrumentType> instrumentList = new ArrayList<>();

        SAXBuilder saxBuilder = new SAXBuilder();
        Document document;
        try (FileInputStream fis = new FileInputStream(filePath); InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            document = saxBuilder.build(isr);

        }
        Element rootElement = document.getRootElement();
        List<Element> instrumentElements = rootElement.getChildren("Tipo_de_instrumento");
        for (Element instrumentElement : instrumentElements) {
            String code = instrumentElement.getChildText("Codigo");
            String name = instrumentElement.getChildText("Nombre");
            String unit = instrumentElement.getChildText("Unidad");

            // Crea un objeto InstrumentType y agrégalo a la lista
            InstrumentType instrumentType = new InstrumentType(code, unit, name);

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

            try (FileOutputStream fos = new FileOutputStream(filePath); OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8); BufferedWriter writer = new BufferedWriter(osw)) {
                xml.output(doc, writer);
            }
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteFromXML(String filePath, InstrumentType instrumentToDelete) throws IOException, JDOMException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(new File(filePath));
        ArrayList<InstrumentModulo2> listOfInstruments3 = loadFromXMLS(filePath);
        if (listOfInstruments3.isEmpty()) {
            Element rootElement = document.getRootElement();
            List<Element> instrumentElements = rootElement.getChildren("Tipo_de_instrumento");
            for (Element instrumentElement : instrumentElements) {
                String code = instrumentElement.getChildText("Codigo");

                // Verifica si el código coincide con el que se quiere eliminar
                if (code.equals(instrumentToDelete.getCode())) {
                    // Elimina el elemento del XML
                    instrumentElement.getParentElement().removeContent(instrumentElement);
                    ViewController.conection(false);
                    break;  // Puedes salir del bucle si se eliminó el elemento
                }
            }
        } else {
            Element rootElement = document.getRootElement();
            List<Element> instrumentElements = rootElement.getChildren("Tipo_de_instrumento");
            boolean foundMatch = false;
            for (InstrumentModulo2 name : listOfInstruments3) {
                System.out.println(name.toString());
                String type = name.getType();
                // Verifica si hay una coincidencia con el tipo
                if (type.equals(instrumentToDelete.getName())) {
                    foundMatch = true;
                    break;  // Puedes salir del bucle si se encuentra una coincidencia
                }
            }
            if (foundMatch) {
                ViewController.conection(true);
            } else {
                for (Element instrumentElement : instrumentElements) {
                    String code = instrumentElement.getChildText("Codigo");
                    // Verifica si el código coincide con el que se quiere eliminar
                    if (code.equals(instrumentToDelete.getCode())) {
                        // Elimina el elemento del XML
                        instrumentElement.getParentElement().removeContent(instrumentElement);
                        ViewController.conection(false);
                        break;  // Puedes salir del bucle si se eliminó el elemento
                    }
                }
            }
        }
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
    private static Element findInstrumentByCode(Element instruments, String code) {
        // Buscar un elemento por su código dentro del elemento raíz
        for (Element typeInstrument : instruments.getChildren("Tipo_de_instrumento")) {
            if (typeInstrument.getChildText("Codigo").equals(code)) {
                return typeInstrument;
            }
        }
        return null;
    }
//Para el modulo 2

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

    public static void updateInstrument(String filePath, String name, String newName) {
        try {
            Document doc;

            // Verificar si el archivo ya existe
            File file = new File(filePath);
            if (file.exists()) {
                // Si el archivo existe, cargar el contenido existente
                SAXBuilder saxBuilder = new SAXBuilder();
                doc = saxBuilder.build(file);

                // Obtener la lista de todos los elementos <Instrumento>
                List<Element> instrumentos = doc.getRootElement().getChildren("Instrumento");

                // Iterar a través de los elementos <Instrumento> y actualizar <Tipo> si coincide con el nombre
                for (Element instrumento : instrumentos) {
                    Element tipoElement = instrumento.getChild("Tipo");
                    String tipoValue = tipoElement.getText();

                    if (tipoValue.equals(name)) {
                        tipoElement.setText(newName);
                    }
                }
                // Guardar los cambios en el archivo
                XMLOutputter xmlOutput = new XMLOutputter();
                xmlOutput.setFormat(Format.getPrettyFormat());
                FileWriter fileWriter = new FileWriter(filePath);
                xmlOutput.output(doc, fileWriter);
                fileWriter.close();
            } else {
                ViewController.showMessage("El archivo no existe.", "error");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void saveToXMLCalibration(String filePath, List<Calibration> calibrationList) {
        if (calibrationList == null || calibrationList.isEmpty()) {
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
            for (Calibration calibration : calibrationList) {
                Element typeInstrument = new Element("Calibracion");
                Element series = new Element("Serie");
                series.setText(calibration.getNumber());
                Element date = new Element("Fecha");
                date.setText(calibration.getDate());
                Element number = new Element("Numero");
                number.setText(Integer.toString(calibration.getId()));
                Element measurement = new Element("Mediciones");
                measurement.setText(Integer.toString(calibration.getMeasuring()));

                typeInstrument.addContent(series);
                typeInstrument.addContent(date);
                typeInstrument.addContent(number);
                typeInstrument.addContent(measurement);

                instruments.addContent(typeInstrument);
            }

            // Buscar la etiqueta <idCounter>
            Element idCounterElement = doc.getRootElement().getChild("idCounter");
            int updatedIdCounter;
            if (idCounterElement == null) {
                // Si la etiqueta <idCounter> no existe, crear una nueva con valor 1
                idCounterElement = new Element("idCounter");
                updatedIdCounter = 1;
                doc.getRootElement().addContent(idCounterElement);
            } else {
                // Si la etiqueta <idCounter> existe, obtener el valor actual y aumentarlo en 1
                int currentIdCounter = Integer.parseInt(idCounterElement.getText());
                updatedIdCounter = currentIdCounter + 1;
            }

            // Actualizar el valor del contador
            idCounterElement.setText(Integer.toString(updatedIdCounter));

            // Guardar los cambios en el archivo XML
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            try (FileWriter writer = new FileWriter(filePath)) {
                xmlOutputter.output(doc, writer);
            }

            System.out.println("Se ha guardado y actualizado el archivo XML.");

        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteInstrumentsFromXML(String filePath, InstrumentModulo2 instrumentToDelete) throws IOException, JDOMException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(new File(filePath));
        ArrayList<Calibration> listOfcalibrations = loadFromCalibrations(filePath);
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

    public static int getIdMedicionFromXML(String filePath) throws IOException, JDOMException {
        try (FileInputStream fis = new FileInputStream(filePath); InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            Document document = new SAXBuilder().build(isr);
            Element rootElement = document.getRootElement();
            Element idMedicionElement = rootElement.getChild("IdMedicionCounter");

            int updatedIdMedicion;
            if (idMedicionElement == null) {
                // Si la etiqueta <IdMedicionCounter> no existe, crear una nueva con valor 1
                idMedicionElement = new Element("IdMedicionCounter");
                updatedIdMedicion = 1;
                rootElement.addContent(idMedicionElement);
            } else {
                // Si la etiqueta <IdMedicionCounter> existe, obtener el valor actual y aumentarlo en 1
                int currentIdMedicion = Integer.parseInt(idMedicionElement.getTextTrim());
                updatedIdMedicion = currentIdMedicion + 1;
                // Actualizar el valor del contador
                idMedicionElement.setText(Integer.toString(updatedIdMedicion));

                // Guardar los cambios en el archivo XML
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    XMLOutputter xmlOutputter = new XMLOutputter();
                    xmlOutputter.output(document, fos);
                }
            }

            return updatedIdMedicion;
        }
    }

    public static int getIdCounterFromXML(String filePath) throws IOException, JDOMException {
        try (FileInputStream fis = new FileInputStream(filePath); InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            Document document = new SAXBuilder().build(isr);
            Element rootElement = document.getRootElement();
            Element idCounterElement = rootElement.getChild("idCounter");

            if (idCounterElement != null) {
                String idCounterText = idCounterElement.getTextTrim();
                return Integer.parseInt(idCounterText);
            } else {
                throw new RuntimeException("La etiqueta <idCounter> no está presente en el archivo XML.");
            }
        }
    }

    public static void ensureIdMedicionExists(String filePath) {
        try {
            // Construir el documento XML desde el archivo existente
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document;
            File file = new File(filePath);
            document = saxBuilder.build(file);

            // Obtener el elemento raíz del documento
            Element rootElement = document.getRootElement();

            // Buscar la etiqueta <idCounter>
            Element idCounterElement = rootElement.getChild("IdMedicionCounter");

            if (idCounterElement == null) {
                // Si la etiqueta <idCounter> no existe, crear una nueva y asignarle el valor predeterminado
                Element newIdCounterElement = new Element("IdMedicionCounter");
                newIdCounterElement.setText("1"); // Valor predeterminado
                rootElement.addContent(newIdCounterElement);
            }

            // Guardar los cambios en el archivo XML
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            try (FileWriter writer = new FileWriter(filePath)) {
                xmlOutputter.output(document, writer);
            }

            System.out.println("Se ha actualizado el valor de <idMedicion> en el archivo XML.");

        } catch (IOException | org.jdom2.JDOMException e) {
            e.printStackTrace();
        }
    }

    public static void ensureIdCounterExists(String filePath) {
        try {
            // Construir el documento XML desde el archivo existente
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document;
            File file = new File(filePath);
            document = saxBuilder.build(file);

            // Obtener el elemento raíz del documento
            Element rootElement = document.getRootElement();

            // Buscar la etiqueta <idCounter>
            Element idCounterElement = rootElement.getChild("idCounter");

            if (idCounterElement == null) {
                // Si la etiqueta <idCounter> no existe, crear una nueva y asignarle el valor predeterminado
                Element newIdCounterElement = new Element("idCounter");
                newIdCounterElement.setText("1"); // Valor predeterminado
                rootElement.addContent(newIdCounterElement);
            }

            // Guardar los cambios en el archivo XML
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            try (FileWriter writer = new FileWriter(filePath)) {
                xmlOutputter.output(document, writer);
            }

            System.out.println("Se ha actualizado el valor de <idCounter> en el archivo XML.");

        } catch (IOException | org.jdom2.JDOMException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Calibration> loadFromCalibrations(String filePath) throws FileNotFoundException, IOException, JDOMException {
        ArrayList<Calibration> calibrationList = new ArrayList<>();
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document;
        try (FileInputStream fis = new FileInputStream(filePath); InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            document = saxBuilder.build(isr);
        }
        Element rootElement = document.getRootElement();
        List<Element> calibrationElements = rootElement.getChildren("Calibracion");
        for (Element calibrationtElement : calibrationElements) {
            String number = calibrationtElement.getChildText("Serie");
            String date = calibrationtElement.getChildText("Fecha");
            int id = Integer.parseInt(calibrationtElement.getChildText("Numero"));
            int measurement = Integer.parseInt(calibrationtElement.getChildText("Mediciones"));

            // Crea un objeto calibration y agrégalo a la lista
            Calibration calibration = new Calibration(number, id, date, measurement);
            calibrationList.add(calibration);
        }
        return calibrationList;
    }

    public static void saveToXMLMeasurement(String filePath, List<Measurement> measurementList) {
        if (measurementList == null || measurementList.isEmpty()) {
            throw new IllegalArgumentException("La lista de mediciones no puede ser nula ni estar vacía");
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
                doc = new Document(new Element("Mediciones"));
            }

            // Buscar la etiqueta <idCounter>
            Element idMedicionElement = doc.getRootElement().getChild("IdMedicionCounter");
            int updatedIdMedicion;
            if (idMedicionElement == null) {
                // Si la etiqueta <idCounter> no existe, crear una nueva con valor 1
                idMedicionElement = new Element("IdMedicionCounter");
                updatedIdMedicion = 1;
                doc.getRootElement().addContent(idMedicionElement);
            } else {
                // Si la etiqueta <idCounter> existe, obtener el valor actual y aumentarlo en 1
                int currentIdMedicion = Integer.parseInt(idMedicionElement.getText());
                updatedIdMedicion = currentIdMedicion + 1;
            }

            // Actualizar el valor del contador
            idMedicionElement.setText(Integer.toString(updatedIdMedicion));

            Element measurements = doc.getRootElement();
            // Agregar las nuevas mediciones
            for (Measurement measurement : measurementList) {
                Element measurementElement = new Element("Medicion");
                Element idMedi = new Element("IdMedicion");
                idMedi.setText(String.valueOf(measurement.getIdMeasure()));
                Element number = new Element("Numero");
                number.setText(measurement.getCode());
                Element medidaElement = new Element("Medida");
                medidaElement.setText(Double.toString(measurement.getId()));
                Element referenciaElement = new Element("Referencia");
                referenciaElement.setText(Double.toString(measurement.getReference()));
                Element lecturaElement = new Element("Lectura");
                lecturaElement.setText(Double.toString(measurement.getReading()));

                measurementElement.addContent(idMedi);
                measurementElement.addContent(number);
                measurementElement.addContent(medidaElement);
                measurementElement.addContent(referenciaElement);
                measurementElement.addContent(lecturaElement);

                measurements.addContent(measurementElement);
            }

            // Guardar los cambios en el archivo XML
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            try (FileWriter writer = new FileWriter(filePath)) {
                xmlOutputter.output(doc, writer);
            }

            System.out.println("Se ha guardado el archivo XML de mediciones.");

        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }
    }

    public static ArrayList<Measurement> loadFromMeasurement(String filePath) throws FileNotFoundException, IOException, JDOMException {
        ArrayList<Measurement> measurementList = new ArrayList<>();
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document;
        try (FileInputStream fis = new FileInputStream(filePath); InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            document = saxBuilder.build(isr);
        }
        Element rootElement = document.getRootElement();
        List<Element> calibrationElements = rootElement.getChildren("Medicion");
        for (Element calibrationtElement : calibrationElements) {
            int idMedi = Integer.parseInt(calibrationtElement.getChildText("IdMedicion"));
            String code = calibrationtElement.getChildText("Numero");
            double id = Double.parseDouble(calibrationtElement.getChildText("Medida"));
            double measurement = Double.parseDouble(calibrationtElement.getChildText("Referencia"));
            double reading = Double.parseDouble(calibrationtElement.getChildText("Lectura"));

            // Crea un objeto calibration y agrégalo a la lista
            Measurement measurements = new Measurement(code, id, measurement, reading, idMedi);
            measurementList.add(measurements);
        }
        return measurementList;
    }

    public static void updateMeasurement(String filePath, List<String> list) {
        try {
            File archivoXML = new File(filePath);
            Document documento = new SAXBuilder().build(archivoXML);
            Element raiz = documento.getRootElement();

            ArrayList<Measurement> listM = loadFromMeasurement(filePath);

            System.out.println("Esti es en el xml " + list);
            List<Element> elementosMedicion = raiz.getChildren("Medicion");
            if (list.size() <= elementosMedicion.size()) {
                for (int i = 0; i <= list.size(); i++) {
                    for (int j = 0; j <= listM.size(); j++) {
                        Measurement m = listM.get(j);
                        if (m.getIdMeasure() == Integer.parseInt(list.get(1))) {
                            Element elementoMedicion = elementosMedicion.get(j);
                            String nuevoValorDeLectura = list.get(0);
                            Element elementoLectura = elementoMedicion.getChild("Lectura");
                            elementoLectura.setText(nuevoValorDeLectura);
                            break;
                        }
                    }

                    break;

                }

//                 Guardar el documento XML actualizado en el archivo
                XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
                try (FileWriter writer = new FileWriter(filePath)) {
                    xmlOutput.output(documento, writer);
                    System.out.println("Valores de Lectura actualizados con éxito.");
                }
                list.clear();

            } else {
                System.err.println("La cantidad de elementos en la lista no coincide con la cantidad de elementos en el XML.");
                list.clear();

            }
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }

    }

    public static void deleteDataMensu(String filePath, String serie) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(new File(filePath));

            Element rootElement = document.getRootElement();
            List<Element> instrumentElements = rootElement.getChildren("Medicion");

            // Almacena las mediciones que se eliminarán
            List<Element> elementsToRemove = new ArrayList<>();

            for (Element instrumentElement : instrumentElements) {
                String code = instrumentElement.getChildText("Numero");

                if (code.equals(serie)) {
                    elementsToRemove.add(instrumentElement);
                }
            }

            // Elimina las mediciones después de completar la iteración
            instrumentElements.removeAll(elementsToRemove);

            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            try (FileWriter writer = new FileWriter(filePath)) {
                xmlOutputter.output(document, writer);
            }
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }
    }

    public static void deleteData(String filePath, String serie) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(new File(filePath));
            System.out.println(serie);

            Element rootElement = document.getRootElement();
            List<Element> instrumentElements = rootElement.getChildren("Calibracion");

            for (Element instrumentElement : instrumentElements) {
                String code = instrumentElement.getChildText("Numero");
                if (code.equals(serie)) {
//                // Elimina el elemento del XML
                    instrumentElement.getParentElement().removeContent(instrumentElement);
//                break;  // Puedes salir del bucle si se eliminó el elemento
                }
            }

            // Guardar los cambios en el archivo XML
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            FileWriter writer = new FileWriter(filePath);
            xmlOutputter.output(document, writer);
            writer.close();
//
            System.out.println("Registros eliminados con éxito.");
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }
    }

    public static List<Element> findCalibrationsByNumber(String filePath, String numero) {
        List<Element> calibracionesEncontradas = new ArrayList<>();

        try {
            // Crear un constructor SAX para construir un documento JDOM a partir del archivo XML
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(new File(filePath));

            // Obtener el elemento raíz (en este caso, <Calibraciones>)
            Element rootElement = document.getRootElement();

            // Obtener todas las calibraciones
            List<Element> calibraciones = rootElement.getChildren("Calibracion");

            // Iterar a través de las calibraciones y buscar las que coincidan con el número
            for (Element calibracion : calibraciones) {
                Element serieElement = calibracion.getChild("Serie");
                String serie = serieElement.getText();

                if (serie.equals(numero)) {
                    // Agregar la calibración a la lista de calibraciones encontradas
                    calibracionesEncontradas.add(calibracion);
                }
            }
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }

        return calibracionesEncontradas;
    }

}
