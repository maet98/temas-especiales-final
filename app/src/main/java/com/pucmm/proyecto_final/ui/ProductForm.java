package com.pucmm.proyecto_final.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.databinding.ActivityProductFormBinding;
import com.pucmm.proyecto_final.room_view_model.database.AppDataBase;
import com.pucmm.proyecto_final.room_view_model.database.AppExecutors;
import com.pucmm.proyecto_final.room_view_model.database.ProductDao;
import com.pucmm.proyecto_final.room_view_model.model.Product;
import com.pucmm.proyecto_final.room_view_model.viewmodel.ProductViewModel;

import java.util.UUID;

public class ProductForm extends AppCompatActivity {

    private ActivityProductFormBinding mBinding;

    private EditText price, description;
    private Button save;
    private String productId;
    private Intent intent;
    private ProductDao productDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityProductFormBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        productDao = AppDataBase.getInstance(getApplicationContext()).productDao();
        intent = getIntent();

        productId = intent.getStringExtra("product_id");

        initViews();

        if (productId != null) {
            save.setText("UPDATE");
            AppExecutors.getInstance().diskIO().execute(() -> {
                Product product = productDao.find(productId);
                populateUI(product);
            });
        }
    }


    private void populateUI(Product product) {
        if(product != null){
            mBinding.priceProduct.setText(product.getPrice().toString());
            mBinding.productDescription.setText(product.getDescription());
        }
    }

    public void onClickBtn(View v)
    {
        onSaveButtonClicked();
    }

    private void initViews() {
        description = mBinding.productDescription;
        price = mBinding.priceProduct;

        save = mBinding.SaveProduct;
        save.setClickable(true);
        save.setOnClickListener(v -> onSaveButtonClicked());
    }

    public void onSaveButtonClicked() {
        Product product = new Product(description.getText().toString(), Double.valueOf(price.getText().toString()));

        System.out.println(intent.hasExtra("product_id"));
        System.out.println("hi");
        AppExecutors.getInstance().diskIO().execute(() -> {
            if (intent.hasExtra("product_id")) {
                product.setId(productId);
                productDao.update(product);
            } else {
                System.out.println("insert product");
                productDao.insert(product);
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