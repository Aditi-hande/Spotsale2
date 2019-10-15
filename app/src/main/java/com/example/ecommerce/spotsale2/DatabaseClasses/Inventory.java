package com.example.ecommerce.spotsale2.DatabaseClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Inventory implements Serializable {

    private String inventory_id ;
    private int total_items;
    private List<ProductRef> productList;

    public static class ProductRef implements Serializable {
        String item_id;
        int age;
        int cost;

        public ProductRef() {}

        public ProductRef(String item_id, int age, int cost) {
            this.item_id = item_id;
            this.age = age;
            this.cost = cost;
        }

        public String getItem_id() {
            return item_id;
        }

        public void setItem_id(String item_id) {
            this.item_id = item_id;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }
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
