package com.example.oceanizeandroidcodingtask.service;

import com.example.oceanizeandroidcodingtask.model.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitInterface {

    @GET("/ssh")
    Call<List<Item>> getsshDetails();
}
