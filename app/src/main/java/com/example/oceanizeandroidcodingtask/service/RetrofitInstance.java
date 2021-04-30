package com.example.oceanizeandroidcodingtask.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit = null;
    private static String BASE_URL = "https://6088cfd3a6f4a30017426f6c.mockapi.io/api/v1/";

    public static RetrofitInterface getService(){

        if(retrofit ==null){
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(RetrofitInterface.class);

    }

}