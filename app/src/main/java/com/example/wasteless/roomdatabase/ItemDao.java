package com.example.wasteless.roomdatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ItemDao {

    @Insert
    void registerItem(ItemEntity ItemEntity);

    @Query("SELECT * from Item where itemID=(:itemID) and titulo=(:titulo)")
    ItemEntity login(String itemID, String titulo);

    @Query("SELECT * from Item where ItemID=(:itemID)")
    ItemEntity loginEmail(String itemID);
}
