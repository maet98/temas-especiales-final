package com.pucmm.proyecto_final.models.relationships;


import androidx.room.Embedded;
import androidx.room.Relation;

import com.pucmm.proyecto_final.models.Carousel;
import com.pucmm.proyecto_final.models.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductWithCarousel implements Serializable {

    @Embedded
    public Product product;
    @Relation(
            parentColumn = "id",
            entityColumn = "product",
            entity = Carousel.class
    )
    public List<Carousel> carousels;

    public ProductWithCarousel() {
        this.product = new Product();
        this.carousels = new ArrayList<>();
    }

    public ProductWithCarousel(Product product, List<Carousel> carousels) {
        this.product = product;
        this.carousels = carousels;
    }
}
