package com.example.ecommerce.spotsale2.DatabaseClasses;

import java.io.Serializable;
import java.util.List;

public class Cart implements Serializable {


    private int cart_id ;
    private int total_items;
    private List<Product> productList;
    private double total_sum;
    private String UID;
    enum status { active, delivered, pending };



    public Cart(){};

    public Cart(int cart_id, int total_items, List<Product> productList, double total_sum, String UID) {
        this.cart_id = cart_id;
        this.total_items = total_items;
        this.productList = productList;
        this.total_sum = total_sum;
        this.UID = UID;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public int getCart_id() {
        return cart_id;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }

    public int getTotal_items() {
        return total_items;
    }

    public void setTotal_items(int total_items) {
        this.total_items = total_items;
    }

    public double getTotal_sum() {
        return total_sum;
    }

    public void setTotal_sum(double total_sum) {
        this.total_sum = total_sum;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
