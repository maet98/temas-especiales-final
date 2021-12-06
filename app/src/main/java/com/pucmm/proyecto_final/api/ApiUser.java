package com.pucmm.proyecto_final.api;

import com.pucmm.proyecto_final.api.dto.User;
import com.pucmm.proyecto_final.api.dto.UserLogin;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiUser {

    @GET
    Call<List<User>> getUsers();

    @POST("/users/login")
    Call<User> login(@Body UserLogin userLogin);

    @POST("/users")
    Call<User> createUser(@Body User user);

    @PUT("/users/change")
    Call<Void> changePassword(@Body UserLogin userLogin);

    @PUT("/users")
    Call<User> changeUser(@Body User user);
}
