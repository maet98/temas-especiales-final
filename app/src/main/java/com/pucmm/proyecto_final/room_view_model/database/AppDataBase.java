package com.pucmm.proyecto_final.room_view_model.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.pucmm.proyecto_final.room_view_model.model.Category;
import com.pucmm.proyecto_final.room_view_model.model.Product;
import com.pucmm.proyecto_final.room_view_model.model.User;

import java.util.ConcurrentModificationException;

@Database(entities = {User.class, Category.class, Product.class}, version = 4)
public abstract class AppDataBase extends RoomDatabase {
    private static final String DATABASE_NAME = "ecommerce";
    private static final Object LOCK = new Object();
    private static AppDataBase sIntance;

    public static AppDataBase getInstance(Context context) {
        if (sIntance == null) {
            synchronized (LOCK) {
                sIntance = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
            }
        }
        return sIntance;
    }

    public abstract UserDAO userDAO();
    public abstract ProductDao productDao();
    public abstract CategoryDao categoryDao();
}
