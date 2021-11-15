package com.pucmm.proyecto_final.room_view_model.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(index = true)
    private String email;
    private String username;
    private String contact;
    private String name;
    private String password;

    public User(int id, String email, String username, String contact, String name, String password) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.contact = contact;
        this.name = name;
        this.password = password;
    }

    @Ignore
    public User(String email, String username, String contact, String name, String password) {
        this.email = email;
        this.username = username;
        this.contact = contact;
        this.name = name;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
