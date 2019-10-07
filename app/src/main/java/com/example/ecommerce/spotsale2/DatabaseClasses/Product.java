package com.example.ecommerce.spotsale2.DatabaseClasses;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private String cat_id ;
    private int item_id ;
    private int qty ;
    private int cost;
    private String name;
    private String description;
    private String brand;
    private List<String> sellers;
    private String imageUrl;

    public Product(String cat_id, int item_id, int qty, int cost, String name, String description, String brand, List<String> sellers, String imageUrl) {
        this.cat_id = cat_id;
        this.item_id = item_id;
        this.qty = qty;
        this.cost = cost;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.sellers = sellers;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<String> getSellers() {
        return sellers;
    }

    public void setSellers(List<String> sellers) {
        this.sellers = sellers;
    }

    public Product(){}

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
