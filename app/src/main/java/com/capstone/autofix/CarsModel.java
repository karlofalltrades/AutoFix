package com.capstone.autofix;

public class CarsModel {
    private String car_id;
    private String carBrand;
    private String carModel;
    private String carYear;
    private String carPlate_number;
    private String CustomerID;

    public CarsModel(String car_id, String carBrand, String carModel ,String carYear ,String carPlate_number ,String CustomerID){
        this.car_id = car_id;
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.carYear = carYear;
        this.carPlate_number = carPlate_number;
        this.CustomerID = CustomerID;
    }

    public String getCar_id(){
        return car_id;
    }
    public String getCarBrand(){
        return carBrand;
    }
    public String getCarModel(){
        return carModel;
    }
    public String getCarYear(){
        return carYear;
    }
    public String getCarPlate_number() {
        return carPlate_number;
    }
    public String getCustomerID() {
        return CustomerID;
    }
}
