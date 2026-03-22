package org.example.Domains;


import java.math.BigDecimal;
import java.util.UUID;

public class Products {

    private UUID Id;
    private String productName;
    private BigDecimal price;
    private int quantity;



    private UUID sellerId;
    private String businessname;
    private UUID companyId;

    public Products(UUID id, String productName, BigDecimal price, int quantity, UUID sellerId, String businessname,UUID companyId) {
        Id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.sellerId = sellerId;
        this.businessname = businessname;
        this.companyId=companyId;
    }

    public String getBusinessname() {
        return businessname;
    }

    public void setBusinessname(String businessname) {
        this.businessname = businessname;
    }




    public UUID getSellerId() {
        return sellerId;
    }

    public void setSellerId(UUID sellerId) {
        this.sellerId = sellerId;
    }

    public Products() {
    }

    public Products(UUID id,String ProductName,BigDecimal price){
        this.Id=id;
        this.productName=ProductName;
        this.price=price;
    }

    public Products(UUID id,String ProductName,BigDecimal price,int quantity){
        this.Id=id;
        this.productName=ProductName;
        this.price=price;
        this.quantity=quantity;
    }

    public Products(UUID Id, String productName, BigDecimal price, int quantity,UUID sellerId) {
        this.Id = Id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.sellerId=sellerId;

    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID Id) {
        this.Id = Id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }


}
