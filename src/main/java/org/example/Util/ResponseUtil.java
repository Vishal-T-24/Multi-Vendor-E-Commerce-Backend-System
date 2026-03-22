package org.example.Util;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ResponseUtil {

    Gson gson=new Gson();
    /*Response*/
    public void sendResponse(HttpExchange exchange, ApiResponse apiResponse) throws IOException {
        String response=gson.toJson(apiResponse);

        exchange.getResponseHeaders().set("Content-Type","application/json");
        byte[]bytes=response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(apiResponse.getStatuscode(),bytes.length);

        OutputStream os=exchange.getResponseBody();
        os.write(bytes);
        os.close();

    }



}
