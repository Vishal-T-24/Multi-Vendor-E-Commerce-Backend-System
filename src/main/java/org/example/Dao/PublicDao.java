package org.example.Dao;

import org.example.Domains.Buyers;
import org.example.Domains.Products;
import org.example.Domains.Users;
import org.example.Records.*;
import org.example.Util.DbConnection;
import org.example.Util.JwtUtil;
import org.example.Util.PasswordUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PublicDao {


   PasswordUtil passwordUtil=new PasswordUtil();

    /*Login User*/
    public String loginUser(AllRecords.UserRecord userRecord) throws SQLException {

        Connection con=DbConnection.getConnection();
        String sql="select * from users where email=? ";
        PreparedStatement ps=con.prepareStatement(sql);
        ps.setString(1,userRecord.email());

        ResultSet rs=ps.executeQuery();

        String role;
        String HashedPassword=null;
        while(rs.next()){
            HashedPassword=rs.getString("password");

        }
        rs.close();
        ps.close();
        con.close();
        return HashedPassword;

    }
    //SellerRequest
    public UUID SellerRequest(Users user)throws SQLException{

        Connection con=DbConnection.getConnection();

        String sql="Insert into users(name,email,password,role_id) values (?,?,?,?)";
        PreparedStatement statement = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);



        statement.setString(1, user.getName());
        statement.setString(2, user.getEmail());
        statement.setString(3, user.getPassword());
        statement.setObject(4,user.getRole_id());

        statement.executeUpdate();
        ResultSet rs=statement.getGeneratedKeys();
        if(rs.next()){
            UUID userid=rs.getObject(1, UUID.class);
            rs.close();
            statement.close();
            con.close();
            return userid;
        }
        rs.close();
        statement.close();
        con.close();
        throw new RuntimeException("Id Generation Failed");
    }

    /*SellersInSellerTable*/
    public int AddSellerInsellerTable(UUID sellerid, String buisnessName, long phoneNo, String address,UUID companyId) throws SQLException {
        Connection con=DbConnection.getConnection();
        String sql="Insert into sellers(sellerid,businessName,phone,address,companyid)values(?,?,?,?,?)";
        PreparedStatement preparedStatement=con.prepareStatement(sql);
        preparedStatement.setObject(1,sellerid);
        preparedStatement.setString(2,buisnessName);
        preparedStatement.setLong(3,phoneNo);
        preparedStatement.setString(4,address);
        preparedStatement.setObject(5,companyId);


        int confirmation= preparedStatement.executeUpdate();

        preparedStatement.close();
        con.close();
        return confirmation;
    }


    //GetAllProducts
    public List<Products>  getAllProducts() throws SQLException{
        List<Products> products=new ArrayList<>();

        Connection con=DbConnection.getConnection();

        String Sql="select * from products";
        Statement s=con.createStatement();
        ResultSet rs= s.executeQuery(Sql);

        while(rs.next()){
            Products pr=new Products(rs.getObject("id", UUID.class),
                    rs.getString("productname"),
                    rs.getBigDecimal("price"),
                    rs.getInt("quantity"),
                    rs.getObject("sellerId", UUID.class),
                    rs.getString("businessname"),
                    rs.getObject("company_id", UUID.class));

            products.add(pr);
        }

        rs.close();
        s.close();
        con.close();
        return products;
    }

    //Get Product By Id
    public Products getProductById(UUID id) throws SQLException {
        Connection con=DbConnection.getConnection();


        String sql="select * from products where id=?";
        PreparedStatement preparedStatement= con.prepareStatement(sql);

        ResultSet rs= preparedStatement.executeQuery();
        if (rs.next()) {
            Products product = new Products(rs.getObject("id", UUID.class),
                    rs.getString("productname"),
                    rs.getBigDecimal("price"),
                    rs.getInt("quantity"),
                    rs.getObject("sellerId", UUID.class));
            return product;
        }
       rs.close();
        preparedStatement.close();
        con.close();
        return null;

    }

    //PlaceOrder
    public AllRecords.OrderDetails palceOrder(AllRecords.PlaceOrder order,UUID userId) throws SQLException {
        Connection con=DbConnection.getConnection();
        con.setAutoCommit(false);


        try {
            String sql="select * from products where id=?";
            PreparedStatement preparedStatement= con.prepareStatement(sql);
            preparedStatement.setObject(1,order.productId());

            ResultSet resultSet=preparedStatement.executeQuery();
            resultSet.next();
            BigDecimal productPerPrice= resultSet.getBigDecimal("price");
            UUID sellerId=resultSet.getObject("sellerid", UUID.class);
            UUID companyId=resultSet.getObject("company_id", UUID.class);

            int available_quantity=checkQuantity(order.productId());
            BigDecimal totalPrice=productPerPrice.multiply(BigDecimal.valueOf(order.quantity()));

            //Insert into Orders
            UUID orderId=UUID.randomUUID();
            String orders = "Insert into orders(id,productid,priceperproduct,quantity,totalamount,orderstatus,userid,sellerid,companyid)values(?,?,?,?,?,?,?,?,?) Returning *";
            PreparedStatement ps = con.prepareStatement(orders);
            ps.setObject(1,orderId);
            ps.setObject(2, order.productId());
            ps.setBigDecimal(3, productPerPrice);
            ps.setInt(4, order.quantity());
            ps.setBigDecimal(5, totalPrice);
            ps.setString(6, "PLACED");
            ps.setObject(7, userId);
            ps.setObject(8, sellerId);
            ps.setObject(9,companyId);

            ResultSet response= ps.executeQuery();

            AllRecords.OrderDetails orderDetails=null;
            if(response.next()){
                orderDetails=new AllRecords.OrderDetails(response.getObject("id", UUID.class),
                        response.getObject("productid", UUID.class),
                        response.getBigDecimal("priceperproduct"),
                        response.getInt("quantity"),
                        response.getBigDecimal("totalamount"),
                        response.getTimestamp("orderat"),
                        response.getString("orderstatus"),
                        response.getObject("userid", UUID.class),
                        response.getObject("sellerid", UUID.class),
                        response.getObject("companyid", UUID.class));
            }
            ps.close();

            int updated_quantity=available_quantity-order.quantity();

            //update the quqntity in products table
            String update = "Update products set quantity=? where id=?";
            PreparedStatement ps1 = con.prepareStatement(update);
            ps1.setInt(1, updated_quantity);
            ps1.setObject(2, order.productId());
            ps1.executeUpdate();
            ps1.close();

            con.commit();
            return orderDetails;

        } catch (Exception e) {
            con.rollback();
            throw new SQLException("Order Failed "+e.getMessage());
        }
        finally {
            con.setAutoCommit(true);
            con.close();
        }

    }

    //Extract User Id
    public UUID extractuserId(String email) throws SQLException {

        Connection connection=DbConnection.getConnection();

        String sql="select * from users where email=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setString(1,email);

        ResultSet rs=preparedStatement.executeQuery();

       rs.next();
       UUID id=rs.getObject("id", UUID.class);
       rs.close();
       preparedStatement.close();
       connection.close();
       return id;
    }

    //CheckQuantity
    public int checkQuantity(UUID id) throws SQLException {
        Connection connection=DbConnection.getConnection();

        String sql="select * from products where id=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setObject(1,id);

        ResultSet resultSet=preparedStatement.executeQuery();

        if(resultSet.next()){
            int quantity=resultSet.getInt("quantity");
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return quantity;
        }
        return -1;
    }

    //Extract RoleId
    public UUID getRoleIdfromUsers(String email) throws SQLException {
        Connection connection=DbConnection.getConnection();
        String sql="select * from users where email=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setString(1,email);
        ResultSet resultSet=preparedStatement.executeQuery();

        if(resultSet.next()){
            UUID id=resultSet.getObject("role_id", UUID.class);
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return id;
        }
        return null;
    }
    //Store RefreshToken in DB
    public void StoreToken(String token,UUID userId) throws SQLException {
        Connection connection=DbConnection.getConnection();
        String sql="Insert into refresh_tokens(user_id,token,expiry,is_active)values(?,?,?,?)";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setObject(1,userId);
        preparedStatement.setString(2,token);
        preparedStatement.setTimestamp(3,Timestamp.valueOf(LocalDateTime.now().plusDays(7)) );
        preparedStatement.setBoolean(4,true);

        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }
    //Get_RefreshToken_Record
    public AllRecords.RefreshToken refreshToken(String refreshToken) throws SQLException {
        Connection connection=DbConnection.getConnection();
        String sql="select * from refresh_tokens where token=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setString(1,refreshToken);

        ResultSet resultSet=preparedStatement.executeQuery();
        if(resultSet.next()){
            AllRecords.RefreshToken refreshToken1=new AllRecords.RefreshToken(resultSet.getObject("id",UUID.class),
                    resultSet.getObject("user_id", UUID.class),
                    resultSet.getString("token"),
                    resultSet.getTimestamp("expiry").toLocalDateTime(),
                    resultSet.getBoolean("is_active"));
            return refreshToken1;
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return null;
    }

    //Deactivate Old Token
    public void deactivateRefreshToken(String token) throws SQLException {
        Connection connection = DbConnection.getConnection();
        String sql = "UPDATE refresh_tokens SET is_active = false WHERE token = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, token);
        ps.executeUpdate();
        ps.close();
        connection.close();
    }

    //Role_Id
    public UUID getRoleId(String role) throws SQLException {
        Connection connection=DbConnection.getConnection();
        String sql="select id from roles where role=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);

        preparedStatement.setString(1,role);
        ResultSet resultSet=preparedStatement.executeQuery();
        if(resultSet.next()){
            UUID id=resultSet.getObject("id", UUID.class);
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return id;

        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
        throw new RuntimeException("Role Not Found: "+role);
    }

    //Extract Role
    public String extractRole(UUID roleId) throws SQLException {

        Connection connection= DbConnection.getConnection();
        String sql="select role from roles where id=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setObject(1,roleId);
        ResultSet resultSet=preparedStatement.executeQuery();
        if(resultSet.next()){
            String role=resultSet.getString("role");
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return role;
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return null;
    }


    public AllRecords.Company getCompany() throws SQLException {
        Connection connection=DbConnection.getConnection();

        Statement statement=connection.createStatement();
        String sql="select * from company";
        ResultSet resultSet=statement.executeQuery(sql);
        resultSet.next();
            AllRecords.Company company=new AllRecords.Company(resultSet.getObject("id", UUID.class),
                    resultSet.getString("companyname"),
                    resultSet.getTimestamp("createdAt"));

            return company;


    }




    public List<Products> getProductByBusiness(String businessName) throws SQLException {
        Connection connection=DbConnection.getConnection();
        String sql="select * from products where businessname=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setString(1,businessName);
        ResultSet resultSet=preparedStatement.executeQuery();
        List<Products>products=new ArrayList<>();
        while(resultSet.next()){
            Products product=new Products(resultSet.getObject("id", UUID.class),
                    resultSet.getString("productname"),
                    resultSet.getBigDecimal("price"),
                    resultSet.getInt("quantity"),
                    resultSet.getObject("sellerid", UUID.class),
                    resultSet.getString("businessname"),
                    resultSet.getObject("company_id", UUID.class));
            products.add(product);
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return products;
    }

    public List<Products> getProductsByCompany(UUID companyId) throws SQLException {
        Connection connection=DbConnection.getConnection();
        String sql="select * from products where company_id=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setObject(1,companyId);
        ResultSet resultSet=preparedStatement.executeQuery();
        List<Products>products=new ArrayList<>();
        while (resultSet.next()){
            products.add(new Products(resultSet.getObject("id", UUID.class),
                    resultSet.getString("productname"),
                    resultSet.getBigDecimal("price"),
                    resultSet.getInt("quantity"),
                    resultSet.getObject("sellerid", UUID.class),
                    resultSet.getString("businessname"),
                    resultSet.getObject("company_id", UUID.class)));

        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return products;
    }


    public UUID BuyerRegister(Buyers buyer, UUID roleId) throws SQLException {
        Connection con=DbConnection.getConnection();

        String sql="Insert into users (name,email,password,role_id,status) values (?,?,?,?,?)";
        PreparedStatement preparedStatement=con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1,buyer.getName());
        preparedStatement.setString(2,buyer.getEmail());
        preparedStatement.setString(3,buyer.getPassword());
        preparedStatement.setObject(4,roleId);
        preparedStatement.setString(5,"Active");

        preparedStatement.executeUpdate();
        ResultSet rs=preparedStatement.getGeneratedKeys();
        if(rs.next()){
            UUID userid=rs.getObject(1, UUID.class);
            rs.close();
            preparedStatement.close();
            con.close();
            return userid;
        }
        rs.close();
        preparedStatement.close();
        con.close();
        throw new RuntimeException("Id Generation Failed");
    }

    public void BuyerRegisterinBuyers(Buyers buyer,UUID buyer_id) throws SQLException {
        Connection connection=DbConnection.getConnection();
        String sql="Insert into buyers (id,name,email,company_id,phoneNo) values(?,?,?,?,?)";
        PreparedStatement preparedStatement= connection.prepareStatement(sql);
        preparedStatement.setObject(1,buyer_id);
        preparedStatement.setString(2,buyer.getName());
        preparedStatement.setString(3,buyer.getEmail());
        preparedStatement.setObject(4,buyer.getCompany_id());
        preparedStatement.setObject(5,buyer.getPhoneNo());

        preparedStatement.executeUpdate();

        preparedStatement.close();
        connection.close();
    }
}
