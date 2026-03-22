package org.example.Records;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class AllRecords {

    public record OrderDetails(UUID Id,
                               UUID Productid,
                               BigDecimal PricePerProduct,
                               int Quantity, BigDecimal TotalAmount,
                               Timestamp OrderAt, String OrderStatus,
                               UUID UserId,
                               UUID SellerId,
                               UUID companyid) {
    }
    public record PlaceOrder(UUID productId,
                             int quantity) {
    }
    public record RefreshToken(UUID id,
                               UUID userId,
                               String token,
                               LocalDateTime expiry,
                               boolean isActive) {
    }
    public record SellerRecord(UUID id,
                               String name,
                               String email,
                               String status,
                               UUID role_id,
                               String businessname,
                               long phone,
                               String address,
                               UUID companyid) {
    }
    public record UserRecord( String email,
                              String password) {


    }
    public record Products(String name,
                           BigDecimal price,
                           int quantity,
                           String businessname){

    }
    public record StockUpdate(UUID ProductId,
                              int quantity) {
    }
    public record Companyname(String name){

    }
    public record Company(UUID id,
                          String name,
                          Timestamp createdAt){

    }
    public record Buyer(String name, String email,String password,UUID company_id){

    }

}
