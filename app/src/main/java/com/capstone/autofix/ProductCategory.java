package com.capstone.autofix;

public class ProductCategory {
    String name;
    String catid;

    public ProductCategory(String name, String catid){
        this.name = name;
        this.catid = catid;
    }

    public String getName() {
        return name;
    }

    public String getCatid() {
        return catid;
    }

}
