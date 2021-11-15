package com.pucmm.proyecto_final.room_view_model.viewmodel;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pucmm.proyecto_final.room_view_model.database.AppDataBase;
import com.pucmm.proyecto_final.room_view_model.database.UserDAO;
import com.pucmm.proyecto_final.room_view_model.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserViewModel extends ViewModel {
    private UserDAO userDAO;
    private LiveData<List<User>> userListLiveData;

    public UserViewModel(@NotNull AppDataBase dataBase) {
        userDAO = dataBase.userDAO();
        userListLiveData = dataBase.userDAO().findAll();
    }

    public LiveData<List<User>> getUserListLiveData() {
        return userListLiveData;
    }

    public void insert(User user) {
        new InsertUserAsync(userDAO).execute(user);
    }



    private static class InsertUserAsync extends AsyncTask<User, Void, Void> {
        private UserDAO userDAO;
        private InsertUserAsync(UserDAO userDAO) {
            this.userDAO = userDAO;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDAO.insert(users[0]);
            return null;
        }
    }
}
