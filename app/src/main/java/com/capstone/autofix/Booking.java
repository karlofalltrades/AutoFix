package com.capstone.autofix;

public class Booking {
    private String bookingID;
    private String bookingAddress;
    private String bookingDate;
    private String bookingTime;
    private String bookingStatus;
    private String CustomerID;
    private String shopId;
    private String emp_name;

    public Booking(String bookingID, String bookingAddress, String bookingDate ,String bookingTime ,String bookingStatus ,String CustomerID, String shopId, String emp_name){
        this.bookingID = bookingID;
        this.bookingAddress = bookingAddress;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.bookingStatus = bookingStatus;
        this.CustomerID = CustomerID;
        this.shopId = shopId;
        this.emp_name = emp_name;
    }

    public String getBookingID(){
        return bookingID;
    }
    public String getBookingAddress(){
        return bookingAddress;
    }
    public String getBookingDate(){
        return bookingDate;
    }
    public String getBookingTime(){
        return bookingTime;
    }
    public String getBookingStatus() { return bookingStatus; }
    public String getCustomerID() { return CustomerID; }
    public String getShopId() { return shopId; }
    public String getEmpName() { return emp_name; }
}
