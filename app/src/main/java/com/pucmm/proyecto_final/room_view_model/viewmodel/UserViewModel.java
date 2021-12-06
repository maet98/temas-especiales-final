package com.pucmm.proyecto_final.room_view_model.viewmodel;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pucmm.proyecto_final.room_view_model.database.AppDataBase;
import com.pucmm.proyecto_final.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserViewModel extends ViewModel {
    private LiveData<List<User>> userListLiveData;

    public UserViewModel(@NotNull AppDataBase dataBase) {
    }

    public LiveData<List<User>> getUserListLiveData() {
        return userListLiveData;
    }




    private static class InsertUserAsync extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... users) {
            return null;
        }
    }
}
