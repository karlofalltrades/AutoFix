package com.capstone.autofix;

public class Employee {
    String emp_id;
    String name;
    String phone;
    String image;

    public Employee(String emp_id, String name, String phone, String image){
        this.emp_id = emp_id;
        this.name = name;
        this.phone = phone;
        this.image = image;
    }

    public String getEmpID() {
        return emp_id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getImage() {
        return image;
    }
}
