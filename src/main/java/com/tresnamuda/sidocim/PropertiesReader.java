/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tresnamuda.sidocim;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author dzil
 */
public class PropertiesReader {

    private Properties properties;
    
    public Properties getProperties(){
        return this.properties;
    }

    public PropertiesReader() throws IOException {
        init();
    }

    private void init() throws IOException {
        properties = new Properties();
        java.net.URL url = ClassLoader.getSystemResource("application.properties");
        properties.load(url.openStream());
    }

}
