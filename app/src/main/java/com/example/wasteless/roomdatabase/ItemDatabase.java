package com.example.wasteless.roomdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ItemEntity.class}, version = 1)
public abstract class ItemDatabase extends RoomDatabase {

    private static final String dbName = "item";
    private static ItemDatabase itemDatabase;

    public static synchronized ItemDatabase getItemDatabase(Context context) {
        if (itemDatabase == null) {
            itemDatabase = Room.databaseBuilder(context, ItemDatabase.class, dbName)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return itemDatabase;
    }

    public abstract ItemDao itemDao();
}
