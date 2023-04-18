package com.example.wasteless.roomdatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface StoreDao {

    @Insert
    void registerStore(StoreEntity storeEntity);

    @Query("SELECT * from Store where storeID=(:storeID) and senha=(:senha)")
    StoreEntity login(String storeID, String senha);

    @Query("SELECT * from Store where storeID=(:storeID)")
    StoreEntity loginEmail(String storeID);
}
