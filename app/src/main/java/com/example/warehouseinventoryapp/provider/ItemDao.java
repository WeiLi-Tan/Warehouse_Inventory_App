package com.example.warehouseinventoryapp.provider;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// interface to access data in database
@Dao
public interface ItemDao {

    @Query("select * from itemTable")
    LiveData<List<Item>> getAllItem();

    @Query("select * from itemTable where itemCost=:cost")
    List<Item> getSearchItem(int cost);

    @Insert
    void addItem(Item item);

//    @Query("delete from customers where customerName= :name")
//    void deleteCustomer(String name);

    @Query("delete FROM itemTable")
    void deleteAllItems();
}

