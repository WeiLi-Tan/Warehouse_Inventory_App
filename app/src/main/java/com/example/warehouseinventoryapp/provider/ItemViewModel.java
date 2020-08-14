package com.example.warehouseinventoryapp.provider;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ItemViewModel extends AndroidViewModel {

    // declare ItemRepository
    private ItemRepository mRepository;

    // declare LiveData holder consist of list of StockItem
    private LiveData<List<Item>> mAllItems;

    // constructor for ItemViewModel
    public ItemViewModel(@NonNull Application application) {
        // run parent class constructor
        super(application);
        // instantiate ItemRepository
        mRepository = new ItemRepository(application);
        // get all data from repository
        mAllItems = mRepository.getAllItems();
    }

    // get all data from database
    public LiveData<List<Item>> getAllItems() {
        return mAllItems;
    }

    // insert data into database by calling method in repository
    public void insert(Item item) {
        mRepository.insert(item);
    }

    // delete all data in database by calling method in repository
    public void deleteAll(){
        mRepository.deleteAll();
    }
}