package com.example.finalserver.network;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("message")
    public String message;

    public String getMessage(){
        return message;
    }
}
