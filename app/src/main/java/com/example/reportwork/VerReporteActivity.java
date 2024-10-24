package com.example.reportwork;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reportwork.BaseDatos.DBHelper;

import java.util.ArrayList;



public class VerReporteActivity extends AppCompatActivity {

    private ListView lw_lista_reportes;
    private ArrayList<String> lista_reportes_array;
    private ArrayAdapter<String> adapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_reporte);

        lw_lista_reportes = findViewById(R.id.lw_lista_reportes);
        dbHelper = new DBHelper(this);
        lista_reportes_array = new ArrayList<>();

        // Cargar los reportes desde la base de datos
        loadReports();

        // Al hacer clic en un reporte, abrir la vista de mapa
        lw_lista_reportes.setOnItemClickListener((parent, view, position, id) -> {
            // Obtener el ID del reporte seleccionado
            Cursor cursor = dbHelper.getAllReports();
            cursor.moveToPosition(position);
            int reportId = cursor.getInt(0);

            // Abrir MapActivity y pasar el ID del reporte
            Intent intent = new Intent(VerReporteActivity.this, MapActivity.class);
            intent.putExtra("REPORT_ID", reportId);
            startActivity(intent);
        });
    }



    private void loadReports() {
        Cursor cursor = dbHelper.getAllReports();
        if (cursor.moveToFirst()) {
            do {
                String description = cursor.getString(4); // Columna de descripción
                lista_reportes_array.add(description);
            } while (cursor.moveToNext());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista_reportes_array);
        lw_lista_reportes.setAdapter(adapter);
    }


}
