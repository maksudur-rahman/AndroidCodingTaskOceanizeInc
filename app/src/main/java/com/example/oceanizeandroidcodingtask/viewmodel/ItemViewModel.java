package com.example.oceanizeandroidcodingtask.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.oceanizeandroidcodingtask.model.Item;
import com.example.oceanizeandroidcodingtask.model.ItemRepository;
import androidx.lifecycle.LiveData;


import java.util.List;

public class ItemViewModel extends AndroidViewModel {
    ItemRepository repository ;

    public ItemViewModel(@NonNull Application application) {
        super(application);
        repository = new ItemRepository();
    }

    public LiveData<List<Item>> getAllSSList(){
        return repository.getSSItems();
    }
}

