package com.example.securitpersonnelle;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AlertDao {

    @Insert
    void insert(Alert alert);

    @Query("SELECT * FROM alerts")
    List<Alert> getAllAlerts();


    @Delete
    void delete(Alert alert);
}
