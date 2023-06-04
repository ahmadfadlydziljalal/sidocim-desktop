/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tresnamuda.sidocim.utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author dzil
 */
public class XLSFileFilter extends FileFilter {

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }

        String extension = getFileExtension(file);
        return extension != null && (extension.equals("xls") || extension.equals("xlsx"));
    }

    @Override
    public String getDescription() {
        return "Microsoft Excel Files (*.xls, *.xlsx)";
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < name.length() - 1) {
            return name.substring(lastDotIndex + 1).toLowerCase();
        }
        return null;
    }
}
