package com.example.oceanizeandroidcodingtask.model;

import androidx.lifecycle.MutableLiveData;

import com.example.oceanizeandroidcodingtask.service.RetrofitInstance;
import com.example.oceanizeandroidcodingtask.service.RetrofitInterface;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ItemRepository {
    private MutableLiveData<List<Item>> itemList;

    //we will call this method to get the data
    public MutableLiveData<List<Item>> getSSItems() {
        //if the list is null
        if (itemList == null) {
            itemList = new MutableLiveData<List<Item>>();
            //we will load it asynchronously from server in this method
            loadItemValues();
        }

        //finally we will return the list
        return itemList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadItemValues() {
        RetrofitInterface retrofitInterface = RetrofitInstance.getService();

        Call<List<Item>> call = retrofitInterface.getsshDetails();


        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {

                //finally we are setting the list to our MutableLiveData
                itemList.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {

            }
        });
    }
}

