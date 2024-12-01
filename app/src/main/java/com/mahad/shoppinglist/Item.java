package com.mahad.shoppinglist;

public class Item {
    private String name;
    private int quantity;
    private double price;
    private int id;

    // Empty constructor required for Firebase
    public Item() {}

    public Item(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }


    public void setId(int id) {
        this.id = id;
    }




}
