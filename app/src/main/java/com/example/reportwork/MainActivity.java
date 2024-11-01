package com.example.reportwork;

 import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btn_crear_reporte, btn_ver_reporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); //Pantalla completa del dispositivo =========================================
        setContentView(R.layout.activity_main); //Se carga el Activity_main.xml ==================================

        btn_crear_reporte = findViewById(R.id.btn_crear_reporte);
        btn_ver_reporte = findViewById(R.id.btn_ver_reporte);

        //======================================================================================================
        //BOTONES======================================================================================================
        //======================================================================================================

        //Boton 1: Crear reporte nuevo =======================================================
        btn_crear_reporte.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, ReporteActivity.class); //Se crea un intent para abrir la actividad ReporteActivity =================================
            startActivity(intent); //Se inicia la actividad ReporteActivity =================================
        });

        //Boton 2: Ver reportes ======================================================
        btn_ver_reporte.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VerReporteActivity.class);
            startActivity(intent);
        });

        //Boton 3: Salir ======================================================
        findViewById(R.id.btnatras).setOnClickListener(v -> finish()); //Para salir de la app =========================

        //======================================================================================================
        //FIN BOTONES======================================================================================================
        //======================================================================================================

        //Ajustar margenes de la ventana ========================================================================
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}