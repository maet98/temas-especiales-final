package com.pucmm.proyecto_final.room_view_model.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.pucmm.proyecto_final.models.Carousel;
import com.pucmm.proyecto_final.models.Product;
import com.pucmm.proyecto_final.models.relationships.ProductWithCarousel;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM product ORDER BY id")
    LiveData<List<ProductWithCarousel>> findAll();

    @Query("select * from product where id = :id")
    Product find(String id);

    @Transaction
    @Insert()
    long insert(Product product);

    @Query("DELETE FROM carousel WHERE product = :uid")
    void deleteCarousels(int uid);

    @Insert
    void insertCarousels(List<Carousel> carousels);

    @Transaction
    @Update
    void update(Product product);

    @Update
    void updateCarousels(List<Carousel> carousels);

    @Transaction
    @Delete
    void delete(Product product);

    @Delete
    void deleteCarousels(List<Carousel> carousels);
}
