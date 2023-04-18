package com.example.wasteless.roomdatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {

    @Insert
    void registerUser(UserEntity userEntity);

    @Query("SELECT * from Aluno where userID=(:userID) and senha=(:senha)")
    UserEntity login(String userID, String senha);

    @Query("SELECT * from Aluno where userID=(:userID)")
    UserEntity loginEmail(String userID);

    @Query("Update Aluno set horas=(:horas) WHERE userID=(:userID)")
    int updateUserQ(int horas, String userID);
}
