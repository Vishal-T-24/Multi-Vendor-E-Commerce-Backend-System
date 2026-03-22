package org.example.Services;

import org.example.Dao.PublicDao;
import org.example.Domains.Buyers;
import org.example.Domains.Products;
import org.example.Domains.Seller;
import org.example.Domains.Users;
import org.example.Records.*;
import org.example.Util.ApiResponse;
import org.example.Util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PublicService {

    private PublicDao publicDao=new PublicDao();
    PasswordUtil passwordUtil=new PasswordUtil();
    String role;


    public UUID loginUser(AllRecords.UserRecord userRecord) throws SQLException {

      String hashedPassword=publicDao.loginUser(userRecord);

      if(hashedPassword==null){
          return null;
      }

     if(passwordUtil.verifyPassword(userRecord.password(),hashedPassword)) {


         return publicDao.getRoleIdfromUsers(userRecord.email());
     }
     return null;

    }
    public  List<Products> getAllProducts() throws SQLException {
        return publicDao.getAllProducts();
    }

    public Products getProductById(UUID id) throws SQLException {
        if(publicDao.getProductById(id)!=null){
            return publicDao.getProductById(id);
        }
        return null;
    }

    public boolean SellerRequest(Seller seller) throws SQLException {

        seller.setPassword( passwordUtil.HashPassword(seller.getPassword()));

        UUID role_id=publicDao.getRoleId("SELLER");


        UUID sellerid=publicDao.SellerRequest(new Users(seller.getName(),seller.getEmail(),seller.getPassword(),role_id,"Pending"));

        int confirmation=publicDao.AddSellerInsellerTable(sellerid,seller.getBusinessName(),seller.getPhoneNo(),seller.getAddress(),seller.getCompanyId());

        if(confirmation!=0){
            return true;
        }
        return false;

    }

    public boolean BuyerRegister(Buyers buyer) throws SQLException {
        buyer.setPassword(passwordUtil.HashPassword(buyer.getPassword()));
        UUID role_id=publicDao.getRoleId("BUYER");

        UUID Buyer_id=publicDao.BuyerRegister(buyer,role_id);
       if(Buyer_id==null){
           return false;
       }
       publicDao.BuyerRegisterinBuyers(buyer, Buyer_id);
       return true;
    }


    public ApiResponse PlaceOrder(AllRecords.PlaceOrder order, UUID userId) throws SQLException {
        int available_quantity=publicDao.checkQuantity(order.productId());
      if(available_quantity==-1){
          return new ApiResponse("Product Not Availble",false,404);

      } else if (available_quantity==0) {
          return new ApiResponse("Product Out of Stock",false,409);
      }
      else if (order.quantity()>available_quantity){
          return new ApiResponse("Insuffecient Stock.Only "+available_quantity+" Units Available.",false,409);
      }
      else {
          return new ApiResponse("Order Summary",true,201,publicDao.palceOrder(order,userId));
      }
    }

    public UUID extractuserId(String email) throws SQLException {
       return publicDao.extractuserId(email);
    }

    public AllRecords.RefreshToken RefreshToken(String refreshToken) throws SQLException {
       return publicDao.refreshToken(refreshToken);
    }

    public void deactivateOldToken(String refreshToken) throws SQLException {
        publicDao.deactivateRefreshToken(refreshToken);
    }

    public void saveTokenInDb(String refreshtoken, UUID userId) throws SQLException {
        publicDao.StoreToken(refreshtoken,userId);
    }

    public UUID getRoleId(String role) throws SQLException {
        return publicDao.getRoleId(role);
    }

    public String extrctRole(UUID roleId) throws SQLException {
       return publicDao.extractRole(roleId);
    }

    public AllRecords.Company getCompany() throws SQLException {
       return publicDao.getCompany();
    }

    public List<Products> getProductByBusiness(String businessName) throws SQLException {
       return publicDao.getProductByBusiness(businessName);
    }

    public List<Products> getProductsByCompany(UUID companyId) throws SQLException {
        return publicDao.getProductsByCompany(companyId);
    }
}
