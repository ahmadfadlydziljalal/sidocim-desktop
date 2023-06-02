/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tresnamuda.sidocim.utils;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


/**
 *
 * @author dzil
 */
public class CheckboxRenderer extends JCheckBox implements TableCellRenderer {

    
    public CheckboxRenderer(JTableHeader header) {
        
        setHorizontalAlignment(JLabel.CENTER);
        setOpaque(true);
        setSelected(true);

        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = ((JTableHeader) e.getSource()).getTable();
                TableColumnModel columnModel = table.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                int modelColumn = table.convertColumnIndexToModel(viewColumn);
                if (modelColumn == 0) {
                    
                    setSelected(!isSelected());
                    TableModel m = table.getModel();
                    
                    Boolean f = isSelected();
                    for (int i = 0; i < m.getRowCount(); i++) {
                        m.setValueAt(f, i, 0);
                    }
                    ((JTableHeader) e.getSource()).repaint();
                }
            }
        });
        
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }

}
