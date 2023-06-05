/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.tresnamuda.sidocim.ui.pages.stamp_depo;

import com.tresnamuda.sidocim.App;
import com.tresnamuda.sidocim.models.ExcelFileReader;
import com.tresnamuda.sidocim.utils.CheckboxRenderer;
import com.tresnamuda.sidocim.utils.ICEPdfHelper;
import com.tresnamuda.sidocim.utils.XLSFileFilter;
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
        initActionPDFViewerAcion();
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

    private void initActionPDFViewerAcion() {
        this.ActionPDFViewer.setPreferredSize(new Dimension(App.showWidth(), 30));
        this.ActionPDFViewer.setMaximumSize(new Dimension(App.showWidth(), 30));
        this.ActionPDFViewer.setLayout(new FlowLayout(FlowLayout.LEFT));
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
            PDFRenderer renderer = new PDFRenderer(tableModel, pathFileJTextField.getText());
            renderer.handleToPrintPdf();
            openPdf(renderer.getPdfreportFile(), false);
        });

        BottomActionPanel.removeAll();
        BottomActionPanel.add(button);
        BottomActionPanel.repaint();
        BottomActionPanel.revalidate();
    }

    private void openPdf(String file, Boolean isOpenInNewFrame) {

        if (isOpenInNewFrame) {
            PDFViewerStampDepo pdfvsd = new PDFViewerStampDepo(file);
            pdfvsd.setVisible(true);
        } else {
            CardPanel1.setVisible(false);
            CardPanel2.setVisible(true);

            ICEPdfHelper icepdf = new ICEPdfHelper(file);
            PDFViewer.setViewportView(icepdf.getViewerPanel());
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

        CardPanel1 = new javax.swing.JPanel();
        TopActionPanel = new javax.swing.JPanel();
        jPilihFileButton = new javax.swing.JButton();
        pathFileJTextField = new javax.swing.JTextField();
        TablePanel = new javax.swing.JPanel();
        BottomActionPanel = new javax.swing.JPanel();
        ProgressPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        CardPanel2 = new javax.swing.JPanel();
        PDFViewer = new javax.swing.JScrollPane();
        ActionPDFViewer = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setName(""); // NOI18N
        setLayout(new java.awt.CardLayout());

        CardPanel1.setLayout(new javax.swing.BoxLayout(CardPanel1, javax.swing.BoxLayout.Y_AXIS));

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
                .addComponent(pathFileJTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
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

        CardPanel1.add(TopActionPanel);

        TablePanel.setLayout(new java.awt.BorderLayout());
        CardPanel1.add(TablePanel);

        javax.swing.GroupLayout BottomActionPanelLayout = new javax.swing.GroupLayout(BottomActionPanel);
        BottomActionPanel.setLayout(BottomActionPanelLayout);
        BottomActionPanelLayout.setHorizontalGroup(
            BottomActionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 625, Short.MAX_VALUE)
        );
        BottomActionPanelLayout.setVerticalGroup(
            BottomActionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 395, Short.MAX_VALUE)
        );

        CardPanel1.add(BottomActionPanel);

        javax.swing.GroupLayout ProgressPanelLayout = new javax.swing.GroupLayout(ProgressPanel);
        ProgressPanel.setLayout(ProgressPanelLayout);
        ProgressPanelLayout.setHorizontalGroup(
            ProgressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProgressPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                .addContainerGap())
        );
        ProgressPanelLayout.setVerticalGroup(
            ProgressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ProgressPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        CardPanel1.add(ProgressPanel);

        add(CardPanel1, "card6");

        CardPanel2.setLayout(new javax.swing.BoxLayout(CardPanel2, javax.swing.BoxLayout.Y_AXIS));
        CardPanel2.add(PDFViewer);

        jButton1.setText("Back");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ActionPDFViewerLayout = new javax.swing.GroupLayout(ActionPDFViewer);
        ActionPDFViewer.setLayout(ActionPDFViewerLayout);
        ActionPDFViewerLayout.setHorizontalGroup(
            ActionPDFViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ActionPDFViewerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap(543, Short.MAX_VALUE))
        );
        ActionPDFViewerLayout.setVerticalGroup(
            ActionPDFViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ActionPDFViewerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addGap(0, 419, Short.MAX_VALUE))
        );

        CardPanel2.add(ActionPDFViewer);

        add(CardPanel2, "card4");
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        CardPanel1.setVisible(true);
        CardPanel2.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ActionPDFViewer;
    private javax.swing.JPanel BottomActionPanel;
    private javax.swing.JPanel CardPanel1;
    private javax.swing.JPanel CardPanel2;
    private javax.swing.JScrollPane PDFViewer;
    private javax.swing.JPanel ProgressPanel;
    private javax.swing.JPanel TablePanel;
    private javax.swing.JPanel TopActionPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jPilihFileButton;
    private javax.swing.JTextField pathFileJTextField;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables

}
