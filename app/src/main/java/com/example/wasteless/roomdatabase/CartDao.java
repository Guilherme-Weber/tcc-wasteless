package com.example.wasteless.roomdatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CartDao {

    @Insert
    void registerItem(CartEntity CartEntity);

    @Query("SELECT * from Item where cartID=(:cartID) and titulo=(:titulo)")
    ItemEntity login(String cartID, String titulo);

    @Query("SELECT * from Item where cartID=(:cartID)")
    ItemEntity loginEmail(String cartID);
}
