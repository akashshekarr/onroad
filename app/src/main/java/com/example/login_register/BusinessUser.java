package com.example.login_register;
public class BusinessUser {
    private String companyName;
    private String companyLocation;
    private String ownerName;
    private String ownerContactNumber;

    public BusinessUser(String companyName, String companyLocation, String ownerName, String ownerContactNumber) {
        this.companyName = companyName;
        this.companyLocation = companyLocation;
        this.ownerName = ownerName;
        this.ownerContactNumber = ownerContactNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return companyLocation;
    }

    public void setAddress(String address) {
        this.companyLocation = companyLocation;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerContactNumber() {
        return ownerContactNumber;
    }

    public void setOwnerContactNumber(String ownerContactNumber) {
        this.ownerContactNumber = ownerContactNumber;
    }
}
