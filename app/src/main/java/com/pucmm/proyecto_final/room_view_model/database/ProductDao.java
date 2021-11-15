package com.pucmm.proyecto_final.room_view_model.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pucmm.proyecto_final.room_view_model.model.Product;
import com.pucmm.proyecto_final.room_view_model.model.User;

import java.util.List;
import java.util.UUID;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM product")
    LiveData<List<Product>> findAll();

    @Query("select * from product where id = :id")
    Product find(String id);

    @Insert
    void insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);
}
