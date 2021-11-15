package com.pucmm.proyecto_final.room_view_model.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.databinding.ActivityMainProductBinding;
import com.pucmm.proyecto_final.room_view_model.adapter.ProductAdapter;
import com.pucmm.proyecto_final.room_view_model.database.AppDataBase;
import com.pucmm.proyecto_final.room_view_model.database.AppExecutors;
import com.pucmm.proyecto_final.room_view_model.database.ProductDao;
import com.pucmm.proyecto_final.room_view_model.model.Product;
import com.pucmm.proyecto_final.room_view_model.viewmodel.ProductViewModel;
import com.pucmm.proyecto_final.room_view_model.viewmodel.ProductViewModelFactory;
import com.pucmm.proyecto_final.ui.ProductForm;

import java.util.List;

public class MainProduct extends AppCompatActivity {

    private ActivityMainProductBinding mBinding;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;
    private ProductAdapter mProductAdapter;
    private ProductDao productDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainProductBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        productDao = AppDataBase.getInstance(getApplicationContext()).productDao();

        mFloatingActionButton = mBinding.addFAB;
//        mFloatingActionButton.setOnClickListener(v -> startActivity(new Intent(MainProduct.this, ProductForm.class)));

        mRecyclerView = mBinding.recyclerView;

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProductAdapter = new ProductAdapter(this);
        mRecyclerView.setAdapter(mProductAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    final int position = viewHolder.getAdapterPosition();
                    final Product product = mProductAdapter.getProduct(position);
                    productDao.delete(product);
                });

            }
        });

        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        retrieveProducts();
    }

    private void retrieveProducts() {
        ProductViewModel personViewModel = new ViewModelProvider(this, new ProductViewModelFactory(productDao))
                .get(ProductViewModel.class);

        personViewModel.getProductListLiveData().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                mProductAdapter.setProducts(products);
            }
        });
    }

    public void changeToProduct(View view) {
        System.out.println("product form");
        startActivity(new Intent(getApplicationContext(), ProductForm.class));
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}