package com.example.finalserver.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ServiceLoginApi {  //로그인 위한 요청 형식 인터페이스

   // @FormUrlEncoded
   // @POST("UserLogin.php")
   // Call<LoginResponse> userLogin(
    //        @Field("userEmail") String userEmail,
    //        @Field("userPwd") String userPwd
  //  );

    @FormUrlEncoded
    @POST("NewUserLogin.php")
    Call<String> userLogin(
            @Field("userEmail") String userEmail,
            @Field("userPwd") String userPwd

    );

    @FormUrlEncoded
    @POST("deleteAccount.php")      //계정 탈퇴 api
    Call<String> delete(
            @Field("userEmail") String userEmail,
            @Field("userPwd") String userPwd
    );

    @FormUrlEncoded
    @POST("logout.php")
    Call<String> logOut(@Field("ok") String okStr
    );


}
