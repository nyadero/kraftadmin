package com.bowerzlabs.models;

import jakarta.persistence.Embeddable;

@Embeddable
public class Contact {
    private String phone2;
    private String phone1;
    private String address;

    public Contact(String phone2, String phone1, String address) {
        this.phone2 = phone2;
        this.phone1 = phone1;
        this.address = address;
    }

    public Contact() {
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
