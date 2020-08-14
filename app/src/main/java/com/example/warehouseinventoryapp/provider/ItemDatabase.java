package com.example.warehouseinventoryapp.provider;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// create the ItemDatabase as abstract class so that it cannot be instantiated outside
@Database(entities = {Item.class}, version=1)
public abstract class ItemDatabase extends RoomDatabase {

    // set the name of the database
    public static final String ITEM_DATABASE_NAME = "item_database";

    // instantiate the DAO in the database
    public abstract ItemDao itemDao();

    // create an instance of the database
    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile ItemDatabase INSTANCE;

    // number of threads use in operation to manipulate the database
    private static final int NUMBER_OF_THREADS = 4;

    // initiate ExecutorService used to manipulate the database
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // return the database, create a database if current database is null
    static ItemDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ItemDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ItemDatabase.class, ITEM_DATABASE_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

