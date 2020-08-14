package com.example.warehouseinventoryapp.provider;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ItemRepository {

    // declare an ItemDao
    private ItemDao mItemDao;

    // declare a LiveData holder consists of a list of StockItem
    private LiveData<List<Item>> mAllItems;

    // constructor for ItemRepository
    ItemRepository(Application application) {
        // get database of the application
        ItemDatabase db = ItemDatabase.getDatabase(application);
        // get DAO of the database
        mItemDao = db.itemDao();
        // get all item in the database
        mAllItems = mItemDao.getAllItem();
    }

    // return all items in database
    LiveData<List<Item>> getAllItems() {
        return mAllItems;
    }

    // insert item into database using databaseWriteExecutor
    // change the source compatibility in project structure if there is an error
    void insert(Item item) {
        ItemDatabase.databaseWriteExecutor.execute(() -> mItemDao.addItem(item));
    }

    // delete all data in database using databaseWriteExecutor
    void deleteAll(){
        ItemDatabase.databaseWriteExecutor.execute(()->{
            mItemDao.deleteAllItems();
        });
    }
}
