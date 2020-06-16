package com.dasong.errands;

public class Chat_Item{
    public String ID;
    public String Title;

    public Chat_Item() {

    }

    public Chat_Item(String id,String title){
        ID = id;
        Title=title;

    }
    public String getID(){return ID;}
    public void setID(String id) {ID=id;}

    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }
}