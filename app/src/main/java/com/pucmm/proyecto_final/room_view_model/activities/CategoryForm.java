package com.pucmm.proyecto_final.room_view_model.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.pucmm.proyecto_final.databinding.ActivityCategoryFormBinding;
import com.pucmm.proyecto_final.room_view_model.database.AppDataBase;
import com.pucmm.proyecto_final.room_view_model.database.AppExecutors;
import com.pucmm.proyecto_final.room_view_model.database.CategoryDao;
import com.pucmm.proyecto_final.room_view_model.model.Category;

public class CategoryForm extends AppCompatActivity {

    private ActivityCategoryFormBinding mBinding;
    private EditText name;
    private Button save;
    private int categoryId;
    private Intent intent;
    private CategoryDao categoryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCategoryFormBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        categoryDao = AppDataBase.getInstance(getApplicationContext()).categoryDao();
        intent = getIntent();

        categoryId = intent.getIntExtra("category_id", -1);

        initViews();

        if (categoryId != -1) {
            save.setText("UPDATE");
            AppExecutors.getInstance().diskIO().execute(() -> {
                Category category = categoryDao.find(categoryId);
                populateUI(category);
            });
        }
    }

    private void populateUI(Category category) {
        if(category != null){
            name.setText(category.getName());
        }
    }

    private void initViews() {
        name = mBinding.categoryName;

        save = mBinding.saveCategory;
        save.setOnClickListener(v -> onSaveButtonClicked());
    }

    private void onSaveButtonClicked() {
        Category category = new Category(name.getText().toString());

        AppExecutors.getInstance().diskIO().execute(() -> {
            if (intent.hasExtra("category_id")) {
                category.setId(categoryId);
                categoryDao.update(category);
            } else {
                categoryDao.insert(category);
            }
            finish();
        });
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