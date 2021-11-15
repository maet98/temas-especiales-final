package com.pucmm.proyecto_final.room_view_model.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "product")
public class Product {

    @PrimaryKey
    @NonNull
    private String id;
    private String description;
//    private String image;
    private Double price;

    public Product(String description, Double price) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.price = price;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
