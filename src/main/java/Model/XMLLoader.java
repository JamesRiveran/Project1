/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
            Element instruments = new Element("Instrumentos");
            Document doc = new Document(instruments);

            for (InstrumentType instrument : instrumentList) {
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
            }

            XMLOutputter xml = new XMLOutputter();
            xml.setFormat(Format.getPrettyFormat());

            // Utilizar try-with-resources para asegurar el cierre adecuado del recurso
            try (FileWriter writer = new FileWriter(filePath)) {
                xml.output(doc, writer);
            }
        } catch (IOException ex) {
            // Manejar la excepción específica y loguear el error
            ex.printStackTrace();
        }
    }
}



