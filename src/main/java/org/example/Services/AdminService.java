package org.example.Services;

import org.example.Dao.AdminDao;
import org.example.Domains.Users;
import org.example.Records.AllRecords;
import org.example.Util.JwtUtil;
import org.example.Util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AdminService {

    AdminDao adminDao=new AdminDao();
    JwtUtil jwtUtil=new JwtUtil();
    PasswordUtil passwordUtil=new PasswordUtil();


    //GetAllUser
    public List<Users> getAllUser() throws SQLException {

        return adminDao.getAllUsers();
    }


    //Approve Sellers
    public boolean acceptSellers(UUID sellerId) throws SQLException {

       if( adminDao.acceptSellers(sellerId)!=0){
           return true;
       }
       return false;

    }
    //Delete SellerById
    public boolean deleteSellerById(UUID id) throws SQLException {

         if(adminDao.deleteSellerById(id)!=0){
             return true;
         }
         return false;

    }

    //GetAllSeller
    public List<AllRecords.SellerRecord> getAllSeller() throws SQLException {
       return adminDao.getAllSeller();
    }

    //GetAllOrders
    public List<AllRecords.OrderDetails> getAllOrders() throws SQLException {
       if(adminDao.getAllOrders()!=null){
           return adminDao.getAllOrders();
       }
       return null;
    }

    public boolean addAdmin(Users user) throws SQLException {
        user.setPassword(passwordUtil.HashPassword(user.getPassword()));
        if(adminDao.AddAdmin(user)!=0){
            return true;
        }
        return false;
    }

    public void addcompany(AllRecords.Companyname companyname) throws SQLException {
        adminDao.addCompany(companyname);
    }
}
