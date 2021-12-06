package com.pucmm.proyecto_final.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pucmm.proyecto_final.api.dto.User;
import com.pucmm.proyecto_final.data.ProductCart;
import com.pucmm.proyecto_final.models.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Session {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private static final String USER = "user";
    private static final String PREF_NAME = "session";
    private static final String LOGGED_IN = "isLoggedIn";
    public static final String CARTS = "carts";
    private final Type productListType = new TypeToken<ArrayList<ProductCart>>(){}.getType();

    public Session(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        editor = sharedPreferences.edit();
    }

    public User getUserSesion() {
        String user_json = sharedPreferences.getString(USER, "{}");

        return new Gson().fromJson(user_json, User.class);
    }

    public void createLoginSession(User user) {
        editor.putBoolean(LOGGED_IN, true);
        editor.putString(USER, new Gson().toJson(user));
        editor.commit();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(LOGGED_IN, false);
    }

    public void logout() {
        editor.putBoolean(LOGGED_IN, false);
        editor.clear();
        editor.commit();
    }


    public void addCart(Product product, int qty) {
        ProductCart productCart = new ProductCart(product, qty);
        ArrayList<ProductCart> cart = getCart(sharedPreferences.getString(CARTS, "[]"));
        cart.add(productCart);
        editor.putString(CARTS, new Gson().toJson(cart));
        editor.commit();
    }

    private ArrayList<ProductCart> getCart(String jsonArrStr) {
        ArrayList<ProductCart> ans = new Gson().fromJson(jsonArrStr, productListType);
        return ans;
    }
}
