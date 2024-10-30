package com.example.reportwork;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reportwork.BaseDatos.DBHelper;

import java.util.ArrayList;



public class VerReporteActivity extends AppCompatActivity {

    private ListView lw_lista_reportes;
    private ArrayList<String> lista_reportes_array;
    private ArrayList<Integer> lista_reportes_ids;
    private ArrayAdapter<String> adapter;
    private DBHelper dbHelper;
    private Button btnAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_reporte);

        lw_lista_reportes = findViewById(R.id.lw_lista_reportes);
        dbHelper = new DBHelper(this);
        lista_reportes_array = new ArrayList<>();
        lista_reportes_ids = new ArrayList<>();

        // Cargar los reportes desde la base de datos
        loadReports();

        // Manejar el clic en el botón "Atrás"
        findViewById(R.id.btnatras).setOnClickListener(v -> finish());

        // Al hacer clic en un reporte, abrir la vista de mapa
        lw_lista_reportes.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = dbHelper.getAllReports();
            if (cursor != null && cursor.moveToPosition(position)) {
                // Verifica si la columna 'id' está en el cursor
                int colId = cursor.getColumnIndex(dbHelper.getColumnId());
                if (colId != -1) {
                    int reportId = cursor.getInt(colId);

                    Intent intent = new Intent(VerReporteActivity.this, MapActivity.class);
                    intent.putExtra("REPORT_ID", reportId);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Error al obtener el ID del reporte", Toast.LENGTH_SHORT).show();
                }
                cursor.close(); // Cierra el cursor después de su uso
            } else {
                Toast.makeText(this, "Error al obtener el reporte", Toast.LENGTH_SHORT).show();
            }
        });

        // Al mantener presionado un elemento, mostrar el menú para eliminar
        lw_lista_reportes.setOnItemLongClickListener((parent, view, position, id) -> {
            int reportId = lista_reportes_ids.get(position);
            showDeleteConfirmationDialog(reportId, position);
            return true;
        });

    }

    //Leer todos los reportes de la base de datos y agregarlos a la lista
    private void loadReports() {
        Cursor cursor = dbHelper.getAllReports();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int colDescripcion = cursor.getColumnIndex(dbHelper.getColumnDescripcion());
                    int colId = cursor.getColumnIndex(dbHelper.getColumnId());

                    if (colDescripcion != -1 && colId != -1) {
                        String description = cursor.getString(colDescripcion);
                        int reportId = cursor.getInt(colId);

                        lista_reportes_array.add(description);
                        lista_reportes_ids.add(reportId);
                    }
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(this, "No hay reportes disponibles", Toast.LENGTH_SHORT).show();
            }
            cursor.close(); // Cierra el cursor después de su uso
        } else {
            Toast.makeText(this, "El cursos es nulo", Toast.LENGTH_SHORT).show();
        }

        // Configura el adaptador del ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista_reportes_array);
        lw_lista_reportes.setAdapter(adapter);
    }


    // Mostrar un diálogo de confirmación para eliminar un reporte
    private void showDeleteConfirmationDialog(int reportId, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Reporte")
                .setMessage("¿Estás seguro de que quieres eliminar este reporte?")
                .setPositiveButton("Sí", (dialog, which) -> deleteReport(reportId, position))
                .setNegativeButton("No", null)
                .show();
    }

    // Eliminar el reporte de la base de datos y de la lista
    private void deleteReport(int reportId, int position) {
        boolean isDeleted = dbHelper.deleteReport(reportId);
        if (isDeleted) {
            lista_reportes_array.remove(position);
            lista_reportes_ids.remove(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Reporte eliminado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al eliminar el reporte", Toast.LENGTH_SHORT).show();
        }
    }

}
