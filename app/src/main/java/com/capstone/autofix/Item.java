package com.capstone.autofix;

public class Item {
    String prodId;
    String prodImage;
    String prodName;
    String prodBrand;
    String prodTypeId;
    String prodPrice;
    String prodQuantity;
    String prodStatus;
    String shopId;

    public Item(String prodId, String prodImage, String prodName, String prodBrand,String prodTypeId,String prodPrice,String prodQuantity
            ,String prodStatus,String shopId ){
        this.prodId = prodId;
        this.prodImage = prodImage;
        this.prodName = prodName;
        this.prodBrand = prodBrand;
        this.prodTypeId = prodTypeId;
        this.prodPrice = prodPrice;
        this.prodQuantity = prodQuantity;
        this.prodStatus = prodStatus;
        this.shopId = shopId;

    }

    public String getProdId() {
        return prodId;
    }

    public String getProdImage() {
        return prodImage;
    }

    public String getProdName() {
        return prodName;
    }
    public String getProdBrand() {
        return prodBrand;
    }

    public String getProdTypeId() {
        return prodTypeId;
    }

    public String getProdPrice() {
        return prodPrice;
    }
    public String getProdQuantity() {
        return prodQuantity;
    }


    public String getProdStatus() {
        return prodStatus;
    }
    public String getShopId() {
        return shopId;
    }
}
