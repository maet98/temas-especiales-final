package com.pucmm.proyecto_final.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pucmm.proyecto_final.room_view_model.database.AppDataBase;
import com.pucmm.proyecto_final.room_view_model.viewmodel.CategoryViewModel;
import com.pucmm.proyecto_final.room_view_model.viewmodel.ProductViewModel;

import org.jetbrains.annotations.NotNull;


public class ViewModelFactory implements ViewModelProvider.Factory {

    private final AppDataBase dataBase;

    public ViewModelFactory(@NonNull Context context) {
        this.dataBase = AppDataBase.getInstance(context);

    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CategoryViewModel.class)) {
            return (T) new CategoryViewModel(dataBase.categoryDao());
        } else if (modelClass.isAssignableFrom(ProductViewModel.class)) {
            return (T) new ProductViewModel(dataBase.productDao());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel Class");
        }
    }
}
