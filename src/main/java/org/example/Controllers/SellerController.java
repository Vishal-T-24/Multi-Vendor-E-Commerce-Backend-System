package org.example.Controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.example.Domains.Products;
import org.example.Records.AllRecords;
import org.example.Services.SellerService;
import org.example.Util.ApiResponse;
import org.example.Util.JwtUtil;
import org.example.Util.ResponseUtil;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class SellerController {

    Gson gson=new Gson();
    SellerService sellerService=new SellerService();
    JwtUtil jwtUtil=new JwtUtil();
    ResponseUtil responseUtil=new ResponseUtil();

    /*Insert Products*/
    public void insertProduct(HttpExchange exchange) {
        try {
            InputStream Is = exchange.getRequestBody();
            String requestBody = new String(Is.readAllBytes());

//            AllRecords products=new AllRecords.Products(gson.fromJson(requestBody, Products.class));
            Products product = gson.fromJson(requestBody, Products.class);

            String authheader = exchange.getRequestHeaders().getFirst("Authorization");
            if (authheader == null || !authheader.startsWith("Bearer ")) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Please Provide Valid Authentication", false, 403));
                return;
            }

            String token = authheader.substring(7);
            if (!jwtUtil.validateToken(token) || !jwtUtil.extractRole(token).equalsIgnoreCase("SELLER")) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Unauthorized User", false, 401));
                return;
            }

            if (!sellerService.SellerStatusVerification(jwtUtil.extractEmail(token))) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Please Wait Until Seller Request Get Approved", false, 403));
                return;
            }

            String email = jwtUtil.extractEmail(token);
            UUID sellerId = sellerService.extractSellerId(email);
            String bName=sellerService.extractBusinessname(sellerId);
            if(!product.getBusinessname().equalsIgnoreCase(bName)){
                responseUtil.sendResponse(exchange,new ApiResponse("Don't add Products in Others Business",false,403));
            }

            if (sellerService.addProduct(product, sellerId)) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Your Products Added Successfully in the Store", true, 201));
            } else {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Please Provide Correct Product Details", false, 400));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*Get their Own Product*/
    public void getOwnProduct(HttpExchange exchange, String tempId) {
        try {
            UUID id = UUID.fromString(tempId);

            String authheader = exchange.getRequestHeaders().getFirst("Authorization");
            if (authheader == null || !authheader.startsWith("Bearer ")) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Please Provide Valid Authentication", false, 403));
                return;
            }

            String token = authheader.substring(7);
            String role = jwtUtil.extractRole(token);
            if (!jwtUtil.validateToken(token) || (!role.equalsIgnoreCase("SELLER") && !role.equalsIgnoreCase("ADMIN"))) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Unauthorized User", false, 401));
                return;
            }


            List<Products> products = sellerService.getOwnProducts(id);

            if (products != null && !products.isEmpty()) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("These are Your Products for Selling", true, 200, products));
            } else {
                responseUtil.sendResponse(exchange, new ApiResponse<>("You don't have any Products for Selling", true, 204));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //DeleteProductById
    public void DeleteProductById(HttpExchange exchange, String temp_id) {
        try {
            UUID id = UUID.fromString(temp_id);

            String authheader = exchange.getRequestHeaders().getFirst("Authorization");
            if (authheader == null || !authheader.startsWith("Bearer ")) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Please Provide Valid Authentication", false, 403));
                return;
            }

            String token = authheader.substring(7);
            if (!jwtUtil.validateToken(token) || !jwtUtil.extractRole(token).equalsIgnoreCase("SELLER")) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Unauthorized User", false, 401));
                return;
            }


            String email = jwtUtil.extractEmail(token);

            if (sellerService.deleteProduct(id, email)) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Product Removed", true, 200));
            } else {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Unauthorized Seller to Remove this Product", false, 401));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //getOwnOrderDetails
    public void Orderdetails(HttpExchange exchange) {
        try {
            String authheader = exchange.getRequestHeaders().getFirst("Authorization");
            if (authheader == null || !authheader.startsWith("Bearer ")) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Please Provide Valid Authentication", false, 403));
                return;
            }

            String token = authheader.substring(7);
            if (!jwtUtil.validateToken(token) || !jwtUtil.extractRole(token).equalsIgnoreCase("SELLER")) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("Unauthorized User", false, 401));
                return;
            }

            String email = jwtUtil.extractEmail(token);
            UUID sellerId = sellerService.extractSellerId(email);
            List<AllRecords.OrderDetails> orderDetails = sellerService.getOrderdetails(sellerId);

            if (orderDetails != null && !orderDetails.isEmpty()) {
                responseUtil.sendResponse(exchange, new ApiResponse<>("These are Orders on your Products", true, 200, orderDetails));
            } else {
                responseUtil.sendResponse(exchange, new ApiResponse<>("No Orders on your Products", true, 204));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Update Stock
    public void updateStocks(HttpExchange exchange){
        try{
            String authHeader=exchange.getRequestHeaders().getFirst("Authorization");
            if(authHeader==null || !authHeader.startsWith("Bearer ")){
                responseUtil.sendResponse(exchange,new ApiResponse("Please Provide Valid Authentication",false,403));
                return;
            }
            String token=authHeader.substring(7);
            if(!jwtUtil.validateToken(token) || !jwtUtil.extractRole(token).equalsIgnoreCase("SELLER")){
                responseUtil.sendResponse(exchange,new ApiResponse("Unauthorized User",false,201));
                return;
            }
            String email=jwtUtil.extractEmail(token);
            UUID sellerId=sellerService.extractSellerId(email);

            InputStream is=exchange.getRequestBody();
            String requestBody=new String(is.readAllBytes());
            AllRecords.StockUpdate newStock=gson.fromJson(requestBody, AllRecords.StockUpdate.class);

            if(!sellerId.equals(sellerService.getsellerId(newStock.ProductId()))){
                responseUtil.sendResponse(exchange,new ApiResponse("You're Not Allowed to Update Others Stock",false,401));
                return;
            }

           responseUtil.sendResponse(exchange,sellerService.StockUpdate(newStock));

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }



}
