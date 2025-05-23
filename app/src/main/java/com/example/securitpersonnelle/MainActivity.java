package com.example.securitpersonnelle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private static final int CAMERA_REQUEST_CODE = 2001;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private Button sosButton, photoButton, addContactButton;
    private TextView locationText;
    private ImageView photoPreview;
    private RecyclerView emergencyContactsRecyclerView;
    private EmergencyContactAdapter emergencyContactAdapter;
    private AlertDatabase alertDatabase;

    private Uri photoUri;
    private File photoFile;

    // Capteurs pour détection mouvement
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private static final float MOVEMENT_THRESHOLD = 15.0f;

    // Gestion cooldown pour éviter alertes SOS trop fréquentes
    private long lastAlertTime = 0;
    private static final long ALERT_COOLDOWN_MS = 10000; // 10 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sosButton = findViewById(R.id.btn_sos);
        locationText = findViewById(R.id.txt_location);
        photoButton = findViewById(R.id.btn_photo);
        photoPreview = findViewById(R.id.photo_preview);
        addContactButton = findViewById(R.id.btn_add_contact);
        emergencyContactsRecyclerView = findViewById(R.id.recycler_view_contacts);

        alertDatabase = AlertDatabase.getDatabase(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Setup RecyclerView
        emergencyContactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        emergencyContactAdapter = new EmergencyContactAdapter(new ArrayList<>());
        emergencyContactsRecyclerView.setAdapter(emergencyContactAdapter);

        // Charger la liste au démarrage
        loadEmergencyContacts();

        addContactButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEmergencyContactActivity.class);
            startActivity(intent);
        });

        sosButton.setOnClickListener(v -> triggerSOSAlert());

        photoButton.setOnClickListener(v -> takePhoto());

        // Demande permission localisation si nécessaire
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            getLocationUpdates();
        }

        Button historyButton = findViewById(R.id.btn_history);
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AlertHistoryActivity.class);
            startActivity(intent);
        });

        // Initialiser capteur acceleromètre pour détection mouvement
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEmergencyContacts();
        // S'inscrire au capteur acceleromètre
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Désinscrire le capteur pour économiser batterie
        sensorManager.unregisterListener(this);
    }

    private void getLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission localisation non accordée.", Toast.LENGTH_SHORT).show();
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    locationText.setText("Aucune localisation disponible.");
                    return;
                }

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    locationText.setText("📍 Latitude : " + lat + "\n📍 Longitude : " + lon);
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".fileprovider",
                        photoFile
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "photo_" + timeStamp;

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && !storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Toast.makeText(this, "Impossible de créer le dossier images", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        try {
            return File.createTempFile(fileName, ".jpg", storageDir);
        } catch (IOException e) {
            Toast.makeText(this, "Erreur création fichier image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void loadEmergencyContacts() {
        new Thread(() -> {
            List<EmergencyContact> contacts = alertDatabase.emergencyContactDao().getAllContacts();
            Log.d("MainActivity", "Nombre de contacts récupérés : " + contacts.size());
            runOnUiThread(() -> emergencyContactAdapter.setContacts(contacts));
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (photoUri != null) {
                photoPreview.setImageURI(photoUri);
            } else {
                Toast.makeText(this, "Erreur lors de l'affichage de la photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Méthodes du SensorEventListener
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

            if (acceleration > MOVEMENT_THRESHOLD) {
                long now = System.currentTimeMillis();
                if (now - lastAlertTime > ALERT_COOLDOWN_MS) {
                    lastAlertTime = now;
                    Toast.makeText(this, "Mouvement brusque détecté ! Alerte SOS déclenchée.", Toast.LENGTH_SHORT).show();
                    triggerSOSAlert();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void triggerSOSAlert() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lon = location.getLongitude();
                            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                            String uriStr = (photoUri != null) ? photoUri.toString() : null;

                            Alert alert = new Alert(lat, lon, currentDate, uriStr);
                            new Thread(() -> alertDatabase.alertDao().insert(alert)).start();

                            Toast.makeText(MainActivity.this, "🚨 Alerte SOS sauvegardée !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Localisation indisponible.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Permission de localisation non accordée.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
