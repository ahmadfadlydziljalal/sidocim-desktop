/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tresnamuda.sidocim.utils;

import javax.swing.JPanel;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.MyAnnotationCallback;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

/**
 *
 * @author dzil
 */
public final class ICEPdfHelper {

    private SwingController controller;
    private final String pathFile;
    private JPanel viewerPanel;

    public ICEPdfHelper(String pathFile) {
        this.pathFile = pathFile;
        init();
    }

    public void init() {
        controller = new SwingController();
        SwingViewBuilder factory = new SwingViewBuilder(controller);
        
        viewerPanel = factory.buildViewerPanel();
        ComponentKeyBinding.install(controller, viewerPanel);
        controller.getDocumentViewController().setAnnotationCallback(
                new MyAnnotationCallback(
                        controller.getDocumentViewController()
                )
        );
        controller.openDocument(this.pathFile);
    }

    
    public JPanel getViewerPanel(){
        return viewerPanel;
    }
}
