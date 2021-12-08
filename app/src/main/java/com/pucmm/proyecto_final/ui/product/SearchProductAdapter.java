package com.pucmm.proyecto_final.ui.product;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.api.dto.User;
import com.pucmm.proyecto_final.data.ProductCart;
import com.pucmm.proyecto_final.databinding.ProductItemBinding;
import com.pucmm.proyecto_final.databinding.ProductItemCartBinding;
import com.pucmm.proyecto_final.models.Carousel;
import com.pucmm.proyecto_final.models.relationships.ProductWithCarousel;
import com.pucmm.proyecto_final.room_view_model.listener.OnItemTouchListener;
import com.pucmm.proyecto_final.room_view_model.listener.OptionsMenuListener;
import com.pucmm.proyecto_final.utils.CommonUtil;
import com.pucmm.proyecto_final.utils.FormattedUtil;
import com.pucmm.proyecto_final.utils.ProductUtils;
import com.pucmm.proyecto_final.utils.Session;

import java.util.ArrayList;
import java.util.Optional;

public class SearchProductAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<ProductWithCarousel> products;
    private User user;
    private String search;

    private OptionsMenuListener optionsMenuListener;
    private OnItemTouchListener onItemTouchListener;

    public SearchProductAdapter(Activity context, ArrayList<ProductWithCarousel> products,
                                String[] productNames, User user, OptionsMenuListener optionsMenuListener,
                                OnItemTouchListener onItemTouchListener) {
        super(context, R.layout.product_item, productNames);
        this.search = "";
        this.user = user;
        this.context=context;
        this.products = products;
        this.onItemTouchListener = onItemTouchListener;
        this.optionsMenuListener = optionsMenuListener;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        ProductItemBinding binding = ProductItemBinding.inflate(inflater, parent, false);

        final ProductWithCarousel element = products.get(position);
        if(element.product.getName().contains(search)) {
            binding.itemCode.setText(ProductUtils.completeLeft(element.product.getId()));
            binding.itemName.setText(element.product.getName());
            binding.price.setText(FormattedUtil.getDecimalValue(element.product.getPrice()));

            if (!user.getRol().equals(User.ROL.SELLER)) {
                binding.manager.setVisibility(View.INVISIBLE);
            }

            binding.manager.setOnClickListener(v -> {
                if (optionsMenuListener != null) {
                    optionsMenuListener.onCreateOptionsMenu(binding.avatar, element);
                }
            });

            binding.avatar.setOnClickListener(v -> {
                if (onItemTouchListener != null) {
                    onItemTouchListener.onClick(element);
                }
            });

            binding.itemCode.setOnClickListener(v -> {
                if (onItemTouchListener != null) {
                    onItemTouchListener.onClick(element);
                }
            });

            binding.itemName.setOnClickListener(v -> {
                if (onItemTouchListener != null) {
                    onItemTouchListener.onClick(element);
                }
            });

            binding.price.setOnClickListener(v -> {
                if (onItemTouchListener != null) {
                    onItemTouchListener.onClick(element);
                }
            });

            if (element.carousels != null && !element.carousels.isEmpty()) {
                CommonUtil.downloadImage(element.carousels.get(0).getPhoto(), binding.avatar);
            }

            return binding.getRoot();

        } else {
            return new View(context);
        }

    };

    public void setSearch(String search) {
        this.search = search;
        notifyDataSetChanged();
    }

    public void setProducts(ArrayList<ProductWithCarousel> products) {
        this.products = products;
    }
}
