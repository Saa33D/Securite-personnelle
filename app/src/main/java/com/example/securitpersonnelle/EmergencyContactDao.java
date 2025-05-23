package com.example.securitpersonnelle;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EmergencyContactDao {

    @Insert
    void insert(EmergencyContact contact);

    @Update
    void update(EmergencyContact contact);

    @Delete
    void delete(EmergencyContact contact);

    @Query("SELECT * FROM emergency_contacts")
    List<EmergencyContact> getAllContacts();

}
