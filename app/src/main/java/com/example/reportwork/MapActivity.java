package com.example.reportwork;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reportwork.BaseDatos.DBHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap eeMap;
    private DBHelper dbHelper;
    private int reporteId;
    private double latitud, longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        reporteId = getIntent().getIntExtra("REPORT_ID", -1);
        dbHelper = new DBHelper(this);

        // Obtener los detalles del reporte
        loadReportDetails();

        // Configurar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void loadReportDetails() {
        Cursor cursor = dbHelper.getReportById(reporteId);
        if (cursor.moveToFirst()) {
            latitud = cursor.getDouble(2);  // Columna de latitud
            longitud = cursor.getDouble(3); // Columna de longitud
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        eeMap = googleMap;

        // Mostrar la ubicación en el mapa
        LatLng reportLocation = new LatLng(latitud, longitud);
        eeMap.addMarker(new MarkerOptions().position(reportLocation).title("Ubicación del Reporte"));
        eeMap.moveCamera(CameraUpdateFactory.newLatLngZoom(reportLocation, 15));
    }
}
