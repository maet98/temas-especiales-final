package com.pucmm.proyecto_final.room_view_model.listener;

import android.view.View;

public interface OptionsMenuListener<T> {
    void onCreateOptionsMenu(View view, T element);
}
