package com.example.finalserver.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ServiceJoinApi {   //회원가입 위한 요청 형식 인터페이스, 회원수정을 위한 요청 형식 인터페이스
    @FormUrlEncoded
    @POST("NewUserJoin.php")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Call<String> newUserJoin(
            @Field(value = "userEmail", encoded = true) String userEmail,
            @Field(value = "userPwd", encoded = true) String userPwd,
            @Field(value = "userName", encoded = true) String userName,
            @Field(value = "userAdd", encoded = true) String userAdd,
            @Field("userTel") String userTel
    );

    @FormUrlEncoded
    @POST("modifyAccount.php")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Call<String> modifyAccountRequest(
            @Field(value = "past_userEmail", encoded = true) String past_email,
            @Field(value = "userEmail", encoded = true) String email,
            @Field(value = "userPwd", encoded = true) String pwd,
            @Field(value = "userName", encoded = true) String name,
            @Field(value = "userAdd", encoded = true) String add,
            @Field("userTel") String tel
    );

}
