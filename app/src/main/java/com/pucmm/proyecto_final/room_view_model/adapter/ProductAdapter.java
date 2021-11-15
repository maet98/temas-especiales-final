package com.pucmm.proyecto_final.room_view_model.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pucmm.proyecto_final.databinding.ActivityProductFormBinding;
import com.pucmm.proyecto_final.databinding.ProductItemBinding;
import com.pucmm.proyecto_final.room_view_model.model.Product;
import com.pucmm.proyecto_final.ui.ProductForm;

import java.util.List;
import java.util.UUID;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private Context context;
    private List<Product> products;
    private ProductItemBinding mBinding;

    public ProductAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        mBinding = ProductItemBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Product product = products.get(position);
        holder.id.setText(product.getId().toString());
        holder.price.setText(product.getPrice().toString());
        holder.description.setText(product.getDescription().toString());
        Glide.with(context).load("https://wallsdesk.com/wp-content/uploads/2017/01/Mouse-HD.jpg").into(holder.image);
    }

    @Override
    public int getItemCount() {
        if(products == null) {
            return 0;
        }
        return products.size();
    }

    public Product getProduct(int position) {
        return products.get(position);
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView id, price, description;
        private ImageView editBtn;
        private ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = mBinding.productId;
            price = mBinding.productPrice;
            description = mBinding.productDescription;
            image = mBinding.image;

            editBtn = mBinding.editImage;
            editBtn.setOnClickListener(v -> {
                String id = products.get(getAdapterPosition()).getId();

                Intent intent = new Intent(context, ProductForm.class);
                intent.putExtra("product_id", id);
                context.startActivity(intent);
            });
        }
    }
}
