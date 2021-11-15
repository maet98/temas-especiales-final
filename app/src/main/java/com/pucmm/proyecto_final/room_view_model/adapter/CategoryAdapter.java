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

import com.pucmm.proyecto_final.databinding.CategoryItemBinding;
import com.pucmm.proyecto_final.room_view_model.activities.CategoryForm;
import com.pucmm.proyecto_final.room_view_model.model.Category;
import com.pucmm.proyecto_final.ui.ProductForm;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>{

    private Context context;
    private List<Category> categories;
    private CategoryItemBinding mBinding;

    public CategoryAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        mBinding = CategoryItemBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder holder, int position) {
        final Category category = categories.get(position);
        holder.name.setText(category.getName().toString());
    }

    @Override
    public int getItemCount() {
        if(categories == null) {
            return 0;
        }
        return categories.size();
    }

    public Category getCategory(int position) {
        return categories.get(position);
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView photo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = mBinding.name;
            photo = mBinding.image;

            photo.setOnClickListener(v -> {
                int id = categories.get(getAdapterPosition()).getId();

                Intent intent = new Intent(context, CategoryForm.class);
                intent.putExtra("category_id", id);
                context.startActivity(intent);
            });
        }
    }
}
