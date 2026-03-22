package org.example.Domains;

import java.util.UUID;

public class Buyers {

    private UUID id;
    private String name;
    private String email;
    private String password;
    private UUID company_id;
    private long phoneNo;

    public Buyers(UUID id, String name, String email, String password, UUID company_id, long phoneNo) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.company_id = company_id;
        this.phoneNo = phoneNo;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UUID getCompany_id() {
        return company_id;
    }

    public void setCompany_id(UUID company_id) {
        this.company_id = company_id;
    }

    public long getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(long phoneNo) {
        this.phoneNo = phoneNo;
    }




}
