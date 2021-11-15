package com.pucmm.proyecto_final.room_view_model.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pucmm.proyecto_final.room_view_model.model.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Query("SELECT * FROM user ORDER BY id")
    LiveData<List<User>> findAll();

    @Query("SELECT * FROM user where email = :email and password= :password")
    User login(String email, String password);


    @Query("SELECT * FROM user where email = :email")
    User findByEmail(String email);

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);
}
