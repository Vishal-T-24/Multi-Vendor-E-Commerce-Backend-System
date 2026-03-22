package org.example.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private final String secret="My Super Secure JWT SecretKey That Is AtLeast 32Bytes Long!";
    private final long AccessToken_Expiration=1000*60*10;
    private final long RefershToken_Expiration=1000*60*60*24*7;
    private final Key secretKey= Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    /*Generate Access_Token*/
    public String generateToken(String email,String role){
       return Jwts.builder()
                .setSubject(email)
               .claim("Role",role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+AccessToken_Expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    /*Generate Refresh_Token*/
    public String generateRefreshToken(String email,String role){
       return Jwts.builder()
                .setSubject(email)
                .claim("Role",role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+RefershToken_Expiration))
                .signWith(secretKey,SignatureAlgorithm.HS256)
                .compact();
    }

    /*Extract Role*/
    public String extractRole(String token){
        Claims claims= Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return  claims.get("Role",String.class);

    }

    /*Validate Token*/
    public boolean validateToken(String token){
        try{
            extractEmail(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*Extract Email*/
    public String extractEmail(String Token){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(Token)
                .getBody()
                .getSubject();
    }
}
