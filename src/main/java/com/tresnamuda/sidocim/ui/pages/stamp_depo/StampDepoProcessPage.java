/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.tresnamuda.sidocim.ui.pages.stamp_depo;

import com.tresnamuda.sidocim.App;
import com.tresnamuda.sidocim.models.ExcelFileReader;
import com.tresnamuda.sidocim.utils.CheckboxRenderer;
import com.tresnamuda.sidocim.utils.XLSFileFilter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
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
public class StampDepoProcessPage extends javax.swing.JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    /**
     * Creates new form StampDepoProcessPage
     */
    public StampDepoProcessPage() {
        initComponents();
        initTopActionPanel();
        initBottomActionPanel();
        initProgressPanel();
    }

    private void initTopActionPanel() {
        this.TopActionPanel.setPreferredSize(new Dimension(App.showWidth(), 50));
        this.TopActionPanel.setMaximumSize(new Dimension(App.showWidth(), 50));
        initFileChooser();
    }

    private void initBottomActionPanel() {
        this.BottomActionPanel.setPreferredSize(new Dimension(App.showWidth(), 30));
        this.BottomActionPanel.setMaximumSize(new Dimension(App.showWidth(), 30));
        this.BottomActionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    private void initProgressPanel() {
        this.ProgressPanel.setPreferredSize(new Dimension(App.showWidth(), 25));
        this.ProgressPanel.setMaximumSize(new Dimension(App.showWidth(), 25));
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
    }

    private void initFileChooser() {
        progressBar.setVisible(false);
        this.jPilihFileButton.addActionListener((ActionEvent e) -> {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new XLSFileFilter());

            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {

                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();

                pathFileJTextField.setText(filePath);
                progressBar.setVisible(true);
                readExcelFileInBackground(selectedFile);

            }

            if (returnValue == JFileChooser.CANCEL_OPTION) {
                pathFileJTextField.setText("File selection canceled.");
            }
        });
    }

    private void readExcelFileInBackground(File file) {
        SwingWorker<DefaultTableModel, Integer> worker = new SwingWorker<DefaultTableModel, Integer>() {

            @Override
            protected DefaultTableModel doInBackground() throws Exception {
                return ExcelFileReader.readExcelFile(file, new ExcelFileReader.ProgressListener() {
                    @Override
                    public void onProgressUpdated(int progress) {
                        publish(progress);
                    }
                });
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int progress = chunks.get(chunks.size() - 1);
                progressBar.setValue(progress);
            }

            @Override
            protected void done() {

                try {
                    tableModel = get();
                    repaintTablePanel();
                    repaintBottomActionPanel();

                    progressBar.setValue(100);
                    showNotificationDialog(file.getName() + " berhasil diload ...");

                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(StampDepoProcessPage.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        };

        // Start the background task
        worker.execute();
    }

    private void repaintTablePanel() {

        // Create table
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Render header checkbox
        TableColumn checkboxColumn = table.getColumnModel().getColumn(0);
        checkboxColumn.setHeaderRenderer(new CheckboxRenderer(table.getTableHeader()));

        // Repaint table panel
        TablePanel.removeAll();
        TablePanel.add(new JScrollPane(table));
        TablePanel.repaint();
        TablePanel.revalidate();
    }

    private void repaintBottomActionPanel() {

        JButton button = new JButton();
        button.setText("Print to PDF");
        button.setSize(new Dimension(10, 10));
        button.addActionListener((ActionEvent event) -> {
            handleToPrintPdf();
        });

        BottomActionPanel.removeAll();
        BottomActionPanel.add(button);
        BottomActionPanel.repaint();
        BottomActionPanel.revalidate();
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

    private void handleToPrintPdf() {
        // Get the selected rows
        int[] selectedRows = getSelectedRows();

        String filePath;

        // Generate the PDF file
        try (PDDocument document = new PDDocument()) {

            PDType1Font header1Font = PDType1Font.HELVETICA_BOLD;
            PDType1Font header2Font = PDType1Font.HELVETICA;
            PDType1Font contentFont = PDType1Font.HELVETICA;

            PDResources resources = new PDResources();
            resources.put(COSName.getPDFName("Font"), header1Font);

            // Print the selected rows
            for (int row : selectedRows) {

                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                page.setResources(resources);
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                    // Set header 1
                    contentStream.beginText();
                    contentStream.setFont(header1Font, 16);
                    contentStream.setLeading(14.5f);
                    contentStream.newLineAtOffset(25, 800);
                    contentStream.showText("PT. PELAYARAN TRESNAMUDA SEJATI");
                    contentStream.endText();

                    // Set header 2
                    contentStream.beginText();
                    contentStream.newLineAtOffset(25, 785);
                    contentStream.setFont(header2Font, 10);
                    contentStream.showText("Komplek Ruko Sunter Permai Indah - Jl. Mitra Sunter Boulevard. Block B No. 12-16.");
                    contentStream.newLine();
                    contentStream.showText("Jakarta Utara, 14360, Indonesia, Telp. +6221-6522333 (Hunting), Fax. +6221-6522336, 6522337");
                    contentStream.endText();

                    // Draw a horizontal line
                    contentStream.setLineWidth(1f);  // Set the line width
                    contentStream.moveTo(20, 760);  // Starting point of the line
                    contentStream.lineTo(page.getMediaBox().getWidth() - 25, 760);  // Ending point of the line
                    contentStream.stroke();  // Draw the line

                    // Set TITLE
                    contentStream.beginText();
                    contentStream.setFont(header1Font, 13);
                    contentStream.newLineAtOffset(20 * 8, 745);
                    contentStream.showText("Instruksi Pemulangan Kontainer Kosong");
                    contentStream.endText();

                    // Show some content of document here
                    contentStream.beginText();
                    contentStream.newLineAtOffset(25, 725);
                    contentStream.setFont(contentFont, 11);
                    contentStream.showText("DEPO: ");
                    contentStream.newLine();

                    // String to be auto-wrapped
                    String longString = "PT.TUNAS MITRA SELARAS ( TRAS ) Alamat : Kawasan Industri Cakung Remaja JL.Raya Rorotan Babek TNI Blok B No.07 Jakarta Utara 14140 Indonesia, Telp : 021 22946296";

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

                    contentStream.showText(line.toString().trim());
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
                    contentStream.showText("PERHATIAN !!!");
                    contentStream.newLine();
                    contentStream.showText("- Consignee / EMKL Harus Lakukan Cek Fisik dan ambil Photo Isotank Sebelum keluar CY");
                    contentStream.newLine();
                    contentStream.showText("- Apabila Terjadi Kerusakan / Kehilangan Part ( Aksesoris ) Isotank akibat kelalaian Pihak Importir maka EMKL");
                    contentStream.newLine();
                    contentStream.showText("  harus menyerahkan deposit seharga/ Senilai perbaikan kerusakan atau pergantian part");
                     contentStream.newLine();
                    contentStream.showText("  di kantor PT.Pelayaran Tresnamuda Sejati");

                    contentStream.endText();
                    contentStream.close();
                }
            }

            File file = new File(pathFileJTextField.getText());
            String extractedPath = FilenameUtils.removeExtension(
                    FilenameUtils.removeExtension(file.getName())
            );

            String directoryPath = System.getProperty("user.dir") + "/reports/" + extractedPath;
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            filePath = directoryPath + "/report.pdf";
            document.save(filePath);
            document.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showNotificationDialog(String message) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JOptionPane.showMessageDialog(parentFrame, message, "Notification", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TopActionPanel = new javax.swing.JPanel();
        jPilihFileButton = new javax.swing.JButton();
        pathFileJTextField = new javax.swing.JTextField();
        TablePanel = new javax.swing.JPanel();
        BottomActionPanel = new javax.swing.JPanel();
        ProgressPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jPilihFileButton.setText("Pilih File");

        pathFileJTextField.setText("....");

        javax.swing.GroupLayout TopActionPanelLayout = new javax.swing.GroupLayout(TopActionPanel);
        TopActionPanel.setLayout(TopActionPanelLayout);
        TopActionPanelLayout.setHorizontalGroup(
            TopActionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TopActionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPilihFileButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pathFileJTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                .addContainerGap())
        );
        TopActionPanelLayout.setVerticalGroup(
            TopActionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TopActionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TopActionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPilihFileButton)
                    .addComponent(pathFileJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(TopActionPanel);

        TablePanel.setLayout(new java.awt.BorderLayout());
        add(TablePanel);

        javax.swing.GroupLayout BottomActionPanelLayout = new javax.swing.GroupLayout(BottomActionPanel);
        BottomActionPanel.setLayout(BottomActionPanelLayout);
        BottomActionPanelLayout.setHorizontalGroup(
            BottomActionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 613, Short.MAX_VALUE)
        );
        BottomActionPanelLayout.setVerticalGroup(
            BottomActionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        add(BottomActionPanel);

        javax.swing.GroupLayout ProgressPanelLayout = new javax.swing.GroupLayout(ProgressPanel);
        ProgressPanel.setLayout(ProgressPanelLayout);
        ProgressPanelLayout.setHorizontalGroup(
            ProgressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProgressPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
                .addContainerGap())
        );
        ProgressPanelLayout.setVerticalGroup(
            ProgressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ProgressPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        add(ProgressPanel);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BottomActionPanel;
    private javax.swing.JPanel ProgressPanel;
    private javax.swing.JPanel TablePanel;
    private javax.swing.JPanel TopActionPanel;
    private javax.swing.JButton jPilihFileButton;
    private javax.swing.JTextField pathFileJTextField;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables

}
