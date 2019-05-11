package ec.com.cooprogreso;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class BaseDatos extends SQLiteOpenHelper {
    String tabla = "CREATE TABLE MARCA(Id INTEGER PRIMARY KEY AUTOINCREMENT, latitud REAL, longitud REAL)";

    public BaseDatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Metodo que genera el SQL para insertar Marca
     */
    public void insertarMarca(Double latitud, Double longitud) {
        ContentValues registronuevo = new ContentValues();
        registronuevo.put("latitud", latitud);
        registronuevo.put("longitud", longitud);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("MARCA", null, registronuevo);
    }

    /**
     * Elimina los datos de la tabla de marcas
     */
    public void eliminarDatosMarcas() {
        String sqlDelete = "DELETE FROM MARCA";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlDelete);
    }

    /**
     * CargarMarcas y devolver la latitud y longitud
     */
    public  ArrayList<LatLng> cargarMarcas() {
        ArrayList<LatLng> latLngCol = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            int i = 0;
            Cursor c = db.rawQuery("select * from MARCA", null);
            if (c.moveToFirst()) {
                do {
                    latLngCol.add(new LatLng(c.getDouble(1), c.getDouble(2)));
                    i++;
                } while (c.moveToNext());
            }
        }
        return latLngCol;
    }

    //solo se crea cunado la base de datos no existe
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tabla);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
