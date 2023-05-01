package com.example.wasteless.roomdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CartEntity.class}, version = 1)
public abstract class CartDatabase extends RoomDatabase {

    private static final String dbName = "cart";
    private static CartDatabase cartDatabase;

    public static synchronized CartDatabase getCartDatabase(Context context) {
        if (cartDatabase == null) {
            cartDatabase = Room.databaseBuilder(context, CartDatabase.class, dbName)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return cartDatabase;
    }

    public abstract CartDao cartDao();
}
