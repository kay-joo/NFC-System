package com.example.finalserver.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {          //Retrofit 설정 클래스
    public final static String BASE_URL="http://ec2-3-34-2-57.ap-northeast-2.compute.amazonaws.com/";
    public static Retrofit retrofit;
    private String authToken;

    private RetrofitClient(String authToken){   //RetrofitClient 클래스의 생성자
        //내용 없음
        this.authToken = authToken;
    }

    public static Retrofit joinConfig(){        //회원가입 설정 메소드
        Retrofit joinConfig = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .build();
        return joinConfig;
    }

    public static Retrofit loginConfig(){      //로그인 설정 메소드
       // Gson gson = new GsonBuilder()
            //    .setLenient()
             //   .create();

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    //.addConverterFactory(GsonConverterFactory.create(gson))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit logoutConfig(){
        Retrofit loginConfig = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return loginConfig;
    }
}
