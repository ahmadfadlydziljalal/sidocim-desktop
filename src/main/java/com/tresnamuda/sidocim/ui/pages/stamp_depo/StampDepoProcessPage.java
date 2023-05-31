/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.tresnamuda.sidocim.ui.pages.stamp_depo;

import com.tresnamuda.sidocim.App;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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
    private JProgressBar progressBar;
    private JDialog notificationDialog;

    /**
     * Creates new form StampDepoProcessPage
     */
    public StampDepoProcessPage() {

        initComponents();
        initFileChooser();
        initProgressBar();

        this.TopActionPanel.setPreferredSize(new Dimension(App.showWidth(), 50));
        this.TopActionPanel.setMaximumSize(new Dimension(App.showWidth(), 50));

    }

    private void initProgressBar() {
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
    }

    private void initFileChooser() {
        this.jPilihFileButton.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
                pathFileJTextField.setText(filePath);

                TablePanel.add(this.progressBar, BorderLayout.SOUTH);
                readExcelFileInBackground(selectedFile);

            } else if (returnValue == JFileChooser.CANCEL_OPTION) {
                pathFileJTextField.setText("File selection canceled.");
            }
        });
    }

    private void readExcelFileInBackground(File file) {
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);

                Sheet sheet = workbook.getSheetAt(0); // Assuming the first sheet

                // Create the table model
                tableModel = new DefaultTableModel();

                // Get the column headers from the first row
                Row headerRow = sheet.getRow(0);
                for (Cell cell : headerRow) {
                    tableModel.addColumn(cell.getStringCellValue());
                }

                int totalRows = sheet.getLastRowNum();
                int currentRow = 0;

                // Populate the rows in the table model
                for (int rowIndex = 1; rowIndex <= totalRows; rowIndex++) {
                    Row dataRow = sheet.getRow(rowIndex);
                    Object[] rowData = new Object[dataRow.getLastCellNum()];
                    for (int columnIndex = 0; columnIndex < dataRow.getLastCellNum(); columnIndex++) {
                        Cell cell = dataRow.getCell(columnIndex);

                        if (cell != null) {
                            if (cell.getCellType() == CellType.STRING) {
                                rowData[columnIndex] = cell.getStringCellValue();
                            } else if (cell.getCellType() == CellType.NUMERIC) {
                                rowData[columnIndex] = cell.getNumericCellValue();
                            }
                        }
                    }
                    tableModel.addRow(rowData);

                    currentRow = rowIndex;
                    int progress = (int) ((double) currentRow / totalRows * 100);
                    publish(progress);

                }

                workbook.close();

                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                // This method is executed on the UI main thread
                // Update the progress bar with the latest value
                int progress = chunks.get(chunks.size() - 1);
                progressBar.setValue(progress);
            }

            @Override
            protected void done() {
                // This method is executed on the UI main thread

                // Perform any additional UI updates or post-processing here
                table = new JTable(tableModel);

                // Optionally customize the JTable appearance or behavior
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Example: Disable auto-resizing

                // Add the JTable to your JPanel
                TablePanel.add(new JScrollPane(table));

                // Perform any additional UI updates or post-processing here
                progressBar.setValue(100); // Set the progress bar to 100% when done

                showNotificationDialog("Process Completed");

            }
        };

        worker.execute(); // Start the background task
    }

    private void showNotificationDialog(String message) {
        JFrame parentFrame = (JFrame) this.getRootPane().getParent(); 
        notificationDialog = new JDialog(parentFrame, "Notification", true);
        JLabel label = new JLabel(message);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        notificationDialog.getContentPane().add(label);
        notificationDialog.setSize(300, 200);
        notificationDialog.setLocationRelativeTo(parentFrame);
        notificationDialog.setVisible(true);
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
                .addComponent(pathFileJTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
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

        TablePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 255, 0), 4));
        TablePanel.setLayout(new java.awt.BorderLayout());
        add(TablePanel);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel TablePanel;
    private javax.swing.JPanel TopActionPanel;
    private javax.swing.JButton jPilihFileButton;
    private javax.swing.JTextField pathFileJTextField;
    // End of variables declaration//GEN-END:variables

}
