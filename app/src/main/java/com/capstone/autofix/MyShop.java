package com.capstone.autofix;

public class MyShop {
    private int sid;
    private String shopname;
    private String shopAddress;
    private String image;
    private String shopContact;
    private String shopEmail;
    private double distance;

    public MyShop(int sid, String shopname, String shopAddress, double distance,String image ,String shopContact ,String shopEmail){
        this.sid = sid;
        this.shopname = shopname;
        this.shopAddress = shopAddress;
        this.image = image;
        this.shopContact = shopContact;
        this.shopEmail = shopEmail;
        this.distance = distance;
    }

    public double getDistance(){
        return distance;
    }

//    public int compareTo(Object obj){
//        if(obj instanceof MyShop){
//            MyShop shop=(MyShop) obj;
//            return (int) (this.distance-shop.getDistance());
//        }
//        return 0;
//    }

    public int getSid(){
        return sid;
    }
    public String getShopname(){
        return shopname;
    }
    public String getShopAddress(){
        return shopAddress;
    }
    public String getImage(){
        return image;
    }
    public String getShopContact() { return shopContact; }
    public String getShopEmail() { return shopEmail; }
}
