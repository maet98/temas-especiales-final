package com.pucmm.proyecto_final.data;

import android.annotation.SuppressLint;

import com.pucmm.proyecto_final.models.Product;

public class ProductCart extends Product {
    private int qty;
    private String photo;

    public ProductCart(Product product, int qty, String photo) {
        super(product.getId(), product.getName(), product.getDescription(), product.getCategory(), product.getPrice(), product.isActive());
        this.qty = qty;
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @SuppressLint("DefaultLocale")
    public String getNotification() {
        return String.format("Added %d x %s added to your cart",this.qty, this.getName());
    }
}
