package com.dasong.errands.model;

public class MapItem {
    public String AddressName;
    private String Posx;
    private String Posy;
    public String ID;
    public MapItem() {
    }

    public MapItem( String id, String addressName, String posx, String posy) {
        ID = id;
        AddressName = addressName;
        Posx = posx;
        Posy = posy;
    }

    public String getAddressName() {
        return AddressName;
    }

    public void setAddressName(String addressName) {
        AddressName = addressName;
    }

    public String getPosx() {
        return Posx;
    }

    public void setPosx(String posx) {
        Posx = posx;
    }

    public String getPosy() {
        return Posy;
    }

    public void setPosy(String posy) {
        Posy = posy;
    }

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        ID = id;
    }
}
