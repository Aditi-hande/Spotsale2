package com.example.ecommerce.spotsale2.DatabaseClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Inventory implements Serializable {

    private String inventory_id ;
    private int total_items;
    private List<ProductRef> productList;

    public static class ProductRef {
        String item_id;
        int age;
        int cost;
    }

    public Inventory(String inventory_id, int total_items, List<ProductRef> productList) {
        this.inventory_id = inventory_id;
        this.total_items = total_items;
        this.productList = productList;
    }

    public Inventory(){
        productList = new ArrayList<>();
    }

    public String getInventory_id() {
        return inventory_id;
    }

    public void setInventory_id(String inventory_id) {
        this.inventory_id = inventory_id;
    }

    public int getTotal_items() {
        return total_items;
    }

    public void setTotal_items(int total_items) {
        this.total_items = total_items;
    }

    public List<ProductRef> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductRef> productList) {
        this.productList = productList;
    }
}
