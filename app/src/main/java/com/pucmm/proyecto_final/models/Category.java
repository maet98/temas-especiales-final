package com.pucmm.proyecto_final.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "category")
public class Category implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String image;
    private Boolean active;

    public Category() {
    }

    public Category(int id, String name, String photo) {
        this.id = id;
        this.name = name;
        this.image = photo;
    }

    @Ignore
    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
