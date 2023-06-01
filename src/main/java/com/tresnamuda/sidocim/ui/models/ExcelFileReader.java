/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tresnamuda.sidocim.ui.models;

import java.io.File;
import java.io.FileInputStream;
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
public class ExcelFileReader {

    public interface ProgressListener {
        void onProgressUpdated(int progress);
    }

    public static DefaultTableModel readExcelFile(File file, ProgressListener listener) throws Exception {
        try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming the first sheet

            // Create the table model
            DefaultTableModel tableModel = new DefaultTableModel();

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
                // Publish progress if needed
                listener.onProgressUpdated(progress);
               
            }

            return tableModel;
        }
    }

}
