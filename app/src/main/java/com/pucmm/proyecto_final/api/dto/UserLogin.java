package com.pucmm.proyecto_final.api.dto;

import androidx.annotation.StringRes;

import com.google.gson.annotations.SerializedName;

public class UserLogin {
    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;

    public UserLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
