package com.example.reportwork;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.reportwork.BaseDatos.DBHelper;
import com.google.android.gms.location.FusedLocationProviderClient;  // Nuevo import
import com.google.android.gms.location.LocationServices;  // Nuevo import
import com.google.android.gms.tasks.OnSuccessListener;  // Nuevo import
import android.location.Location;  // Nuevo import



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReporteActivity extends AppCompatActivity {

    private static final int RECUEST_CAPTURA_DE_IMAGEN = 1;
    private static final int RECUEST_PERMISO_LOCALIZACION = 2;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

    private ImageView ima_imagen;
    private EditText txted_descripcion_repositorio;
    private Button btn_guardar_reporte;

    private Bitmap imagen_bitmap;
    private double latitud = 0.0, longitud = 0.0;
    private DBHelper dbHelper;
    private Uri photoURI;
    private String currentPhotoPath;

    // Instancia de FusedLocationProviderClient para obtener la ubicación del dispositivo
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);

        ima_imagen = findViewById(R.id.ima_imagen);
        txted_descripcion_repositorio = findViewById(R.id.txted_descripcion_repositorio);
        btn_guardar_reporte = findViewById(R.id.btn_guardar_reporte);

        dbHelper = new DBHelper(this);

        // Inicializar el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Botones =============================================================================================================================================================================
        findViewById(R.id.btn_tomar_foto).setOnClickListener(v -> tomar_foto()); //Tomar foto ===========
        findViewById(R.id.btn_guardar_reporte).setOnClickListener(v -> guardar_reporte()); //Guardar el reporte ========================
        findViewById(R.id.btnatras).setOnClickListener(v -> finish()); //Para cancelar el reporte. ========================

        // Solicitar permisos para la ubicación y camara ==========================================================
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_LOCATION_PERMISSION);
        } else {
            obtenerUbicacion();
        }
    }

    // Obtener la ubicación ============================================
    private void obtenerUbicacion() {
        // Verificar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitud = location.getLatitude();
                    longitud = location.getLongitude();
                } else {
                    Toast.makeText(ReporteActivity.this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Función para tomar la fotico =============================================================
    private void tomar_foto() {
        //Pedimos el permiso en tiempo real =================
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, RECUEST_CAPTURA_DE_IMAGEN);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = crearArchivoImagen();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Toast.makeText(this, "Error al crear el archivo de imagen", Toast.LENGTH_SHORT).show();
                }
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(this, "com.example.reportwork.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, RECUEST_CAPTURA_DE_IMAGEN);
                }
            }
        }
    }

    //Funcion para crear el archivo de imagen ======================================================
    private File crearArchivoImagen() throws IOException {
        String nombreArchivo = "reporte_" + System.currentTimeMillis();
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File archivoImagen = File.createTempFile(nombreArchivo, ".jpg", directorio);
        currentPhotoPath = archivoImagen.getAbsolutePath();
        return archivoImagen;
    }

    //Funcion para poder guardar el reporte ======================================================0
    private void guardar_reporte() {
        // Verificar si las coordenadas son válidas antes de guardar
        if (latitud == 0.0 || longitud == 0.0) {
            Toast.makeText(this, "Las coordenadas no son válidas", Toast.LENGTH_SHORT).show();
            return;
        }

        String description = txted_descripcion_repositorio.getText().toString();
        // Verificar si la descripción está vacía
        if (description.isEmpty()) {
            Toast.makeText(this, "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentPhotoPath == null) {
            Toast.makeText(this, "No se ha capturado una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        //Incertar el reporte en la base de datos =================================================
        boolean isInserted = dbHelper.insertReport(currentPhotoPath, latitud, longitud, description);
        if (isInserted) {
            Toast.makeText(this, "Reporte guardado exitosamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar el reporte", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECUEST_CAPTURA_DE_IMAGEN && resultCode == RESULT_OK) {
            ima_imagen.setImageURI(photoURI);
        } else if (resultCode != RESULT_OK) {
            Toast.makeText(this, "No se capturó la imagen", Toast.LENGTH_SHORT).show();
        }
    }


}
