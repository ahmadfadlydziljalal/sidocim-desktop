/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tresnamuda.sidocim.models;

import java.io.File;
import java.io.FileInputStream;
import javax.swing.JCheckBox;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
public class ExcelFileReader {

    public interface ProgressListener {

        void onProgressUpdated(int progress);
    }

    public static DefaultTableModel readExcelFile(File file, ProgressListener listener) throws Exception {

        try (FileInputStream fis = new FileInputStream(file)) {

            Workbook workbook;

            if (file.getName().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (file.getName().endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new IllegalArgumentException("Unsupported file format");
            }

            // Assuming the first sheet
            Sheet sheet = workbook.getSheetAt(0);

            // Get the column headers from the first row
            Row headerRow = sheet.getRow(0);

            // Create the table model
            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) {
                        return Boolean.class; // Use Boolean class for checkbox column
                    } else {
                        
                        // Get the first cell in the header row to determine the data type
                        CellType cellType = headerRow.getCell(columnIndex - 1).getCellType();
                        if (null == cellType) {
                            return Object.class;
                        } else return switch (cellType) {
                            case STRING -> String.class;
                            case NUMERIC -> Double.class;
                            default -> Object.class;
                        };
                    }
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 0; // Make checkbox column editable
                }

            };

            // Add the check/uncheck all in first header row
            tableModel.addColumn(new JCheckBox());

            // Add Header Cell
            for (Cell cell : headerRow) {
                tableModel.addColumn(cell.getStringCellValue());
            }

            // Add Body Cell
            int totalRows = sheet.getLastRowNum();
            int currentRow = 0;

            // Populate the rows in the table model
            for (int rowIndex = 1; rowIndex <= totalRows; rowIndex++) {
                Row dataRow = sheet.getRow(rowIndex);

                // Add 1 for the checkbox column
                Object[] rowData = new Object[dataRow.getLastCellNum() + 1];

                // Set initial checkbox state to false
                rowData[0] = false;

                for (int columnIndex = 0; columnIndex < dataRow.getLastCellNum(); columnIndex++) {

                    Cell cell = dataRow.getCell(columnIndex);

                    if (cell != null) {
                        if (cell.getCellType() == CellType.STRING) {
                            rowData[columnIndex + 1] = cell.getStringCellValue();
                        } else if (cell.getCellType() == CellType.NUMERIC) {
                            double numericValue = cell.getNumericCellValue();

                            // Check if the value is an integer
                            if (numericValue == Math.floor(numericValue) && !Double.isInfinite(numericValue)) {
                                rowData[columnIndex + 1] = (int) numericValue; // Convert to int
                            } else {
                                rowData[columnIndex + 1] = numericValue; // Keep it as double
                            }
                        }
                    }
                }
                tableModel.addRow(rowData);

                currentRow = rowIndex;
                int progress = (int) ((double) currentRow / totalRows * 100);

                // Publish progress
                listener.onProgressUpdated(progress);
            }

            return tableModel;
        }
    }

}
