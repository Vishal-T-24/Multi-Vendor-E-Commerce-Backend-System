package org.example;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Util.ApiResponse;
import org.example.Util.ResponseUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class Router implements HttpHandler {

    Gson gson=new Gson();
    ResponseUtil responseUtil=new ResponseUtil();

    String method;
    String path;
    BiConsumer<HttpExchange,String>handler;

    public Router() {
    }

    public Router(String method, String path, BiConsumer<HttpExchange, String> handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    List<Router> router=new ArrayList<>();

    public void register(String method,String path,BiConsumer<HttpExchange,String>handler){

        router.add(new Router(method,path,handler));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String requestMethod=exchange.getRequestMethod();
        String requestPath=exchange.getRequestURI().getPath();

      for(Router route:router){
          if(!route.method.equalsIgnoreCase(requestMethod)){
              continue;
          }
          if(route.path.equalsIgnoreCase(requestPath)){
              try {
                  route.handler.accept(exchange, null);
              }catch (Exception e){
                  SendException(exchange,e);

              }
              return;
          }
          if(route.path.endsWith("/{id}")){
              String temp=route.path.replace("/{id}","");
              if(requestPath.startsWith(temp+"/")){
                  String id=requestPath.substring(temp.length()+1);
                  try {
                      route.handler.accept(exchange, id);
                  }catch (Exception e){
                      SendException(exchange,e);

                  }
                  return;
              }

          }
          if(route.path.endsWith("/{businessname}")){
              String temp=route.path.replace("/{businessname}","");
              if(requestPath.startsWith(temp+"/")){
                  String businessname=requestPath.substring(temp.length()+1);
                  try {
                      route.handler.accept(exchange, businessname);
                  }catch (Exception e){
                      SendException(exchange,e);

                  }
                  return;
              }

          }
      }


    }
    public void SendException(HttpExchange exchange,Exception e) throws IOException {
        ApiResponse<?>response=new ApiResponse<>("Unhandled Exception",false,500,e.getMessage());
        responseUtil.sendResponse(exchange,response);
    }
}
