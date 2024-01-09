package com.example.finalserver.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ServiceEntList {   // 출입기록 인터페이스
    @FormUrlEncoded
    @POST("request_onlyUser_list.php")
    Call<String> request_onlyUser_list(
            @Field("userEmail") String userEmail
           // @Field("placeName") String placeName
    );


    @FormUrlEncoded
    @POST("ent_list.php")
    Call<ListResponse> ent_List(
            @Field("userName") String userName,
            @Field("addmission_time") String addmission_time
    );

}
