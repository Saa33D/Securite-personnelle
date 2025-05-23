package com.example.securitpersonnelle;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securitpersonnelle.Alert;
import com.example.securitpersonnelle.AlertAdapter;
import com.example.securitpersonnelle.AlertDatabase;
import com.example.securitpersonnelle.R;

import java.util.List;

public class AlertHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlertAdapter adapter;
    private AlertDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recycler_alerts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = AlertDatabase.getDatabase(this);

        new Thread(() -> {
            List<Alert> alerts = db.alertDao().getAllAlerts();
            runOnUiThread(() -> adapter = new AlertAdapter(this, alerts));
            runOnUiThread(() -> recyclerView.setAdapter(adapter));
        }).start();
    }
}
