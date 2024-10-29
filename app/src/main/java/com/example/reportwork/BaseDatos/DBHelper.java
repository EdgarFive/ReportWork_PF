package com.example.reportwork.BaseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.database.Cursor;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME  = "reportes.db";
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla y columnas
    private static final String TABLE_NAME = "reportes";
    private static final String COL_ID = "id";
    private static final String COL_IMAGEN = "imagen";
    private static final String COL_LATITUD = "latitud";
    private static final String COL_LONGITUD = "longitud";
    private static final String COL_DESCRIPCION = "descripcion";
    private static final String COL_DATE = "date";

    //Constructor ==============================================
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Método para crear la base de datos (se ejecuta la primera vez que se usa) =============
    @Override
    //se crea la tabla la primera vez que se ejecuta la aplicacion
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_IMAGEN + " BLOB, " +
                COL_LATITUD + " REAL, " +
                COL_LONGITUD + " REAL, " +
                COL_DESCRIPCION + " TEXT, " +
                COL_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

        db.execSQL(createTable);
    }

    //Con esto eliminamos la tabla mamalona ====================
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Ingresar nuevo reporte ========================
    public boolean insertReport(byte[] imagen, double latitud, double longitud, String descripcion) {
        // Verifica que las coordenadas no sean 0.0
        if (latitud == 0.0 || longitud == 0.0) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IMAGEN, imagen);
        values.put(COL_LATITUD, latitud);
        values.put(COL_LONGITUD, longitud);
        values.put(COL_DESCRIPCION, descripcion);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    // Método para obtener todos los reportes =============
    public Cursor getAllReports() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        return cursor;
    }

    // Método para obtener un reporte por ID
    public Cursor getReportById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Métodos getter para las columnas
    public String getColumnId() {
        return COL_ID;
    }

    public String getColumnImagen() {
        return COL_IMAGEN;
    }

    public String getColumnLatitud() {
        return COL_LATITUD;
    }

    public String getColumnLongitud() {
        return COL_LONGITUD;
    }

    public String getColumnDescripcion() {
        return COL_DESCRIPCION;
    }

    public String getColumnDate() {
        return COL_DATE;
    }


}
