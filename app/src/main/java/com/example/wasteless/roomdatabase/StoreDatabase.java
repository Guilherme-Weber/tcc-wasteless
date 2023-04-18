package com.example.wasteless.roomdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {StoreEntity.class}, version = 1)
public abstract class StoreDatabase extends RoomDatabase {

    private static final String dbName = "store";
    private static StoreDatabase storeDatabase;

    public static synchronized StoreDatabase getStoreDatabase(Context context) {
        if (storeDatabase == null) {
            storeDatabase = Room.databaseBuilder(context, StoreDatabase.class, dbName)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return storeDatabase;
    }

    public abstract StoreDao storeDao();
}
