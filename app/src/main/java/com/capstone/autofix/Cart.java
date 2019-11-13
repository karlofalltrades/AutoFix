package com.capstone.autofix;

public class Cart {
    private String shopId;
    private String shopImage;
    private String shopName;
    private String shopAddress;
    private String shopContact;
    private String shopEmail;

    public Cart(String shopId, String shopImage, String shopName ,String shopAddress ,String shopContact ,String shopEmail){
        this.shopId = shopId;
        this.shopImage = shopImage;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.shopContact = shopContact;
        this.shopEmail = shopEmail;
    }

    public String getShopId(){
        return shopId;
    }
    public String getShopImage(){
        return shopImage;
    }
    public String getShopName(){
        return shopName;
    }
    public String getShopAddress(){
        return shopAddress;
    }
    public String getShopContact() {
        return shopContact;
    }
    public String getShopEmail() {
        return shopEmail;
    }
}
