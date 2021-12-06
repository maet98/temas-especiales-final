package com.pucmm.proyecto_final.models.relationships;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.pucmm.proyecto_final.models.Category;
import com.pucmm.proyecto_final.models.Product;

import java.io.Serializable;

public class CategoryProducts implements Serializable {
    @Embedded
    public Category category;
    @Relation(
            parentColumn = "uid",
            entityColumn = "category"
    )
    public Product product;
}
