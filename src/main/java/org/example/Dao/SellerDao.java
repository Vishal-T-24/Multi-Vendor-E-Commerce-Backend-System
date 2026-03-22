package org.example.Dao;

import org.example.Domains.Products;
import org.example.Records.AllRecords;
import org.example.Util.ApiResponse;
import org.example.Util.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SellerDao {

    PublicDao publicDao=new PublicDao();

    //AddProducts
    public  int AddProduct(Products products,UUID sellerId) throws SQLException {

        Connection con=DbConnection.getConnection();

        String sql="Insert into products(productname,price,quantity,sellerid,businessname,company_id)values(?,?,?,?,?,?)";
        PreparedStatement statement= con.prepareStatement(sql);

        statement.setString(1,products.getProductName());
        statement.setBigDecimal(2,products.getPrice());
        statement.setInt(3,products.getQuantity());
        statement.setObject(4,sellerId);
        statement.setString(5,products.getBusinessname());
        statement.setObject(6,products.getCompanyId());

         int confirmation= statement.executeUpdate();


        statement.close();
        con.close();
        return confirmation;
    }

    //GetSellerId_By_ProductId
    public UUID getSellerId_ByProductId(UUID id) throws SQLException {
        Connection connection=DbConnection.getConnection();

        UUID sellerid = null;
        String sql="select * from products where id=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setObject(1,id);

        ResultSet rs=preparedStatement.executeQuery();
        while(rs.next()){
            sellerid= rs.getObject("sellerid", UUID.class);

        }
        rs.close();
        preparedStatement.close();
        connection.close();

        return sellerid;

    }



    //Delete a Product
    public int deleteProduct(UUID id)throws SQLException{
        Connection con=DbConnection.getConnection();

        String sql="delete from products where id=?";
        PreparedStatement ps=con.prepareStatement(sql);
        ps.setObject(1,id);

       int confirmation= ps.executeUpdate();

        ps.close();
       con.close();
       return confirmation;

    }
    //Active or Pending Verification
    public boolean verfication(String email) throws SQLException {
          Connection con=DbConnection.getConnection();
          String sql="select status from users where email=?";
          PreparedStatement preparedStatement= con.prepareStatement(sql);
          preparedStatement.setString(1,email);

          ResultSet rs=preparedStatement.executeQuery();
          while(rs.next()){
              if(rs.getString("status").equalsIgnoreCase("Active")){
                  return true;
              }
          }
          rs.close();
          preparedStatement.close();
          rs.close();

          return false;
    }

    //Seller_Id_UsingEmail
    public UUID getSellerId(String email) throws SQLException {
        Connection con=DbConnection.getConnection();
        UUID id = null;
        String sql="select * from users where email=?";
        PreparedStatement ps= con.prepareStatement(sql);
        ps.setString(1,email);

        ResultSet rs=ps.executeQuery();

        while(rs.next()){
            id=rs.getObject("id", UUID.class);
        }

        rs.close();
        con.close();
        ps.close();
        return id;

    }


    public List<AllRecords.OrderDetails> getOrderdetails(UUID sellerid) throws SQLException {
        List<AllRecords.OrderDetails> orderDetailsList = new ArrayList<>();
        Connection connection = DbConnection.getConnection();

        String sql = "SELECT * FROM orders WHERE sellerid = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setObject(1, sellerid);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            AllRecords.OrderDetails orderDetails = new AllRecords.OrderDetails(
                    rs.getObject("id", UUID.class),
                    rs.getObject("productid", UUID.class),
                    rs.getBigDecimal("priceperproduct"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("totalamount"),
                    rs.getTimestamp("orderat"),
                    rs.getString("orderstatus"),
                    rs.getObject("userid", UUID.class),
                    rs.getObject("sellerid", UUID.class),
                    rs.getObject("companyid", UUID.class));
            orderDetailsList.add(orderDetails);
        }
        ps.close();
        connection.close();
        return orderDetailsList;
    }
    public List<Products> getOwnProducts(UUID id) throws SQLException {
        List<Products>products=new ArrayList<>();
        Connection connection=DbConnection.getConnection();
        String sql="select * from products where sellerid=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setObject(1,id);

        ResultSet rs=preparedStatement.executeQuery();

        while(rs.next()){
            Products product=new Products(rs.getObject("id", UUID.class),
                    rs.getString("productname"),
                    rs.getBigDecimal("price"),
                    rs.getInt("quantity"));

            products.add(product);
        }

        rs.close();
        preparedStatement.close();
        connection.close();
        return products;


    }

    //StockUpdate
    public ApiResponse StockUpdate(AllRecords.StockUpdate stockUpdate) throws SQLException {
        Connection connection=DbConnection.getConnection();
        String sql="Update products set quantity=? where id=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        int quantity=publicDao.checkQuantity(stockUpdate.ProductId());
        if(quantity!=-1){
            preparedStatement.setInt(1,quantity+stockUpdate.quantity());
            preparedStatement.setObject(2,stockUpdate.ProductId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            return new ApiResponse("Product Stock Updated Successfully",true,200);
        }
        preparedStatement.close();
        connection.close();
        return new ApiResponse("Product Not Available",false,404);

    }

    public String extractBusinessname(UUID sellerId) throws SQLException {
        Connection connection=DbConnection.getConnection();
        String sql="select businessname from sellers where sellerid=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setObject(1,sellerId);
        ResultSet resultSet= preparedStatement.executeQuery();
        if(resultSet.next()){
            String bName=resultSet.getString("businessname");
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return bName;
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return null;
    }
}
