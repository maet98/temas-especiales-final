package com.pucmm.proyecto_final.room_view_model.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pucmm.proyecto_final.room_view_model.database.ProductDao;

public class ProductViewModelFactory implements ViewModelProvider.Factory {

    private ProductDao productDao;

    public ProductViewModelFactory(ProductDao productDao) {
        this.productDao = productDao;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(ProductViewModel.class)) {
            return (T) new ProductViewModel(productDao);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel Class");
        }
    }
}
