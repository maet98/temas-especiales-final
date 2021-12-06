package com.pucmm.proyecto_final.room_view_model.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pucmm.proyecto_final.room_view_model.database.CategoryDao;
import com.pucmm.proyecto_final.models.Category;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private LiveData<List<Category>> CategoryListLiveData;

    public CategoryViewModel(@NonNull CategoryDao categoryDao) {
        this.CategoryListLiveData = categoryDao.findAll();
    }

    public LiveData<List<Category>> getCategoryListLiveData() {
        return CategoryListLiveData;
    }
}
