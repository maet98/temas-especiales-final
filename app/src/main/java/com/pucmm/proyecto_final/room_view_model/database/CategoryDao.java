package com.pucmm.proyecto_final.room_view_model.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pucmm.proyecto_final.room_view_model.model.Category;
import com.pucmm.proyecto_final.room_view_model.model.Product;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM category")
    LiveData<List<Category>> findAll();

    @Query("select * from category where id = :id")
    Category find(int id);

    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);
}
