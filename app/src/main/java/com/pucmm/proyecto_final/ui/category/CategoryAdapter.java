package com.pucmm.proyecto_final.ui.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucmm.proyecto_final.api.dto.User;
import com.pucmm.proyecto_final.databinding.CategoryItemBinding;
import com.pucmm.proyecto_final.models.Category;
import com.pucmm.proyecto_final.room_view_model.listener.OnItemTouchListener;
import com.pucmm.proyecto_final.room_view_model.listener.OptionsMenuListener;
import com.pucmm.proyecto_final.utils.CommonUtil;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>{

    private Context context;
    private List<Category> categories;
    private User user;
    private CategoryItemBinding mBinding;
    private OptionsMenuListener optionsMenuListener;
    private OnItemTouchListener onItemTouchListener;

    public CategoryAdapter(Context context, User user) {
        this.context = context;
        this.user = user;
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

        if(!user.getRol().equals(User.ROL.SELLER)) {
            holder.action.setVisibility(View.INVISIBLE);
        }

        holder.action.setOnClickListener(v -> {
            if (optionsMenuListener != null) {
                optionsMenuListener.onCreateOptionsMenu(holder.action, category);
            }
        });

        holder.photo.setOnClickListener(v -> {
            if (onItemTouchListener != null) {
                onItemTouchListener.onClick(category);
            }
        });

        holder.name.setOnClickListener(v -> {
            if (onItemTouchListener != null) {
                onItemTouchListener.onClick(category);
            }
        });


        CommonUtil.downloadImage(category.getImage(), mBinding.image);
    }

    public void setOptionsMenuListener(OptionsMenuListener optionsMenuListener) {
        this.optionsMenuListener = optionsMenuListener;
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.onItemTouchListener = onItemTouchListener;
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
        private ImageView photo, action;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = mBinding.image;
            name = mBinding.name;
            action = mBinding.manager;
        }
    }
}
