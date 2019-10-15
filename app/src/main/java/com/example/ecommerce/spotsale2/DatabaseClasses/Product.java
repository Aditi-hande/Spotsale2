package com.example.ecommerce.spotsale2.DatabaseClasses;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private String cat_id ;
    private String item_id ;
    private int qty ;
    private int cost;
    private String name;
    private String description;
    private String brand;
    private List<SellerDesc> sellers;
    private String imageUrl;

    public static class SellerDesc {
        String user_id;
        int cost;
        int age;



        public SellerDesc(String user_id, int cost, int age) {
            this.user_id = user_id;
            this.cost = cost;
            this.age = age;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public Product(String cat_id, String item_id, int qty, int cost, String name, String description, String brand, List<SellerDesc> sellers, String imageUrl) {
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

    public List<SellerDesc> getSellers() {
        return sellers;
    }

    public void setSellers(List<SellerDesc> sellers) {
        this.sellers = sellers;
    }

    public Product(){}

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
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

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
