package org.example.Services;

import org.example.Dao.PublicDao;
import org.example.Dao.SellerDao;
import org.example.Domains.Products;
import org.example.Records.AllRecords;
import org.example.Util.ApiResponse;
import org.example.Util.JwtUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class SellerService {
    SellerDao sellerDao=new SellerDao();
    JwtUtil jwtUtil=new JwtUtil();
    PublicDao publicDao=new PublicDao();



    public boolean addProduct(Products products, UUID sellerId) throws SQLException {


       if(sellerDao.AddProduct(products,sellerId)!=0){
           return true;
       }
       return false;

    }

    public boolean deleteProduct(UUID id,String email) throws SQLException {
        int confirmation;
        UUID sellerId_Client=sellerDao.getSellerId(email);

        UUID sellerId_Id=sellerDao.getSellerId_ByProductId(id);
        if(sellerId_Id == null){
            return false;
        }
        if(sellerId_Client.equals(sellerId_Id)){
            if(sellerDao.deleteProduct(id)!=0){
                return true;
            }
        }
        return false;


    }



    public boolean SellerStatusVerification(String email) throws SQLException {
       if(sellerDao.verfication(email)){
           return true;
       }
       return false;
    }

    public UUID extractSellerId(String email) throws SQLException {

        UUID sellerId= sellerDao.getSellerId(email);

        return sellerId;
    }

    public List<AllRecords.OrderDetails> getOrderdetails(UUID sellerid) throws SQLException {
        return sellerDao.getOrderdetails(sellerid);
    }

    public List<Products> getOwnProducts(UUID id) throws SQLException {
       return sellerDao.getOwnProducts(id);
    }

    public UUID getsellerId(UUID productId) throws SQLException {
        return sellerDao.getSellerId_ByProductId(productId);

    }

    public ApiResponse StockUpdate(AllRecords.StockUpdate stockUpdate) throws SQLException {
        return sellerDao.StockUpdate(stockUpdate);
    }

    public String extractBusinessname(UUID sellerId) throws SQLException {
        return sellerDao.extractBusinessname(sellerId);
    }
}
