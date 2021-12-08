package com.pucmm.proyecto_final.ui.cart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pucmm.proyecto_final.data.ProductCart;
import com.pucmm.proyecto_final.databinding.FragmentCartBinding;
import com.pucmm.proyecto_final.utils.Session;

import java.util.ArrayList;

public class CartFragment extends Fragment {


    private Session session;
    private ArrayList<ProductCart> products;
    private FragmentCartBinding binding;

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        session = new Session(getContext());
        products = session.getCart();
        String[] mainTitle = new String[products.size()];
        float subtotal = 0;
        int total = 0;
        for(int i = 0;i < products.size();i++) {
            mainTitle[i] = products.get(i).getName();
            subtotal += products.get(i).getQty() * products.get(i).getPrice();
            total += products.get(i).getQty();
        }
        binding.total.setText(String.format("Sub total( %d items): %.2f $",total, subtotal));
        ProductCartAdapter adapter = new ProductCartAdapter(getActivity(), products, mainTitle, session, binding.total);
        binding.productList.setAdapter(adapter);

        return root;
    }
}