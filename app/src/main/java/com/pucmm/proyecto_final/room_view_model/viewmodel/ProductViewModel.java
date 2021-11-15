package com.pucmm.proyecto_final.room_view_model.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pucmm.proyecto_final.room_view_model.database.ProductDao;
import com.pucmm.proyecto_final.room_view_model.model.Product;

import java.util.List;

public class ProductViewModel extends ViewModel {
    private LiveData<List<Product>> productListLiveData;

    public ProductViewModel(@NonNull ProductDao productDao) {
        productListLiveData = productDao.findAll();
    }

    public LiveData<List<Product>> getProductListLiveData() {
        return productListLiveData;
    }
}
