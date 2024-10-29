package com.example.reportwork;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import com.example.reportwork.BaseDatos.DBHelper;
import com.google.android.gms.location.FusedLocationProviderClient;  // Nuevo import
import com.google.android.gms.location.LocationServices;  // Nuevo import
import com.google.android.gms.tasks.OnSuccessListener;  // Nuevo import
import android.location.Location;  // Nuevo import



import java.io.ByteArrayOutputStream;

public class ReporteActivity extends AppCompatActivity {

    private static final int RECUEST_CAPTURA_DE_IMAGEN = 1;
    private static final int RECUEST_PERMISO_LOCALIZACION = 2;

    private ImageView ima_imagen;
    private EditText txted_descripcion_repositorio;
    private Button btn_guardar_reporte;

    private Bitmap imagen_bitmap;
    private double latitud = 0.0, longitud = 0.0;
    private DBHelper dbHelper;

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

        // Solicitar permisos para la ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RECUEST_PERMISO_LOCALIZACION);
        } else {
            obtenerUbicacion();  // Llamar a la función para obtener la ubicación
        }

    }

    // Obtener la ubicación ============================================
    private void obtenerUbicacion() {
        // Verificar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si los permisos no están concedidos, solicitar nuevamente
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    RECUEST_PERMISO_LOCALIZACION);
            return;
        }

        fusedLocationClient.getLastLocation() //Soliciar la ultima ubicacion conocida del dispositivo ===========================================
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
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
                startActivityForResult(takePictureIntent, RECUEST_CAPTURA_DE_IMAGEN);
            }
        }
    }

    //Funcion para poder guardar el reporte ======================================================0
    private void guardar_reporte() {
        // Verificar si las coordenadas son válidas antes de guardar
        if (latitud == 0.0 || longitud == 0.0) {
            Toast.makeText(this, "Las coordenadas no son válidas", Toast.LENGTH_SHORT).show();
            return;
        }

        String description = txted_descripcion_repositorio.getText().toString();

        // Convierte el Bitmap a byte[] para guardarlo en SQLite
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (imagen_bitmap != null) {  // Verificación añadida
            imagen_bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        }
        byte[] imageBytes = stream.toByteArray();

        boolean isInserted = dbHelper.insertReport(imageBytes, latitud, longitud, description);

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

        switch (requestCode){
            case RECUEST_CAPTURA_DE_IMAGEN: //Permisos para la camara ===============================================================================
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tomar_foto(); // Vuelve a intentar tomar la foto si se concede el permiso
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
                break;

            case RECUEST_PERMISO_LOCALIZACION: //Permisos para la Localización ========================================================================
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation(); // Llama al método para obtener la ubicación si el permiso fue concedido
                } else {
                    Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECUEST_CAPTURA_DE_IMAGEN && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imagen_bitmap = (Bitmap) extras.get("data");
            ima_imagen.setImageBitmap(imagen_bitmap); // Muestra la imagen en el ImageView
        }
    }

}
