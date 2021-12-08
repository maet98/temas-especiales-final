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
    public static final String NOTIFICATIONS = "notifications";
    private final Type productListType = new TypeToken<ArrayList<ProductCart>>(){}.getType();
    private final Type notificationListType = new TypeToken<ArrayList<String>>(){}.getType();

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

    public void updateUser(User user) {
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


    public void addCart(Product product, int qty, String photo) {
        ProductCart productCart = new ProductCart(product, qty, photo);
        ArrayList<ProductCart> cart = getCart();
        cart.add(productCart);
        addNotification(productCart.getNotification());
        editor.putString(CARTS, new Gson().toJson(cart));
        editor.commit();
    }

    public void updateProduct(ProductCart product, int position) {
        ArrayList<ProductCart> productCarts = getCart();
        productCarts.set(position, product);
        editor.putString(CARTS, new Gson().toJson(productCarts));
        editor.commit();
    }

    public ArrayList<ProductCart> getCart() {
        String jsonArrStr = sharedPreferences.getString(CARTS, "[]");
        ArrayList<ProductCart> ans = new Gson().fromJson(jsonArrStr, productListType);
        return ans;
    }

    public void deletedProduct(int position) {
        ArrayList<ProductCart> productCarts = getCart();
        productCarts.remove(position);
        editor.putString(CARTS, new Gson().toJson(productCarts));
        editor.commit();
    }


    public void addNotification(String notification) {
        ArrayList<String> notifications = getNotifications();
        notifications.add(notification);
        editor.putString(NOTIFICATIONS, new Gson().toJson(notifications));
        editor.commit();
    }


    public ArrayList<String> getNotifications() {
        String jsonArrStr = sharedPreferences.getString(NOTIFICATIONS, "[]");
        return new Gson().fromJson(jsonArrStr, notificationListType);
    }
}
