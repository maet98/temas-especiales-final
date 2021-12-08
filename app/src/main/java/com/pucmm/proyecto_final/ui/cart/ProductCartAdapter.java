package com.pucmm.proyecto_final.ui.cart;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.data.ProductCart;
import com.pucmm.proyecto_final.databinding.ProductItemCartBinding;
import com.pucmm.proyecto_final.utils.CommonUtil;
import com.pucmm.proyecto_final.utils.FormattedUtil;
import com.pucmm.proyecto_final.utils.Session;

import java.util.ArrayList;

public class ProductCartAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<ProductCart> products;
    private ProductItemCartBinding binding;
    private Session session;
    private TextView totalTV;
    private int total;
    private float subtotal;

    public ProductCartAdapter(Activity context, ArrayList<ProductCart> products, String[] mainTitles, Session session, TextView totalTV) {
        super(context, R.layout.product_item_cart, mainTitles);

        this.session = session;
        this.context=context;
        this.products = products;
        this.totalTV = totalTV;

        subtotal = 0;
        total = 0;
        for(int i = 0;i < products.size();i++) {
            subtotal += products.get(i).getQty() * products.get(i).getPrice();
            total += products.get(i).getQty();
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        binding = ProductItemCartBinding.inflate(inflater, parent, false);

        binding.nameProduct.setText(products.get(position).getName());
        binding.qty.setText(String.valueOf(products.get(position).getQty()));
        binding.priceProduct.setText(FormattedUtil.getDecimalValue(products.get(position).getPrice()));
        System.out.println(products.get(position).getPrice());
        if(products.get(position).getPhoto() != null) {
            CommonUtil.downloadImage(products.get(position).getPhoto(), binding.avatarProduct);
        }

        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                products.get(position).setQty(products.get(position).getQty() + 1);
                binding.qty.setText(String.valueOf(products.get(position).getQty()));
                session.updateProduct(products.get(position), position);
                total++;
                subtotal += products.get(position).getPrice();
                totalTV.setText(String.format("Sub total( %d items): %.2f $",total, subtotal));
                notifyDataSetChanged();
            }
        });

        binding.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = products.get(position).getQty();
                if(qty > 1) {
                    products.get(position).setQty(qty - 1);
                    binding.qty.setText(String.valueOf(products.get(position).getQty()));
                    session.updateProduct(products.get(position), position);
                    total--;
                    subtotal -= products.get(position).getPrice();
                    totalTV.setText(String.format("Sub total( %d items): %.2f $",total, subtotal));
                    notifyDataSetChanged();
                }
            }
        });

        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                products.remove(position);
                Toast.makeText(context, String.format("%s deleted from cart", products.get(position).getName()), Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        });

        return binding.getRoot();

    };
}
