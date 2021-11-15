package com.pucmm.proyecto_final.room_view_model.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pucmm.proyecto_final.databinding.ActivityMainCategoryBinding;
import com.pucmm.proyecto_final.room_view_model.adapter.CategoryAdapter;
import com.pucmm.proyecto_final.room_view_model.database.AppDataBase;
import com.pucmm.proyecto_final.room_view_model.database.AppExecutors;
import com.pucmm.proyecto_final.room_view_model.database.CategoryDao;
import com.pucmm.proyecto_final.room_view_model.model.Category;
import com.pucmm.proyecto_final.room_view_model.viewmodel.CategoryViewModel;
import com.pucmm.proyecto_final.room_view_model.viewmodel.CategoryViewModelFactory;
import com.pucmm.proyecto_final.ui.ProductForm;

import java.util.List;

public class MainCategory extends AppCompatActivity {


    private ActivityMainCategoryBinding mBinding;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;
    private CategoryAdapter mCategoryAdapter;
    private CategoryDao categoryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainCategoryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        categoryDao = AppDataBase.getInstance(getApplicationContext()).categoryDao();
        mFloatingActionButton = mBinding.addFAB;
        mRecyclerView = mBinding.recyclerView;
        mFloatingActionButton.setOnClickListener(v -> startActivity(new Intent(MainCategory.this, CategoryForm.class)));

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        mCategoryAdapter = new CategoryAdapter(this);
        mRecyclerView.setAdapter(mCategoryAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    final int position = viewHolder.getAdapterPosition();
                    final Category category = mCategoryAdapter.getCategory(position);
                    categoryDao.delete(category);
                });

            }
        });

        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        retrieveProducts();
    }

    private void retrieveProducts() {
        CategoryViewModel categoryViewModel = new ViewModelProvider(this, new CategoryViewModelFactory(categoryDao))
                .get(CategoryViewModel.class);

        categoryViewModel.getCategoryListLiveData().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                mCategoryAdapter.setCategories(categories);
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