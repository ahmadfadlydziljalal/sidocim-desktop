/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tresnamuda.sidocim.ui.pages.stamp_depo;

import com.tresnamuda.sidocim.App;
import com.tresnamuda.sidocim.pojo.ContainerPojo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
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
public class PDFFileRenderer {

    private final DefaultTableModel tableModel;
    private final String excelFile;
    private String pdfReportFile;
    private final PDType1Font header1Font = PDType1Font.HELVETICA_BOLD;
    private final PDType1Font header2Font = PDType1Font.HELVETICA;
    private final PDType1Font contentFont = PDType1Font.HELVETICA;

    public String getExcelFile() {
        return this.excelFile;
    }

    public String getPdfreportFile() {
        return this.pdfReportFile;
    }

    public PDFFileRenderer(DefaultTableModel tableModel, String excelFile) {
        this.tableModel = tableModel;
        this.excelFile = excelFile;
    }

    private int[] getSelectedRows() {
        List<Integer> selectedRowsList = new ArrayList<>();

        // Iterate over the table model
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            // Check the value of the checkbox column for each row
            boolean selected = (boolean) tableModel.getValueAt(row, 0);
            if (selected) {
                // Add the selected row index to the list
                selectedRowsList.add(row);
            }
        }

        // Convert the list to an array
        int[] selectedRows = new int[selectedRowsList.size()];
        for (int i = 0; i < selectedRowsList.size(); i++) {
            selectedRows[i] = selectedRowsList.get(i);
        }

        return selectedRows;
    }

    private void renderHeader(PDPage page, PDPageContentStream contentStream) {
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
            contentStream.showText(renderLongText(page, contentStream, App.readProperties().getProperty("application.alamatperusahaan")));
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
//

    private void renderTitle(PDPageContentStream contentStream) {
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

    private String renderLongText(PDPage page, PDPageContentStream contentStream, String longString) throws IOException {

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
//

    private void renderContent(int row, PDPage page, PDPageContentStream contentStream) {
        try {

            // Show some content of document here
            contentStream.beginText();
            contentStream.newLineAtOffset(25, 725);
            contentStream.setFont(contentFont, 11);

            contentStream.showText("DEPO: ");
            contentStream.newLine();
            contentStream.showText(
                    renderLongText(
                            page,
                            contentStream,
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
                                    .add(TextCell.builder().text(tableModel.getValueAt(row, 2).toString()).fontSize(10).borderWidth(1).borderWidthTop(0).build())
                                    .add(TextCell.builder().text(tableModel.getValueAt(row, 3).toString()).fontSize(10).borderWidth(1).borderWidthTop(0).build())
                                    .add(TextCell.builder().text(tableModel.getValueAt(row, 12).toString()).fontSize(10).borderWidth(1).borderWidthTop(0).build())
                                    .add(TextCell.builder().text(tableModel.getValueAt(row, 8).toString()).fontSize(10).borderWidth(1).borderWidthTop(0).build())
                                    .add(TextCell.builder().text(tableModel.getValueAt(row, 13).toString()).fontSize(10).borderWidth(1).borderWidthTop(0).build())
                                    .add(TextCell.builder().text(tableModel.getValueAt(row, 4).toString()).fontSize(10).borderWidth(1).borderWidthTop(0).build())
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
                            page,
                            contentStream,
                            App.readProperties().getProperty("application.stampdepo.footer.line_3")
                    )
            );

            contentStream.endText();

        } catch (IOException ex) {
            Logger.getLogger(PDFFileRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void saveFile(PDDocument document) {
        File file = new File(excelFile);
        String extractedPath = FilenameUtils.removeExtension(
                FilenameUtils.removeExtension(file.getName())
        );

        String directoryPath = System.getProperty("user.dir") + "/reports/" + extractedPath;
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        this.pdfReportFile = directoryPath + "/report.pdf";
        try {
            document.save(this.pdfReportFile);
            document.close();
        } catch (IOException ex) {
            Logger.getLogger(PDFFileRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
//

    public void handleToPrintPdf() {

        // Get the selected rows
        int[] selectedRows = getSelectedRows();

        // Generate the PDF file
        try (PDDocument document = new PDDocument()) {

            PDResources resources = new PDResources();
            resources.put(COSName.getPDFName("Font"), header1Font);

            // Print the selected rows
            for (int row : selectedRows) {

                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                page.setResources(resources);

                ContainerPojo containerPojo = new ContainerPojo(
                        tableModel.getValueAt(row, 2).toString(),
                        tableModel.getValueAt(row, 3).toString(),
                        tableModel.getValueAt(row, 12).toString(),
                        tableModel.getValueAt(row, 8).toString(),
                        tableModel.getValueAt(row, 13).toString(),
                        tableModel.getValueAt(row, 4).toString()
                );

                PDFPageRenderer pageRenderer = new PDFPageRenderer(containerPojo,  new PDPageContentStream(document, page));
                pageRenderer.render();
                
            }

            saveFile(document);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public void handleToPrintPdf() throws IOException {
//
//        // Get the selected rows
//        int[] selectedRows = getSelectedRows();
//
//        PDDocument document = new PDDocument();
//         // Generate the PDF file
//       
//
//        // Print the selected rows
//        for (int row : selectedRows) {
//
//            ContainerPojo containerPojo = new ContainerPojo(
//                    tableModel.getValueAt(row, 2).toString(),
//                    tableModel.getValueAt(row, 3).toString(),
//                    tableModel.getValueAt(row, 12).toString(),
//                    tableModel.getValueAt(row, 8).toString(),
//                    tableModel.getValueAt(row, 13).toString(),
//                    tableModel.getValueAt(row, 4).toString()
//            );
//
//            PDFPageRenderer pageRenderer = new PDFPageRenderer(containerPojo, document);
//            pageRenderer.render();
//
//        }
//
//        saveFile(document);
//
//    }
}