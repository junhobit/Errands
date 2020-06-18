package com.dasong.errands;

import java.util.Date;

public class List_Item {
    public int Num;
    public String Title;
    public String Start;
    public String Arrive;
    public String Detail;
    public long Date;
    public String Writer;
    public String Price;

    public List_Item() {

    }

    public List_Item(String title, String name, long date, String start, String arrive, String detail, String price){
        Num = Num;
        Title=title;
        Start=start;
        Arrive=arrive;
        Date=date;
        Writer=name;
        Detail=detail;
        Price=price;
    }

    public int getNum(){return Num;}

    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }

    public String getStart() {
        return Start;
    }
    public void setStart(String start) {
        Start = start;
    }

    public String getArrive() {
        return Arrive;
    }
    public void setArrive(String arrive) {
        Arrive = arrive;
    }

    public String getArrive_detail() {
        return Detail;
    }
    public void setArrive_detail(String detail) {
        Detail = detail;
    }

    public long getDate() {
        return Date;
    }
    public void setDate(long date) {
        Date = date;
    }

    public String getWriter() {
        return Writer;
    }
    public void setWriter(String writer) {
        Writer = writer;
    }

    public String getPrice() {
        return Price;
    }
    public void setPrice(String price) {
        Price = price;
    }
}
