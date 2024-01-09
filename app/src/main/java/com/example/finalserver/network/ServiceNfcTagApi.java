package com.example.finalserver.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ServiceNfcTagApi {   // NFC 태그 인터페이스
    @FormUrlEncoded
    @POST("nfc_tag.php")      //태그한 정보를 서버에 올리는 request
    Call<String> Nfc_Tag(
            @Field("userEmail") String userEmail,
            @Field("NFC_UID") String nfcUID,
            @Field("status_exists") String status

         //   @Field("placeName") String placeName
    );

    @FormUrlEncoded
    @POST("nfc_tag_response.php")   //태그한 정보와 시간, 공간 정보를 받기 위한 request
    Call<String> placeInFo_response(
            @Field("requestPlaceInfo") String requestPlaceInfo
    );

}
