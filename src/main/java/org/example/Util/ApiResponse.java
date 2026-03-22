package org.example.Util;

public class ApiResponse<T> {

    private String message;
    private boolean success;
    private transient int statuscode;
    private T data;
    private String Access_Token;
    private String Refresh_Token;

    public ApiResponse(String message, boolean success, int statuscode, String access_Token, String refresh_Token) {
        this.message = message;
        this.success = success;
        this.statuscode = statuscode;
        Access_Token = access_Token;
        Refresh_Token = refresh_Token;
    }

    public ApiResponse(String message, boolean success, int statuscode, T data) {
        this.message = message;
        this.success = success;
        this.statuscode = statuscode;
        this.data = data;
    }
    public ApiResponse(String message, boolean success, int statuscode, String Token) {
        this.message = message;
        this.success = success;
        this.statuscode = statuscode;
        this.Access_Token=Token;
    }

    public ApiResponse(String message, boolean success, T data) {
        this.message = message;
        this.success = success;
       this.data=data;
    }

    public ApiResponse(String message, boolean success,int statuscode) {
        this.message = message;
        this.success = success;
        this.statuscode=statuscode;

    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(int statuscode) {
        this.statuscode = statuscode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getRefresh_Token() {
        return Refresh_Token;
    }

    public void setRefresh_Token(String refresh_Token) {
        Refresh_Token = refresh_Token;
    }

    public String getToken() {
        return Access_Token;
    }

    public void setToken(String token) {
        Access_Token = token;
    }
}
