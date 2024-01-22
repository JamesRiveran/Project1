/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentation_Model;

import Presentation_Model.Calibration;
import Presentation.Controller.ViewController;
import static Presentation.Controller.ViewController.showMessage;
import Presentation.View.Modulo;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;


/**
 *
 * @author 50686
 */
public class GeneratorPDF {
    
     public static ArrayList<InstrumentType> loadTypeOfInstrument(JTable jTable) {
         ArrayList<InstrumentType> instrumentList = new ArrayList<>();
        instrumentList.clear();
        int rowCount = jTable.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            String code = (String) jTable.getValueAt(i, 0);
            String name = (String) jTable.getValueAt(i, 1);
            String unit = String.valueOf(jTable.getValueAt(i, 2).toString());
            InstrumentType instrument = new InstrumentType(code, unit, name);
            instrumentList.add(instrument);
        }
        return instrumentList;
    }

    public static ArrayList<InstrumentModulo2> loadInstrument(JTable jTable) {
        ArrayList<InstrumentModulo2> instrumentList = new ArrayList<>();

        int rowCount = jTable.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            String serie = (String) jTable.getValueAt(i, 0);
            String description = (String) jTable.getValueAt(i, 1);
            String min = (String) jTable.getValueAt(i, 2);
            String max = (String) jTable.getValueAt(i, 3);
            String tolerance = (String) jTable.getValueAt(i, 4);

            InstrumentModulo2 instrument = new InstrumentModulo2(serie, min, tolerance, description, max);
            instrumentList.add(instrument);
        }

        return instrumentList;
    }
    
    public static ArrayList<Calibration> loadCalibration(JTable jTable) {
        ArrayList<Calibration> instrumentList = new ArrayList<>();

        int rowCount = jTable.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            int id = (int)jTable.getValueAt(i, 0);
            String date = (String) jTable.getValueAt(i, 1);
            int measurement = (int)jTable.getValueAt(i, 2);
            Calibration calibration = new Calibration(date, measurement, id);
            instrumentList.add(calibration);
        }

        return instrumentList;
    }

    public static <T> void generatePDFReport(List<T> itemList, String filePath, String modulo)
            throws DocumentException, FileNotFoundException {
        try {
            Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        // Abre el documento para escritura
        document.open();

        // Agrega el encabezado con la fecha, hora y título
        addHeader(document,modulo);

        // Agrega la lista de elementos al documento
        switch (modulo) {
            case "modulo_1" -> addInstrumentList(document, (ArrayList<InstrumentType>) itemList);
            case "modulo_2" -> addInstrumentListForInstrument(document, (ArrayList<InstrumentModulo2>) itemList);
            case "modulo_3" -> addForCalibration(document, (ArrayList<Calibration>) itemList);
            default -> {
            }
        }

        // Cierra el documento
        document.close();
        }catch(DocumentException ex){
            ex.printStackTrace();
        }
    }

    private static void addHeader(Document document, String modulo) throws DocumentException {
        Date currentDate = new Date();

        String dateTimeFormat = "dd-MM-yyyy HH:mm:ss";
        String formattedDate = new java.text.SimpleDateFormat(dateTimeFormat).format(currentDate);

        Paragraph header = new Paragraph("Fecha y hora: " + formattedDate, FontFactory.getFont(FontFactory.HELVETICA, 12));
        header.setAlignment(Element.ALIGN_RIGHT);
        document.add(header);
        document.add(Chunk.NEWLINE);

        Paragraph title = new Paragraph("Sistema de Laboratorio Industrial", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        switch (modulo) {
            case "modulo_1" -> {
                document.add(Chunk.NEWLINE);
                Paragraph subTitle = new Paragraph("Tipos de instrumentos", FontFactory.getFont(FontFactory.HELVETICA, 14));
                subTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(subTitle);
                // Agrega un espacio en blanco después del título
                document.add(Chunk.NEWLINE);
            }

            case "modulo_2" -> {
                document.add(Chunk.NEWLINE);
                Paragraph subTitle = new Paragraph("Instrumentos", FontFactory.getFont(FontFactory.HELVETICA, 14));
                subTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(subTitle);
                // Agrega un espacio en blanco después del título
                document.add(Chunk.NEWLINE);
            }
            case "modulo_3" -> {
                document.add(Chunk.NEWLINE);
                Paragraph subTitle = new Paragraph("Calibraciones", FontFactory.getFont(FontFactory.HELVETICA, 14));
                subTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(subTitle);
                // Agrega un espacio en blanco después del título
                document.add(Chunk.NEWLINE);
            }
            default -> {
            }
        }

    }

    private static void addInstrumentList(Document document, ArrayList<InstrumentType> instrumentList)
            throws DocumentException {
        // Crea una tabla con tres columnas

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100); // La tabla ocupa el 100% del ancho disponible

        // Encabezados de columna con fondo de color #911414
        PdfPCell headerCell1 = new PdfPCell(new Phrase("Código", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        headerCell1.setBackgroundColor(new BaseColor(145, 20, 20));

        PdfPCell headerCell2 = new PdfPCell(new Phrase("Nombre", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        headerCell2.setBackgroundColor(new BaseColor(145, 20, 20));

        PdfPCell headerCell3 = new PdfPCell(new Phrase("Unidad", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        headerCell3.setBackgroundColor(new BaseColor(145, 20, 20));

        // Alineación de los encabezados de columna
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Agrega los encabezados a la tabla
        table.addCell(headerCell1);
        table.addCell(headerCell2);
        table.addCell(headerCell3);

        // Agrega la lista de instrumentos a la tabla con fondo de color #c88989
        for (InstrumentType instrument : instrumentList) {
            PdfPCell codeCell = new PdfPCell(new Phrase(instrument.getCode(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            codeCell.setBackgroundColor(new BaseColor(200, 137, 137));

            PdfPCell nameCell = new PdfPCell(new Phrase(instrument.getName(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            nameCell.setBackgroundColor(new BaseColor(200, 137, 137));

            PdfPCell unitCell = new PdfPCell(new Phrase(instrument.getUnit(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            unitCell.setBackgroundColor(new BaseColor(200, 137, 137));

            // Añade celdas con la información de cada instrumento
            table.addCell(codeCell);
            table.addCell(nameCell);
            table.addCell(unitCell);
        }

        // Agrega la tabla al documento
        document.add(table);
    }

    private static void addInstrumentListForInstrument(Document document, ArrayList<InstrumentModulo2> instrumentList)
            throws DocumentException {
        // Crea una tabla con tres columnas
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100); // La tabla ocupa el 100% del ancho disponible

        // Encabezados de columna con fondo de color #911414
        PdfPCell headerCell1 = new PdfPCell(new Phrase("Serie", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        headerCell1.setBackgroundColor(new BaseColor(145, 20, 20));

        PdfPCell headerCell2 = new PdfPCell(new Phrase("Descripción", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        headerCell2.setBackgroundColor(new BaseColor(145, 20, 20));

        PdfPCell headerCell3 = new PdfPCell(new Phrase("Minímo", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        headerCell3.setBackgroundColor(new BaseColor(145, 20, 20));

        PdfPCell headerCell4 = new PdfPCell(new Phrase("Máximo", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        headerCell4.setBackgroundColor(new BaseColor(145, 20, 20));

        PdfPCell headerCell5 = new PdfPCell(new Phrase("Tolerancia", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        headerCell5.setBackgroundColor(new BaseColor(145, 20, 20));

        

        // Alineación de los encabezados de columna
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell5.setHorizontalAlignment(Element.ALIGN_CENTER);
        

        // Agrega los encabezados a la tabla
        table.addCell(headerCell1);
        table.addCell(headerCell2);
        table.addCell(headerCell3);
        table.addCell(headerCell4);
        table.addCell(headerCell5);
       

        // Agrega la lista de instrumentos a la tabla con fondo de color #c88989
        for (InstrumentModulo2 instrument : instrumentList) {
            PdfPCell seriCell = new PdfPCell(new Phrase(instrument.getSerie(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            seriCell.setBackgroundColor(new BaseColor(200, 137, 137));

            PdfPCell minCell = new PdfPCell(new Phrase(instrument.getDescri(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            minCell.setBackgroundColor(new BaseColor(200, 137, 137));

            PdfPCell toleCell = new PdfPCell(new Phrase(instrument.getMini(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            toleCell.setBackgroundColor(new BaseColor(200, 137, 137));

            PdfPCell desCell = new PdfPCell(new Phrase(instrument.getMaxi(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            desCell.setBackgroundColor(new BaseColor(200, 137, 137));

            PdfPCell maxCell = new PdfPCell(new Phrase(instrument.getTole(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            maxCell.setBackgroundColor(new BaseColor(200, 137, 137));

            

            // Añade celdas con la información de cada instrumento
            table.addCell(seriCell);
            table.addCell(minCell);
            table.addCell(toleCell);
            table.addCell(desCell);
            table.addCell(maxCell);
        }

        // Agrega la tabla al documento
        document.add(table);
    }
        private static void addForCalibration(Document document, ArrayList<Calibration> calibrationList)
            throws DocumentException {
        // Crea una tabla con tres columnas

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100); // La tabla ocupa el 100% del ancho disponible

        // Encabezados de columna con fondo de color #911414
        PdfPCell headerCell1 = new PdfPCell(new Phrase("Número", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        headerCell1.setBackgroundColor(new BaseColor(145, 20, 20));

        PdfPCell headerCell2 = new PdfPCell(new Phrase("Fecha", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        headerCell2.setBackgroundColor(new BaseColor(145, 20, 20));

        PdfPCell headerCell3 = new PdfPCell(new Phrase("Mediciones", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        headerCell3.setBackgroundColor(new BaseColor(145, 20, 20));

        // Alineación de los encabezados de columna
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Agrega los encabezados a la tabla
        table.addCell(headerCell1);
        table.addCell(headerCell2);
        table.addCell(headerCell3);

        // Agrega la lista de instrumentos a la tabla con fondo de color #c88989
        for (Calibration calibration : calibrationList) {
            PdfPCell NumberCell = new PdfPCell(new Phrase(String.valueOf(calibration.getNumberId()), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            NumberCell.setBackgroundColor(new BaseColor(200, 137, 137));

            PdfPCell dateCell = new PdfPCell(new Phrase(calibration.getDate(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            dateCell.setBackgroundColor(new BaseColor(200, 137, 137));

            PdfPCell measurementCell = new PdfPCell(new Phrase(String.valueOf(calibration.getMeasurement()), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            measurementCell.setBackgroundColor(new BaseColor(200, 137, 137));

            // Añade celdas con la información de cada instrumento
            table.addCell(NumberCell);
            table.addCell(dateCell);
            table.addCell(measurementCell);
        }

        // Agrega la tabla al documento
        document.add(table);
    }
}
