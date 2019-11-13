package com.capstone.autofix;

public class CartListModel {
    private String prodId;
    private String prodImage;
    private String prodName;
    private String prodBrand;
    private String prodPrice;
    private String prodQuantity;
    private String cartID;
    private String cartQuantity;
    private String cartPrice;
    private String CustomerID;
    private String shopId;
    public boolean isSelected;

    public CartListModel(){}

    public CartListModel(String prodId, String prodImage, String prodName ,String prodBrand, String prodPrice, String prodQuantity,String cartID ,String cartQuantity, String cartPrice, String CustomerID, String shopId){
        this.prodId = prodId;
        this.prodImage = prodImage;
        this.prodName = prodName;
        this.prodBrand = prodBrand;
        this.prodPrice = prodPrice;
        this.prodQuantity = prodQuantity;
        this.cartID = cartID;
        this.cartQuantity = cartQuantity;
        this.cartPrice = cartPrice;
        this.CustomerID = CustomerID;
        this.shopId = shopId;
    }

    public String getProdId(){
        return prodId;
    }
    public String getProdImage(){
        return prodImage;
    }
    public String getProdName(){
        return prodName;
    }
    public String getProdBrand(){
        return prodBrand;
    }
    public String getProdPrice(){
        return prodPrice;
    }
    public String getProdQuantity(){
        return prodQuantity;
    }
    public String getCartID(){
        return cartID;
    }
    public String getCartQuantity(){
        return cartQuantity;
    }
    public String getCartPrice(){
        return cartPrice;
    }
    public String getCustomerID(){
        return CustomerID;
    }
    public String getShopId(){
        return shopId;
    }

    public boolean getSelected(){
        return isSelected;
    }
    public void setSelected(boolean selected){
        isSelected = selected;
    }
}
