package com.example.finalserver.network;

import com.google.gson.annotations.SerializedName;

public class ListResponse {

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

}
