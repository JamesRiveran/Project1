/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author james
 */
public class GeneratorPDF {

    public static void generatePDFReport(ArrayList<InstrumentType> instrumentList, String filePath)
            throws DocumentException, FileNotFoundException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        // Abre el documento para escritura
        document.open();

        // Agrega el encabezado con la fecha, hora y título
        addHeader(document);

        // Agrega la lista de instrumentos al documento
        addInstrumentList(document, instrumentList);

        // Cierra el documento
        document.close();
    }

    private static void addHeader(Document document) throws DocumentException {
        //try {
            
            Date currentDate = new Date();
            
            String dateTimeFormat = "dd-MM-yyyy HH:mm:ss";
            String formattedDate = new java.text.SimpleDateFormat(dateTimeFormat).format(currentDate);

            Paragraph header = new Paragraph("Fecha y hora: " + formattedDate, FontFactory.getFont(FontFactory.HELVETICA, 12));
            header.setAlignment(Element.ALIGN_RIGHT);
            document.add(header);
            /*
            document.add(Chunk.NEWLINE);

            String imagePath = "/resource/Icon.png";
            Image image;

            image = Image.getInstance(imagePath);
            
            image.scaleToFit(200, 100);
            image.setAlignment(Element.ALIGN_CENTER);

            document.add(image);*/

            document.add(Chunk.NEWLINE);

            Paragraph title = new Paragraph("Sistema de Laboratorio Industrial", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Agrega un espacio en blanco después del título
            document.add(Chunk.NEWLINE);
        /*} catch (IOException e) {
            Logger.getLogger(GeneratorPDF.class.getName()).log(Level.SEVERE, null, e);
        }*/
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

}