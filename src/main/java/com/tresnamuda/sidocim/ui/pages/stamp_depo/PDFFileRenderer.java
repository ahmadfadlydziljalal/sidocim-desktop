/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tresnamuda.sidocim.ui.pages.stamp_depo;

import com.tresnamuda.sidocim.pojo.ContainerPojo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 *
 * @author dzil
 */
public class PDFFileRenderer {

    private final DefaultTableModel tableModel;
    private final String pathExcelFile;
    private String pathPdfReportFile;

    public PDFFileRenderer(DefaultTableModel tableModel, String excelFile) {
        this.tableModel = tableModel;
        this.pathExcelFile = excelFile;
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

    private String saveFile(PDDocument document) {
        try {
            try (document) {
                File file = new File(pathExcelFile);
                String extractedPath = FilenameUtils.removeExtension(
                        FilenameUtils.removeExtension(file.getName())
                );
                
                String directoryPath = System.getProperty("user.dir") + "/reports/" + extractedPath;
                File directory = new File(directoryPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                
                this.pathPdfReportFile = directoryPath + "/report.pdf";
                document.save(this.pathPdfReportFile);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(PDFFileRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return this.pathPdfReportFile;

    }

    public String handleToPrintPdf() {

        // Get baris yang dipilih pada table model
        int[] selectedRows = getSelectedRows();

        // Generate sebuah PDF FILE
        PDDocument document = new PDDocument();
        try {
            // Ulangi sebanyak baris yang dipilih
            for (int row : selectedRows) {

                // Buat per page
                PDPage page = new PDPage(PDRectangle.A4);

                // Isi content page tersebut
                PDFPageRenderer pageRenderer = new PDFPageRenderer(
                        page,
                        new PDPageContentStream(document, page),
                        new ContainerPojo(
                                tableModel.getValueAt(row, 2).toString(),
                                tableModel.getValueAt(row, 3).toString(),
                                tableModel.getValueAt(row, 12).toString(),
                                tableModel.getValueAt(row, 8).toString(),
                                tableModel.getValueAt(row, 13).toString(),
                                tableModel.getValueAt(row, 4).toString()
                        )
                );
                pageRenderer.render();
                pageRenderer.close();

                // Add page tersebut ke dalam document PDF File tersebut
                document.addPage(page);
            }

        } catch (IOException ex) {
            Logger.getLogger(PDFFileRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Terakhir, simpan file pdf yang digenerate 
        return saveFile(document);

    }

}
