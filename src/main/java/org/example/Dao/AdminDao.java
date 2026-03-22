package org.example.Dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.Domains.Users;
import org.example.Records.AllRecords;
import org.example.Util.DbConnection;
import org.example.Util.RedisConnection;
import redis.clients.jedis.Jedis;

import java.sql.*;
import java.util.*;

public class AdminDao {

    Gson gson=new Gson();

    //GetAllUsers
    public List<Users> getAllUsers() throws SQLException {

        List<Users> newUser=new ArrayList<>();

        //Check Redis First
        try(Jedis jedis=RedisConnection.getJedis()){
            String cached=jedis.get("getAllUsers");
            if(cached!=null){
//                System.out.println("Serving from Redis");
                return gson.fromJson(cached,new TypeToken<List<Users>>(){}.getType());
            }
        }

        //Get Data from Db if Not in Redis
        Connection con=DbConnection.getConnection();

        String Sql="select * from Users";
        Statement s=con.createStatement();
        ResultSet rs= s.executeQuery(Sql);

        while(rs.next()){
            Users user=new Users(rs.getObject("id", UUID.class),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getObject("role_id", UUID.class),
                    rs.getString("status"),
                    rs.getTimestamp("created_at"));

            newUser.add(user);

        }
        //Store in redis
        try(Jedis jedis= RedisConnection.getJedis()){
            jedis.setex("getAllUsers",3600,gson.toJson(newUser));
//            System.out.println("Stored in Redis");
        }

        s.close();
        con.close();
        return newUser;
    }

    public int AddAdmin(Users user) throws SQLException {
        Connection con=DbConnection.getConnection();

        String sql="Insert into users (name,email,password,role,status)values(?,?,?,?,?)";
        PreparedStatement preparedStatement=con.prepareStatement(sql);
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2,user.getEmail());
        preparedStatement.setString(3,user.getPassword());
        preparedStatement.setString(4,"ADMIN");
        preparedStatement.setString(5,"Active");

       int confirmation= preparedStatement.executeUpdate();

        preparedStatement.close();
        con.close();

       return confirmation;

    }

    public int acceptSellers(UUID sellerId) throws SQLException {
        Connection con=DbConnection.getConnection();

        String sql="update users set status=? where id=? ";
        PreparedStatement preparedStatement=con.prepareStatement(sql);

        preparedStatement.setString(1,"Active");
        preparedStatement.setObject(2,sellerId);

       int confirmation= preparedStatement.executeUpdate();

       if(confirmation!=0){
           try(Jedis jedis=RedisConnection.getJedis()){
               jedis.del("getAllUsers");
               System.out.println("Cache Updated");
           }
       }

        preparedStatement.close();
        con.close();

        return confirmation;
    }

    public int deleteSellerById(UUID id) throws SQLException {

        Connection con=DbConnection.getConnection();

        String sql="delete from users where id=?";
        PreparedStatement preparedStatement=con.prepareStatement(sql);
        preparedStatement.setObject(1,id);

        int confirmation = preparedStatement.executeUpdate();

        if(confirmation!=0){
            try(Jedis jedis=RedisConnection.getJedis()){
                jedis.del("getAllUsers");
                System.out.println("Cache Evicted");
            }
        }

        preparedStatement.close();
        con.close();

        return confirmation;

    }

    public List<AllRecords.SellerRecord> getAllSeller() throws SQLException {

        List<AllRecords.SellerRecord>sellers=new ArrayList<>();

       Connection connection= DbConnection.getConnection();
       String sql="select u.id,u.name,u.email,u.status,u.role_id,s.businessname,s.phone,s.address,s.companyid from users u inner join sellers s on u.id=s.sellerid";
       PreparedStatement preparedStatement=connection.prepareStatement(sql);

       ResultSet rs=preparedStatement.executeQuery();

       while (rs.next()){
          sellers.add(new AllRecords.SellerRecord(rs.getObject("id", UUID.class),
                  rs.getString("name"),
                  rs.getString("email"),
                  rs.getString("status"),
                  rs.getObject("role_id", UUID.class),
                  rs.getString("businessname"),
                  rs.getLong("phone"),
                  rs.getString("address"),
                  rs.getObject("companyid", UUID.class)));

       }
       rs.close();
       preparedStatement.close();
       connection.close();
       return sellers;
    }

    public List<AllRecords.OrderDetails> getAllOrders() throws SQLException {
        Connection connection=DbConnection.getConnection();

        Statement statement=connection.createStatement();
        String sql="select * from orders";
        ResultSet rs= statement.executeQuery(sql);
        List<AllRecords.OrderDetails>orderDetails=new ArrayList<>();
        while(rs.next()){
            AllRecords.OrderDetails orders=new AllRecords.OrderDetails(rs.getObject("id", UUID.class),
                    rs.getObject("productid", UUID.class),
                    rs.getBigDecimal("priceperproduct"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("totalamount"),
                    rs.getTimestamp("orderat"),
                    rs.getString("orderstatus"),
                    rs.getObject("userid", UUID.class),
                    rs.getObject("sellerid", UUID.class),
                    rs.getObject("companyid", UUID.class));

            orderDetails.add(orders);
        }
        rs.close();
        statement.close();
        connection.close();
        return orderDetails;
    }

    public void addCompany(AllRecords.Companyname companyname) throws SQLException {
        Connection connection=DbConnection.getConnection();
        String sql="Insert into company(companyname)values(?)";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setString(1,companyname.name());

        preparedStatement.executeUpdate();
    }
}
