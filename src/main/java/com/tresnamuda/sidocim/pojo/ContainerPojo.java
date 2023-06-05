/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tresnamuda.sidocim.pojo;

/**
 *
 * @author dzil
 */
public class ContainerPojo {
    
    private String nomor;
    private String bl;
    private String vessel;
    private String eta;
    private String consignee;
    private String commodity;

    public ContainerPojo(String nomor, String bl, String vessel, String eta, String consignee, String commodity) {
        this.nomor = nomor;
        this.bl = bl;
        this.vessel = vessel;
        this.eta = eta;
        this.consignee = consignee;
        this.commodity = commodity;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getBl() {
        return bl;
    }

    public void setBl(String bl) {
        this.bl = bl;
    }

    public String getVessel() {
        return vessel;
    }

    public void setVessel(String vessel) {
        this.vessel = vessel;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }
    
    
    
}
