package com.example.finalserver.network;

import com.google.gson.annotations.SerializedName;

public class EntData {

    @SerializedName("NFC_UID")
    String NFC_UID;

    @SerializedName("placeName")
    String placeName;

    @SerializedName("placeAdd")
    String placeAdd;

    @SerializedName("placeTel")
    String placeTel;


    public EntData(String NFC_UID, String placeName, String placeAdd,
                   String placeTel ) {

        this.NFC_UID = NFC_UID;
        this.placeName = placeName;
        this.placeAdd = placeAdd;
        this.placeTel = placeTel;
    }
}
