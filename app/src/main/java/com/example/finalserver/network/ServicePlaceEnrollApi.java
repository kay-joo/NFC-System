package com.example.finalserver.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ServicePlaceEnrollApi {   //장소 등록 인터페이스
    @FormUrlEncoded
    @POST("place_enroll.php")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Call<String> Place_Enroll(
            @Field(value = "NFC_UID", encoded = true) String NFC_UID,
            @Field(value = "placeName", encoded = true) String placeName,
            @Field(value = "placeAdd", encoded = true) String placeAdd,
            @Field("placeTel") String placeTel
    );

}
