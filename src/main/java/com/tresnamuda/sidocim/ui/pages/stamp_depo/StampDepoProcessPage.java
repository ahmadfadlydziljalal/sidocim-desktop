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
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;


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

    private void initBottomActionPanel(){
        this.BottomActionPanel.setPreferredSize(new Dimension(App.showWidth(), 50));
        this.BottomActionPanel.setMaximumSize(new Dimension(App.showWidth(), 50));
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
                    renderTable();
                    
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
    
    private void renderTable() {
   
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
