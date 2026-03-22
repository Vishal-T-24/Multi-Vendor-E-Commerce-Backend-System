package org.example.Util;

import com.sun.net.httpserver.HttpExchange;

public class CookieUtil {

    /*Store Refresh Token in Cookie*/
    public void setRefreshTokenCookie(HttpExchange exchange, String refreshToken) {
        exchange.getResponseHeaders().add("Set-Cookie",
                "RefreshToken= " + refreshToken +
                        "; HttpOnly" +
                        "; Path=/" +
                        "; Max-Age=604800");
    }

    /*Extract Refresh Token from Cookies*/
    public String extractRefreshToken(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader == null) {
            return null;
        }
        String []str=cookieHeader.split(";");
        for (String cookie : str) {
            String[] parts = cookie.trim().split("=");
            if (parts[0].equals("RefreshToken")) {
                return parts[1];
            }
        }
        return null;
    }

}
