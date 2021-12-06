package com.pucmm.proyecto_final.room_view_model.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.pucmm.proyecto_final.models.Carousel;
import com.pucmm.proyecto_final.models.Category;
import com.pucmm.proyecto_final.models.Product;
import com.pucmm.proyecto_final.models.User;

@Database(entities = {Category.class, Product.class, Carousel.class}, version = 2)
public abstract class AppDataBase extends RoomDatabase {
    private static final String DATABASE_NAME = "ecommerce";
    private static final Object LOCK = new Object();
    private static AppDataBase sIntance;

    public static AppDataBase getInstance(Context context) {
        if (sIntance == null) {
            synchronized (LOCK) {
                sIntance = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class,
                        DATABASE_NAME).
                        fallbackToDestructiveMigration().
                        build();
            }
        }
        return sIntance;
    }

    public abstract ProductDao productDao();
    public abstract CategoryDao categoryDao();
}
