/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tresnamuda.sidocim.ui.pages.stamp_depo;

import com.tresnamuda.sidocim.App;
import com.tresnamuda.sidocim.pojo.ContainerPojo;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

/**
 *
 * @author dzil
 */
public class PDFPageRenderer {

    
    private final PDType1Font header1Font = PDType1Font.HELVETICA_BOLD;
    private final PDType1Font header2Font = PDType1Font.HELVETICA;
    private final PDType1Font contentFont = PDType1Font.HELVETICA;

    private final PDPage page = new PDPage(PDRectangle.A4);
    private final PDPageContentStream contentStream;
    private final ContainerPojo containerPojo;

    public PDFPageRenderer(ContainerPojo containerPojo,PDPageContentStream contentStream) {
        this.containerPojo = containerPojo;
        this.contentStream = contentStream;

    }

    public void render() throws IOException {
        
        try (contentStream) {
            renderHeader();
            renderTitle();
            renderContent();
            contentStream.close();
        }
        


    }

    private void renderHeader() {
        try {
            // Set header 1
            contentStream.beginText();
            contentStream.setFont(header1Font, 16);
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(25, 800);
            contentStream.showText(App.readProperties().getProperty("application.namaperusahaan"));
            contentStream.endText();

            // Set header 2
            contentStream.beginText();
            contentStream.newLineAtOffset(25, 785);
            contentStream.setFont(header2Font, 10);
            contentStream.showText(renderLongText(App.readProperties().getProperty("application.alamatperusahaan")));
            contentStream.endText();

            // Draw a horizontal line
            contentStream.setLineWidth(1f);  // Set the line width
            contentStream.moveTo(20, 760);  // Starting point of the line
            contentStream.lineTo(page.getMediaBox().getWidth() - 25, 760);  // Ending point of the line
            contentStream.stroke();  // Draw the line
        } catch (IOException ex) {
            Logger.getLogger(PDFFileRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void renderTitle() {
        try {
            contentStream.beginText();
            contentStream.setFont(header1Font, 13);
            contentStream.newLineAtOffset(20 * 8, 745);
            contentStream.showText("Instruksi Pemulangan Kontainer Kosong");
            contentStream.endText();
        } catch (IOException ex) {
            Logger.getLogger(PDFFileRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void renderContent() {
        try {

            // Show some content of document here
            contentStream.beginText();
            contentStream.newLineAtOffset(25, 725);
            contentStream.setFont(contentFont, 11);

            contentStream.showText("DEPO: ");
            contentStream.newLine();
            contentStream.showText(
                    renderLongText(
                            App.readProperties().getProperty("customer.depo.TRAS.nama")
                            + " Alamat : "
                            + App.readProperties().getProperty("customer.depo.TRAS.alamat")
                    )
            );
            contentStream.endText();

            // Build the table
            Table myTable = Table.builder()
                    .addColumnsOfWidth(25, 87, 100, 88, 75, 87, 87)
                    .padding(2)
                    .addRow(
                            Row.builder()
                                    .add(TextCell.builder().text("NO").horizontalAlignment(HorizontalAlignment.CENTER).fontSize(10).borderWidth(1).build())
                                    .add(TextCell.builder().text("Nomor Kontainer").horizontalAlignment(HorizontalAlignment.CENTER).fontSize(10).borderWidth(1).build())
                                    .add(TextCell.builder().text("B" + "\\" + "L").horizontalAlignment(HorizontalAlignment.CENTER).fontSize(10).borderWidth(1).build())
                                    .add(TextCell.builder().text("Vessel").horizontalAlignment(HorizontalAlignment.CENTER).fontSize(10).borderWidth(1).build())
                                    .add(TextCell.builder().text("ETA").horizontalAlignment(HorizontalAlignment.CENTER).fontSize(10).borderWidth(1).build())
                                    .add(TextCell.builder().text("Consignee").horizontalAlignment(HorizontalAlignment.CENTER).fontSize(10).borderWidth(1).build())
                                    .add(TextCell.builder().text("Commodity").horizontalAlignment(HorizontalAlignment.CENTER).fontSize(10).borderWidth(1).build())
                                    .build())
                    .addRow(
                            Row.builder()
                                    .padding(2)
                                    .add(TextCell.builder().text("1").horizontalAlignment(HorizontalAlignment.RIGHT).fontSize(10).borderWidth(1).borderWidthTop(0).build())
                                    .add(TextCell.builder().text(containerPojo.getNomor()).fontSize(10).borderWidth(1).borderWidthTop(0).build())
                                    .add(TextCell.builder().text(containerPojo.getBl()).fontSize(10).borderWidth(1).borderWidthTop(0).build())
                                    .add(TextCell.builder().text(containerPojo.getVessel()).fontSize(10).borderWidth(1).borderWidthTop(0).build())
                                    .add(TextCell.builder().text(containerPojo.getEta()).fontSize(10).borderWidth(1).borderWidthTop(0).build())
                                    .add(TextCell.builder().text(containerPojo.getConsignee()).fontSize(10).borderWidth(1).borderWidthTop(0).build())
                                    .add(TextCell.builder().text(containerPojo.getCommodity()).fontSize(10).borderWidth(1).borderWidthTop(0).build())
                                    .build()
                    )
                    .build();

            // Set up the drawer
            TableDrawer tableDrawer = TableDrawer.builder()
                    .contentStream(contentStream)
                    .startX(20f)
                    .startY(680)
                    .table(myTable)
                    .build();

            // And go for it!
            tableDrawer.draw();

            contentStream.beginText();

            contentStream.newLineAtOffset(25, (tableDrawer.getFinalY() - 30));
            contentStream.setFont(contentFont, 10);
            contentStream.showText(App.readProperties().getProperty("application.stampdepo.footer.line_1"));

            contentStream.newLine();
            contentStream.showText(App.readProperties().getProperty("application.stampdepo.footer.line_2"));

            contentStream.newLine();
            contentStream.showText(
                    renderLongText(
                            App.readProperties().getProperty("application.stampdepo.footer.line_3")
                    )
            );

            contentStream.endText();

        } catch (IOException ex) {
            Logger.getLogger(PDFFileRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String renderLongText(String longString) throws IOException {

        // Width of the text box
        float textBoxWidth = page.getMediaBox().getWidth() - 25;  // Adjust the width as needed

        // Auto-wrap the long string to the next line
        float fontSize = 11;
        float leading = 14.5f;

        String[] words = longString.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            float stringWidth = fontSize * contentFont.getStringWidth(line + " " + word) / 1000;
            if (stringWidth > textBoxWidth) {
                contentStream.showText(line.toString().trim());
                contentStream.newLineAtOffset(0, -leading);
                line = new StringBuilder(word);
            } else {
                line.append(" ").append(word);
            }
        }

        return line.toString().trim();

    }

}
