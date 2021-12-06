package com.pucmm.proyecto_final.data;

import com.pucmm.proyecto_final.models.Product;

public class ProductCart extends Product {
    private int qty;

    public ProductCart(Product product, int qty) {
        super(product.getId(), product.getName(), product.getDescription(), product.getCategory(), product.getPrice(), product.isActive());
        this.qty = qty;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
