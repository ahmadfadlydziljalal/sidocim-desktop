/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.tresnamuda.sidocim.ui.pages;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.tresnamuda.sidocim.App;
import java.awt.FlowLayout;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author dzil
 */
public class OuterPanel extends javax.swing.JPanel {

    private final JPanel titlePanel;
    private final JPanel contentPanel;
    private final String titleString;

    /**
     * Creates new form OuterPanel
     * @param contentPanel
     * @param title
     */
    public OuterPanel(JPanel contentPanel, String title) {
        this.titlePanel = new JPanel();
        this.contentPanel = contentPanel;
        this.titleString = title;
        init();
    }

    private void init() {
        
        //  Set outer layout as Box Layout
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));  
        
        // Set JPanel layout, with specific height
        this.titlePanel.setPreferredSize(new Dimension(App.showWidth(), 60));
        this.titlePanel.setMaximumSize(new Dimension(App.showWidth(), 60));
        
        this.titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Set page title
        JLabel titleLabel = new JLabel(titleString, JLabel.LEFT);
        titleLabel.setBorder(new EmptyBorder(14,20,10,0));
        titleLabel.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:$Menu.header.font;"
        );
        
        // Add title to title-panel
        this.titlePanel.add(titleLabel);

        // Set content panel, with remaining space
        // this.contentPanel.setBackground(Color.DARK_GRAY);

        // Add all those panels
        add(this.titlePanel);
        add(this.contentPanel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
