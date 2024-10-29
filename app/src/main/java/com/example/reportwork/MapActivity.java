package com.example.reportwork;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reportwork.BaseDatos.DBHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


//Importes nuevos===


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap eeMap;
    private DBHelper dbHelper;
    private int reporteId;
    private double latitud, longitud;
    private String descripcion;
    private byte[] imagenBytes;

    private TextView tvDescription;
    private ImageView imgReport;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        reporteId = getIntent().getIntExtra("REPORT_ID", -1);
        if (reporteId == -1) {
            Toast.makeText(this, "ID de reporte no válido", Toast.LENGTH_SHORT).show();
            finish(); // Termina la actividad si no hay un ID válido
            return; // Sale del método onCreate
        }

        dbHelper = new DBHelper(this);

        tvDescription = findViewById(R.id.tvDescription);
        imgReport = findViewById(R.id.imgReport);
        btnBack = findViewById(R.id.btnBack);

        // Obtener los detalles del reporte ===============================================
        loadReportDetails();


        // Configurar el mapa =====================================================================
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Botón "Atrás" ======================================================================
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadReportDetails() {
        Cursor cursor = dbHelper.getReportById(reporteId);
        if (cursor != null && cursor.moveToFirst()) {

                // Asegurarse de que las columnas existen en el cursor
                int COL_LATITUD = cursor.getColumnIndex(dbHelper.getColumnLatitud());
                int COL_LONGITUD = cursor.getColumnIndex(dbHelper.getColumnLongitud());
                int COL_DESCRIPCION = cursor.getColumnIndex(dbHelper.getColumnDescripcion());
                int COL_IMAGEN = cursor.getColumnIndex(dbHelper.getColumnImagen());

                // Verificación de las columnas
                if (COL_LATITUD != -1) latitud = cursor.getDouble(COL_LATITUD);
                if (COL_LONGITUD != -1) longitud = cursor.getDouble(COL_LONGITUD);
                if (COL_DESCRIPCION != -1) descripcion = cursor.getString(COL_DESCRIPCION);
                if (COL_IMAGEN != -1) imagenBytes = cursor.getBlob(COL_IMAGEN);

                // Verifica las coordenadas ================================================================
                if (latitud == 0.0 || longitud == 0.0) {
                    Toast.makeText(this, "Coordenadas no válidas obtenidas", Toast.LENGTH_SHORT).show();
                } else {
                    // Muestra las coordenadas obtenidas en el mapa
                    tvDescription.setText(descripcion);
                    if (imagenBytes != null) {
                        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
                        imgReport.setImageBitmap(imageBitmap);
                    }
                }

        } else {
            Toast.makeText(this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        eeMap = googleMap;

        // Verificar si las coordenadas son válidas ================================================================
        if (latitud != 0.0 && longitud != 0.0) {
            // Mostrar la ubicación en el mapa
            LatLng reportLocation = new LatLng(latitud, longitud);
            eeMap.addMarker(new MarkerOptions().position(reportLocation).title("Reporte"));
            eeMap.moveCamera(CameraUpdateFactory.newLatLngZoom(reportLocation, 15));
        } else {
            Toast.makeText(this, "Coordenadas no válidas", Toast.LENGTH_SHORT).show();
        }

    }
}
