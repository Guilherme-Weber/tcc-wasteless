package com.example.wasteless.roomdatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Aluno")
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    Integer id;

    @ColumnInfo(name = "userID")
    String UserId;

    @ColumnInfo(name = "senha")
    String Senha;

    @ColumnInfo(name = "nome")
    String Nome;

    @ColumnInfo(name = "horas")
    int Horas;

    @ColumnInfo(name = "fav1")
    String Fav1;

    @ColumnInfo(name = "fav2")
    String Fav2;

    @ColumnInfo(name = "fav3")
    String Fav3;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getSenha() {
        return Senha;
    }

    public void setSenha(String senha) {
        Senha = senha;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public int getHoras() {
        return Horas;
    }

    public void setHoras(int horas) {
        Horas = horas;
    }

    public String getFav1() {
        return Fav1;
    }

    public void setFav1(String fav1) {
        Fav1 = fav1;
    }

    public String getFav2() {
        return Fav2;
    }

    public void setFav2(String fav2) {
        Fav2 = fav2;
    }

    public String getFav3() {
        return Fav3;
    }

    public void setFav3(String fav3) {
        Fav3 = fav3;
    }
}



