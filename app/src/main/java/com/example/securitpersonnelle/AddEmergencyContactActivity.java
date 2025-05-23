package com.example.securitpersonnelle;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEmergencyContactActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText phoneEditText;
    private Button saveButton;
    private AlertDatabase alertDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        nameEditText = findViewById(R.id.edit_name);
        phoneEditText = findViewById(R.id.edit_phone);
        saveButton = findViewById(R.id.btn_save);

        alertDatabase = AlertDatabase.getDatabase(this);

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String phone = phoneEditText.getText().toString();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            } else {
                EmergencyContact contact = new EmergencyContact(name, phone);
                new Thread(() -> {
                    alertDatabase.emergencyContactDao().insert(contact);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Contact ajouté", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }).start();
            }
        });
    }
}
