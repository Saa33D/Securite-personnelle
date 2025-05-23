package com.example.securitpersonnelle;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "emergency_contacts")
public class EmergencyContact {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String phoneNumber;

    public EmergencyContact() {}

    public EmergencyContact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
