package com.example.reportwork;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
            Cursor cursor = dbHelper.getAllReports();
            if (cursor != null && cursor.moveToPosition(position)) {
                // Verifica si la columna 'id' está en el cursor
                int colId = cursor.getColumnIndex(dbHelper.getColumnId());
                if (colId != -1) {
                    int reportId = cursor.getInt(colId);
                    Log.d("VerReporteActivity", "ID de reporte seleccionado: " + reportId);

                    Intent intent = new Intent(VerReporteActivity.this, MapActivity.class);
                    intent.putExtra("REPORT_ID", reportId);
                    startActivity(intent);
                } else {
                    Log.e("VerReporteActivity", "No se encontró la columna ID en el cursor");
                    Toast.makeText(this, "Error al obtener el ID del reporte", Toast.LENGTH_SHORT).show();
                }
                cursor.close(); // Cierra el cursor después de su uso
            } else {
                Toast.makeText(this, "Error al obtener el reporte", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadReports() {
        Cursor cursor = dbHelper.getAllReports();
        if (cursor != null) {
            Log.d("VerReporteActivity", "Número de filas en el cursor: " + cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    int colDescripcion = cursor.getColumnIndex(dbHelper.getColumnDescripcion());
                    if (colDescripcion != -1) {
                        String description = cursor.getString(colDescripcion);
                        lista_reportes_array.add(description);
                    }
                } while (cursor.moveToNext());
            } else {
                Log.d("VerReporteActivity", "El cursor no tiene filas");
                Toast.makeText(this, "No hay reportes disponibles", Toast.LENGTH_SHORT).show();
            }

            cursor.close(); // Cierra el cursor después de su uso
        } else {
            Log.e("VerReporteActivity", "El cursor es nulo");
        }

        // Configura el adaptador del ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista_reportes_array);
        lw_lista_reportes.setAdapter(adapter);
    }

}
