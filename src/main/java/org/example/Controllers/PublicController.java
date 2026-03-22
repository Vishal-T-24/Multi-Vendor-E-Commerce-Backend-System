package org.example.Controllers;
import java.time.LocalDateTime;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import org.example.Domains.Buyers;
import org.example.Domains.Products;
import org.example.Domains.Seller;
import org.example.Domains.Users;
import org.example.Records.*;
import org.example.Services.PublicService;
import org.example.Util.ApiResponse;
import org.example.Util.CookieUtil;
import org.example.Util.JwtUtil;
import org.example.Util.ResponseUtil;

import java.io.InputStream;

public class PublicController  {

    Gson gson=new Gson();
    PublicService publicService=new PublicService();
    JwtUtil jwtUtil=new JwtUtil();
    ResponseUtil responseUtil=new ResponseUtil();
    CookieUtil cookieUtil=new CookieUtil();


    /* Login User*/
    public void LoginUser(HttpExchange exchange)  {

        try {
            InputStream Is = exchange.getRequestBody();
            String requestBody = new String(Is.readAllBytes());

            AllRecords.UserRecord userRecord = gson.fromJson(requestBody, AllRecords.UserRecord.class);

            UUID role_id = publicService.loginUser(userRecord);
            String role=publicService.extrctRole(role_id);

            if (role != null) {
                String Accesstoken = jwtUtil.generateToken(userRecord.email(), role);
                String Refreshtoken=jwtUtil.generateRefreshToken(userRecord.email(),role);
                UUID userId=publicService.extractuserId(userRecord.email());
                cookieUtil.setRefreshTokenCookie(exchange,Refreshtoken);
                publicService.saveTokenInDb(Refreshtoken,userId);
                    ApiResponse<String>response=new ApiResponse<>("Login Successfull",true,200,Accesstoken,Refreshtoken);
                    responseUtil.sendResponse(exchange,response);
            } else {
               ApiResponse<String>response=new ApiResponse<>("Unauthorized User",false,401);
               responseUtil.sendResponse(exchange,response);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*Seller Request*/
    public void SellerRequest(HttpExchange exchange)  {

        try {
            InputStream Is = exchange.getRequestBody();
            String requestBody = new String(Is.readAllBytes());
            Seller Seller = gson.fromJson(requestBody, Seller.class);

            if (publicService.SellerRequest(Seller)) {
                ApiResponse<String>response=new ApiResponse<>("Your Request Sent Successfully, let's Wait for Approval",true,201);
                responseUtil.sendResponse(exchange,response);
            } else {
                ApiResponse<String>response=new ApiResponse<>("Please Provide Valid Details",false,400);
                responseUtil.sendResponse(exchange,response);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    /* Buyer Register*/
    public void BuyerRegister(HttpExchange exchange) {

        try {
            InputStream Is = exchange.getRequestBody();
            String requestBody=new String(Is.readAllBytes());
            Buyers buyer =gson.fromJson(requestBody,Buyers.class);

            if (publicService.BuyerRegister(buyer)) {
                ApiResponse<String>response=new ApiResponse<>("Account Created Successfully",true,201);
                responseUtil.sendResponse(exchange,response);
            } else {
                ApiResponse<String>response=new ApiResponse<>("Invalid Credentials",false,400);
                responseUtil.sendResponse(exchange,response);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*GetAll Products*/
    public void getAllProducts(HttpExchange exchange)  {

        try {
                List<Products> products = publicService.getAllProducts();
               ApiResponse<List<Products>>response=new ApiResponse<>("These are the Availabled Products",true,200,products);
               responseUtil.sendResponse(exchange,response);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    /*Get Product By Id*/
    public void getProductById(HttpExchange exchange,String temp_id)  {
        UUID id=UUID.fromString(temp_id);
        try {
            if(publicService.getProductById(id)==null){
                ApiResponse<String>response=new ApiResponse<>("Product Not Available",false,404,null);
                responseUtil.sendResponse(exchange,response);
            }
            ApiResponse<Products>response=new ApiResponse<>("The Product",true,200,publicService.getProductById(id));
            responseUtil.sendResponse(exchange,response);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    /*Place Order*/
    public void placeOrder(HttpExchange exchange) {
        try {
            String authheader = exchange.getRequestHeaders().getFirst("Authorization");
            if (authheader == null || !authheader.startsWith("Bearer ")) {
                ApiResponse<String>response=new ApiResponse<>("Please Provide Valid Authentication", false, 403);
                responseUtil.sendResponse(exchange, response);
                return;
            }

            String token = authheader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                ApiResponse<String>response=new ApiResponse<>("Unauthorized User", false,401);
                responseUtil.sendResponse(exchange, response);
                return;
            }

            InputStream Is = exchange.getRequestBody();
            String requestBody = new String(Is.readAllBytes());

            AllRecords.PlaceOrder order;
            try {
                 order = gson.fromJson(requestBody, AllRecords.PlaceOrder.class);
            } catch (JsonSyntaxException e) {
                ApiResponse<String>response=new ApiResponse<>("Quantity Should be Only in Whole Number Not in Decimal",false,400);
                responseUtil.sendResponse(exchange,response);
                return;
            }

            if (order.productId() == null || order.productId().toString().isBlank()) {
                ApiResponse<String>response=new ApiResponse<>("Please Provide Product Id", false,400);
                responseUtil.sendResponse(exchange, response);
                return;
            }

            if (order.quantity()<=0) {
                ApiResponse<String>response=new ApiResponse<>("Minimum Atleast 1 Quantity Required", false,400);
                responseUtil.sendResponse(exchange,response);
                return;
            }


            UUID userId = publicService.extractuserId(jwtUtil.extractEmail(token));

            // service returns ApiResponse directly
            ApiResponse<AllRecords.OrderDetails> response = publicService.PlaceOrder(order, userId);
            responseUtil.sendResponse(exchange, response);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*Refresh Api*/
    public void Refresh(HttpExchange exchange){
        try{
            String refreshToken=cookieUtil.extractRefreshToken(exchange);
            if(refreshToken==null){
                responseUtil.sendResponse(exchange, new ApiResponse("No Refresh Token Found", false, 401));
                return;
            }

            AllRecords.RefreshToken tokenDetails=publicService.RefreshToken(refreshToken);
            if (tokenDetails == null) {
                responseUtil.sendResponse(exchange, new ApiResponse("Invalid Refresh Token", false, 401));
                return;
            }
            if(!tokenDetails.isActive()){
                responseUtil.sendResponse(exchange,new ApiResponse("Already Used Token",false,401));
                return;
            }
            if(tokenDetails.expiry().isBefore(LocalDateTime.now())){
                responseUtil.sendResponse(exchange,new ApiResponse("Expired Token, Please Login Again",false,401));
                return;
            }
            publicService.deactivateOldToken(refreshToken);

            String email=jwtUtil.extractEmail(refreshToken);
            String role=jwtUtil.extractRole(refreshToken);
            String newRefreshToken= jwtUtil.generateRefreshToken(email,role);

            UUID userId=publicService.extractuserId(email);
            publicService.saveTokenInDb(newRefreshToken,userId);

            cookieUtil.setRefreshTokenCookie(exchange,newRefreshToken);

            String AccessToken=jwtUtil.generateToken(email,role);
            responseUtil.sendResponse(exchange,new ApiResponse<>("AccessToken",true,200,AccessToken));

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*GetToCompanySite*/
    public void getCompanySite(HttpExchange exchange){
        try{

           AllRecords.Company company= publicService.getCompany();

           responseUtil.sendResponse(exchange,new ApiResponse<>("Welcome to ShopZone!",true,200,company));

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*GetProductByBusinessName*/
    public void getProductByBusinessName(HttpExchange exchange,String businessName){
        try{

           List<Products>products=publicService.getProductByBusiness(businessName);
           if(products.isEmpty()){
               responseUtil.sendResponse(exchange,new ApiResponse(businessName+" had no Products on ShopZone",false,404));
               return;
           }

           responseUtil.sendResponse(exchange,new ApiResponse("These are the Products of "+businessName,true,200,products));

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*Get Products by Company*/
    public void getProductByCompany(HttpExchange exchange,String id){
        try{
            UUID companyId=UUID.fromString(id);
            List<Products>products=publicService.getProductsByCompany(companyId);
            if(products.isEmpty()){
                responseUtil.sendResponse(exchange,new ApiResponse("There is No Products On these Company",false,204));
                return;
            }
            responseUtil.sendResponse(exchange,new ApiResponse("These are the Products of "+companyId,true,200,products));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
