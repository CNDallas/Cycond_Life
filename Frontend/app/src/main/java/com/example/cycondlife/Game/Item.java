package com.example.cycondlife.Game;

import java.util.ArrayList;

public abstract class Item {
    protected int itemID;
    protected String name;
    protected String description;
    protected int type;
    protected static ArrayList<Item> itemList= new ArrayList<>();
    public int getType(){
        return type;
    }
    public int getItemID()
    {
        return itemID;
    }
    /*
    finds an item on the item list by its id
     */
    public static Item findByID(int ID)
    {
        for(int i=0;i<itemList.size();i++)
        {
            if(itemList.get(i).itemID==ID);
            {
                return itemList.get(i);
            }
        }
        return itemList.get(0);
    }
}