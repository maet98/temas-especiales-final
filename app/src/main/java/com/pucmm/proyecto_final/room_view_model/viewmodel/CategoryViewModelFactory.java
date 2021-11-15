package com.pucmm.proyecto_final.room_view_model.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pucmm.proyecto_final.room_view_model.database.CategoryDao;
import com.pucmm.proyecto_final.room_view_model.database.ProductDao;

public class CategoryViewModelFactory implements ViewModelProvider.Factory{

    private CategoryDao categoryDao;

    public CategoryViewModelFactory(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(CategoryViewModel.class)) {
            return (T) new CategoryViewModel(categoryDao);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel Class");
        }
    }
}
