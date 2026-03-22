package org.example.Controllers;

import com.sun.net.httpserver.HttpExchange;
import org.example.Domains.Users;
import org.example.Records.AllRecords;
import org.example.Services.AdminService;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import org.example.Util.ApiResponse;
import org.example.Util.JwtUtil;
import org.example.Util.PasswordUtil;
import org.example.Util.ResponseUtil;


public class AdminController {

    private AdminService adminService=new AdminService();
    PasswordUtil passwordUtil=new PasswordUtil();
    ResponseUtil responseUtil=new ResponseUtil();
    Gson gson =new Gson();
    JwtUtil jwtUtil=new JwtUtil();

    /*Add Comapany*/
    public void addCompany(HttpExchange exchange){
        try{
            String authheader=exchange.getRequestHeaders().getFirst("Authorization");
            if(authheader==null || !authheader.startsWith("Bearer ")){
                responseUtil.sendResponse(exchange,new ApiResponse("Please Provide Valid Authentication",false,403));
                return;
            }
            String token=authheader.substring(7);
            if(!jwtUtil.validateToken(token) || !jwtUtil.extractRole(token).equalsIgnoreCase("ADMIN")){
                responseUtil.sendResponse(exchange,new ApiResponse("Unauthorized User",false,401));
                return;
            }
            InputStream is=exchange.getRequestBody();
            String requestBody=new String(is.readAllBytes());
            AllRecords.Companyname companyname=gson.fromJson(requestBody,AllRecords.Companyname.class);

            if(companyname == null){
                responseUtil.sendResponse(exchange,new ApiResponse("Please Provide Valid Company Details",false,400));
                return;
            }
            adminService.addcompany(companyname);
            responseUtil.sendResponse(exchange,new ApiResponse("Company Created Successfully",true,201));

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*Accept Sellers*/
        public void AcceptSellers (HttpExchange exchange, String id)  {
        try {
            UUID sellerId = UUID.fromString(id);
            String auhtheader=exchange.getRequestHeaders().getFirst("Authorization");
            if(auhtheader==null && !auhtheader.startsWith("Bearer ")) {
                ApiResponse<String>response=new ApiResponse<>("Please Provide Valid Authentication",false,403);
                responseUtil.sendResponse(exchange,response);
                return;

            }
            String token=auhtheader.substring(7);

            if(!jwtUtil.validateToken(token) || !jwtUtil.extractRole(token).equalsIgnoreCase("ADMIN")) {
                ApiResponse<String>response=new ApiResponse<>("Unauthorized User",false,401);
                responseUtil.sendResponse(exchange,response);
                return;

            }
            if (adminService.acceptSellers(sellerId)) {

                   ApiResponse<String>response=new ApiResponse<>("Seller Approved",true,201);
                   responseUtil.sendResponse(exchange,response);

            } else {

                   ApiResponse<String>response=new ApiResponse<>("Seller Not Found",false,404);
                   responseUtil.sendResponse(exchange,response);

            }


        }catch (Exception e){
            throw new RuntimeException(e.getMessage());

        }


    }

    //Get All Users
    public void getAllUsers(HttpExchange exchange) {
        try {
            String authheader = exchange.getRequestHeaders().getFirst("Authorization");
            if (authheader == null || !authheader.startsWith("Bearer ")) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Please Provide Valid Authentication", false, 403));
                return;
            }

            String token = authheader.substring(7);
            if (!jwtUtil.validateToken(token) || !jwtUtil.extractRole(token).equalsIgnoreCase("ADMIN")) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Unauthorized User", false, 401));
                return;
            }

            List<Users> users = adminService.getAllUser();
            responseUtil.sendResponse(exchange, new ApiResponse<>("These are the Available Users", true, 200, users));

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*Get All Sellers*/
    public void getAllSellers(HttpExchange exchange){

            try {
                String authheader = exchange.getRequestHeaders().getFirst("Authorization");
                if (authheader == null || !authheader.startsWith("Bearer ")) {
                    responseUtil.sendResponse(exchange, new ApiResponse<>("Please Provide Valid Authentication", false, 403));
                    return;
                }

                String token = authheader.substring(7);
                if (!jwtUtil.validateToken(token) || !jwtUtil.extractRole(token).equalsIgnoreCase("ADMIN")) {
                    responseUtil.sendResponse(exchange, new ApiResponse<>("Unauthorized User", false, 401));
                    return;
                }

                List<AllRecords.SellerRecord> sellers = adminService.getAllSeller();
                responseUtil.sendResponse(exchange, new ApiResponse<>("Seller Details", true, 200, sellers));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

    }

    /*Delete Seller By SellerId*/
    public void deleteSeller(HttpExchange exchange, String Pathid) {
        try {
            UUID id = UUID.fromString(Pathid);

            String authheader = exchange.getRequestHeaders().getFirst("Authorization");
            if (authheader == null || !authheader.startsWith("Bearer ")) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Please Provide Valid Authentication", false, 403));
                return;
            }

            String token = authheader.substring(7);
            if (!jwtUtil.validateToken(token) || !jwtUtil.extractRole(token).equalsIgnoreCase("ADMIN")) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Unauthorized User", false, 401));
                return;
            }

            if (adminService.deleteSellerById(id)) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Seller " + id + " has been removed", true, 200));
            } else {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Seller Not Found", false, 404));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*Get All Orders Details*/
    public void getAllOrders(HttpExchange exchange){
        Map<String,Object>map=new HashMap<>();
        int statusCode;
        try{
            String authheader=exchange.getRequestHeaders().getFirst("Authorization");
            if(authheader==null && !authheader.startsWith("Bearer ")) {
                ApiResponse<String>response=new ApiResponse<>("Please Provide Valid Authentication",false,403);
                responseUtil.sendResponse(exchange,response);
                return;
            }
            String token=authheader.substring(7);
            if(!jwtUtil.validateToken(token) || !jwtUtil.extractRole(token).equalsIgnoreCase("ADMIN")) {
                responseUtil.sendResponse(exchange,new ApiResponse("Unauthorized User",false,401));
                return;
            }
           List<AllRecords.OrderDetails>orderDetails= adminService.getAllOrders();
           if(orderDetails!=null){
              responseUtil.sendResponse(exchange,new ApiResponse<>("Order Details",true,200,orderDetails));

           }
           else{
              responseUtil.sendResponse(exchange,new ApiResponse<>("There are no orders",true,200));
           }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    //Add Another Admin
    public void AddAdmin(HttpExchange exchange) {
        try {
            InputStream Is = exchange.getRequestBody();
            String requestBody = new String(Is.readAllBytes());
            Users user = gson.fromJson(requestBody, Users.class);

            String authheader = exchange.getRequestHeaders().getFirst("Authorization");
            if (authheader == null || !authheader.startsWith("Bearer ")) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Please Provide Valid Authentication", false, 403));
                return;
            }

            String token = authheader.substring(7);
            if (!jwtUtil.validateToken(token) || !jwtUtil.extractRole(token).equalsIgnoreCase("ADMIN")) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Unauthorized User", false, 401));
                return;
            }

            if (adminService.addAdmin(user)) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Admin Added Successfully", true, 201));
            } else {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Please Provide Valid Details", false, 400));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
