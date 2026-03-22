package org.example.Domains;



import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Users {

    private UUID id;
    private String name;
    private String email;
    private String password;
    private UUID role_id;
    private  String status;
    private Timestamp createdat;

    public Users(UUID id, String name, String email, String password, UUID role_id, String status, Timestamp createdat) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role_id = role_id;
        this.status = status;
        this.createdat=createdat;
    }

    public Users() {
    }

    public Users(UUID id, String name, String email, String password, UUID role_id ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role_id = role_id;

    }

   public Users(String name,String email,String password,UUID role_id,String status){
        this.name=name;
        this.email=email;
        this.password=password;
        this.role_id=role_id;
        this.status=status;
   }
   public Users (String name, String email,String Password){
        this.name=name;
        this.email=email;
        this.password=Password;
   }


    public UUID getId() {
        return id;
    }

    public void setid(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getRole_id() {
        return role_id;
    }

    public void setRole_id(UUID role_id) {
        this.role_id = role_id;
    }



}
