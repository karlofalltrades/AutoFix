package com.capstone.autofix;

public class OrderItems {
    private String OrderID;
    private String OrderQuantity;
    private String OrderAmount;
    private String OrderStatus;
    private String OrderDate;
    private String PaymentStatus;
    private String shopId;
    private String prodId;
    private String CustomerID;
    private String prodImage;

    public OrderItems(String OrderID, String OrderQuantity, String OrderAmount ,String OrderStatus ,String OrderDate ,String PaymentStatus
            , String shopId,String prodId,String CustomerID, String prodImage){
        this.OrderID = OrderID;
        this.OrderQuantity = OrderQuantity;
        this.OrderAmount = OrderAmount;
        this.OrderStatus = OrderStatus;
        this.OrderDate = OrderDate;
        this.PaymentStatus = PaymentStatus;
        this.shopId = shopId;
        this.prodId = prodId;
        this.CustomerID = CustomerID;
        this.prodImage = prodImage;

    }

    public String getOrderID(){
        return OrderID;
    }
    public String getOrderQuantity(){
        return OrderQuantity;
    }
    public String getOrderAmount(){
        return OrderAmount;
    }
    public String getOrderStatus(){
        return OrderStatus;
    }
    public String getOrderDate() { return OrderDate; }
    public String getPaymentStatus() { return PaymentStatus; }
    public String getShopId() { return shopId; }
    public String getProdId() { return prodId; }
    public String getCustomerID() { return CustomerID; }
    public String getProdImage() { return prodImage; }
}
