package com.pucmm.proyecto_final.ui.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucmm.proyecto_final.api.dto.User;
import com.pucmm.proyecto_final.databinding.ProductItemBinding;
import com.pucmm.proyecto_final.models.Carousel;
import com.pucmm.proyecto_final.models.relationships.ProductWithCarousel;
import com.pucmm.proyecto_final.room_view_model.listener.OnItemTouchListener;
import com.pucmm.proyecto_final.room_view_model.listener.OptionsMenuListener;
import com.pucmm.proyecto_final.utils.CommonUtil;
import com.pucmm.proyecto_final.utils.FormattedUtil;
import com.pucmm.proyecto_final.utils.ProductUtils;

import java.util.List;
import java.util.Optional;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private User user;
    private Context context;
    private List<ProductWithCarousel> products;
    private ProductItemBinding binding;
    private OptionsMenuListener optionsMenuListener;
    private OnItemTouchListener onItemTouchListener;

    public ProductAdapter(User user) {
        this.user = user;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        binding = ProductItemBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ProductWithCarousel element = products.get(position);
        holder.itemCode.setText(ProductUtils.completeLeft(element.product.getId()));
        holder.itemName.setText(element.product.getName());
        holder.price.setText(FormattedUtil.getDecimalValue(element.product.getPrice()));


        if (!user.getRol().equals(User.ROL.SELLER)) {
            holder.action.setVisibility(View.INVISIBLE);
        }

        holder.action.setOnClickListener(v -> {
            if (optionsMenuListener != null) {
                optionsMenuListener.onCreateOptionsMenu(holder.action, element);
            }
        });

        holder.avatar.setOnClickListener(v -> {
            if (onItemTouchListener != null) {
                onItemTouchListener.onClick(element);
            }
        });

        holder.itemCode.setOnClickListener(v -> {
            if (onItemTouchListener != null) {
                onItemTouchListener.onClick(element);
            }
        });

        holder.itemName.setOnClickListener(v -> {
            if (onItemTouchListener != null) {
                onItemTouchListener.onClick(element);
            }
        });

        holder.price.setOnClickListener(v -> {
            if (onItemTouchListener != null) {
                onItemTouchListener.onClick(element);
            }
        });


        if (element.carousels != null && !element.carousels.isEmpty()) {
            Optional<Carousel> optional = element.carousels.stream()
                    .sorted((a1, a2) -> Integer.valueOf(a1.getLineNum()).compareTo(a2.getLineNum()))
                    .findFirst();

            if (optional.isPresent()) {
                CommonUtil.downloadImage(optional.get().getPhoto(), binding.avatar);
            }
        }
    }

    @Override
    public int getItemCount() {
        if(products == null) {
            return 0;
        }
        return products.size();
    }

    public ProductWithCarousel getProduct(int position) {
        return products.get(position);
    }

    public void setProducts(List<ProductWithCarousel> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public void setOptionsMenuListener(OptionsMenuListener optionsMenuListener) {
        this.optionsMenuListener = optionsMenuListener;
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.onItemTouchListener = onItemTouchListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView itemCode, itemName, price;
        private ImageView avatar, action;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCode = binding.itemCode;
            itemName = binding.itemName;
            price = binding.price;
            avatar = binding.avatar;
            action = binding.manager;
        }
    }
}
