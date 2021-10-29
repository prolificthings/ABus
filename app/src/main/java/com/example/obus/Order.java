package com.example.obus;

public class Order {
    private String itm_name, itm_state, itm_dist, itm_qnt, itm_uri, phne;

    public Order() {

    }


    public Order(String itm_name, String itm_state, String itm_dist, String itm_qnt, String itm_uri, String phne) {
        this.itm_name = itm_name;
        this.itm_state = itm_state;
        this.itm_dist = itm_dist;
        this.itm_qnt = itm_qnt;
        this.itm_uri = itm_uri;
        this.phne = phne;
    }

    public Order(String itm_name, String itm_qnt, String itm_uri) {
        this.itm_name = itm_name;
        this.itm_qnt = itm_qnt;
        this.itm_uri = itm_uri;
    }

    public String getItm_name() {
        return itm_name;
    }

    public void setItm_name(String itm_name) {
        this.itm_name = itm_name;
    }

    public String getItm_state() {
        return itm_state;
    }

    public void setItm_state(String itm_state) {
        this.itm_state = itm_state;
    }

    public String getItm_dist() {
        return itm_dist;
    }

    public void setItm_dist(String itm_dist) {
        this.itm_dist = itm_dist;
    }

    public String getItm_qnt() {
        return itm_qnt;
    }

    public void setItm_qnt(String itm_qnt) {
        this.itm_qnt = itm_qnt;
    }

    public String getItm_uri() {
        return itm_uri;
    }

    public void setItm_uri(String itm_uri) {
        this.itm_uri = itm_uri;
    }

    public String getPhne() {
        return phne;
    }

    public void setPhne(String phne) {
        this.phne = phne;
    }

}
