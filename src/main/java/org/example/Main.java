package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.Controllers.*;
import org.example.Util.DbConnection;

import java.io.IOException;
import java.net.InetSocketAddress;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {

        Router router=new Router();
        SellerController sellerController=new SellerController();
        PublicController publicController=new PublicController();
        AdminController adminController=new AdminController();

        DbConnection.getConnection().close();

        HttpServer server=HttpServer.create(new InetSocketAddress(8080),0);

        router.register("POST","/InsertProduct",((exchange, s) -> sellerController.insertProduct(exchange)));

        router.register("GET","/getAllProducts",((exchange, s) -> publicController.getAllProducts(exchange)));

        router.register("GET","/getProductById",((exchange, s) -> publicController.getProductById(exchange,s)));

        router.register("DELETE","/deleteProduct/{id}",((exchange, s) -> sellerController.DeleteProductById(exchange,s)));

        router.register("POST","/Login",((exchange, s) -> publicController.LoginUser(exchange)));

        router.register("POST","/SellerRequest",((exchange, s) -> publicController.SellerRequest(exchange)));

        router.register("POST","/AddAdmin",((exchange, s) -> adminController.AddAdmin(exchange)));

        router.register("PATCH","/ApproveSeller/{id}",((exchange, s) -> adminController.AcceptSellers(exchange,s)));

        router.register("POST","/RegisterBuyer",(((exchange, s) -> publicController.BuyerRegister(exchange))));

        router.register("GET","/getAllUsers",(((exchange, s) -> adminController.getAllUsers(exchange))));

        router.register("DELETE","/deleteSeller/{id}",((exchange, s) -> adminController.deleteSeller(exchange,s) ));

        router.register("GET","/getAllSellers",((exchange, s) -> adminController.getAllSellers(exchange)));

        router.register("POST","/placeOrder",((exchange, s) -> publicController.placeOrder(exchange)));

        router.register("GET","/getAllOrders",((exchange, s) -> adminController.getAllOrders(exchange)));

        router.register("GET","/getOrderDetails",((exchange, s) -> sellerController.Orderdetails(exchange)));

        router.register("GET","/getOwnProducts/{id}",((exchange, s) -> sellerController.getOwnProduct(exchange,s)));

        router.register("POST", "/refresh", (exchange, s) -> publicController.Refresh(exchange));

        router.register("POST","/UpdateStocks",((exchange, s) -> sellerController.updateStocks(exchange)));

        router.register("POST","/createCompany",((exchange, s) -> adminController.addCompany(exchange)));

        router.register("GET","/ShopZone",(exchange, s) -> publicController.getCompanySite(exchange));

        router.register("GET","/getProductsOf/{businessname}",(exchange, s) -> publicController.getProductByBusinessName(exchange,s));

        router.register("GET","/GetProductByCompany/{id}",(exchange, s) -> publicController.getProductByCompany(exchange,s));

        server.createContext("/",router);


        server.setExecutor(null);
        server.start();

        System.out.println("Server Started at http://localhost:8080");



    }
}