package com.pucmm.proyecto_final.utils;

import android.net.Uri;

import com.pucmm.proyecto_final.models.Carousel;

public class CarouselUpload {

    public Uri uri;
    public Carousel carousel;

    public CarouselUpload(Uri uri, Carousel carousel) {
        this.uri = uri;
        this.carousel = carousel;
    }
}
