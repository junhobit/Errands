package com.dasong.errands.model;

public class Chat_Item{
    public String ID;
    public String Title;
    public String Okname;
    public String Point;

    public Chat_Item(String id,String title) {
        ID = id;
        Title=title;
    }

    public Chat_Item(String id,String title,String name, String Price){
        ID = id;
        Title=title;
        Okname=name;
        Point=Price;

    }
    public String getID(){return ID;}
    public void setID(String id) {ID=id;}

    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }

    public String getOkname() {
        return Okname;
    }

    public void setOkname(String okname) {
        Okname = okname;
    }

    public String getPoint() {
        return Point;
    }

    public void setPoint(String point) {
        Point = point;
    }
}