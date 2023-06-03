/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.tresnamuda.sidocim.ui.pages.stamp_depo;

import com.tresnamuda.sidocim.App;
import com.tresnamuda.sidocim.models.ExcelFileReader;
import com.tresnamuda.sidocim.utils.CheckboxRenderer;
import com.tresnamuda.sidocim.utils.XLSFileFilter;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

        // Print the selected rows
        for (int row : selectedRows) {
            generatePDF(row);
        }
    }

    private void generatePDF(int row) {

        // Generate the PDF file
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDType1Font font = PDType1Font.HELVETICA_BOLD;
            PDResources resources = new PDResources();
            resources.put(COSName.getPDFName("Font"), font);
            page.setResources(resources);

            String filePath;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(100, 700);

                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    Object value = tableModel.getValueAt(row, col);

                    if (value != null) {

                        contentStream.showText(value.toString());
                    }
                }

                contentStream.endText();
            }

            File file = new File(pathFileJTextField.getText());
            String extractedPath = FilenameUtils.removeExtension(
                    FilenameUtils.removeExtension(file.getName())
            );

            String directoryPath = System.getProperty("user.dir") + "/reports/" + extractedPath ;
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                 directory.mkdirs();
            }

            filePath = directoryPath + "/" + table.getValueAt(row, 2) + ".pdf";
            document.save(filePath);
            System.out.println("PDF file generated for row " + row + " at " + filePath);
            
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
