package com.example.imvcol;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class Bodega {

    private String bodega;
    private String descripcion;
    private String localizacion;

    public Bodega() {

    }

    public Bodega(String bodega, String descripcion, String localizacion) {
        this.bodega = bodega;
        this.descripcion = descripcion;
        this.localizacion = localizacion;
    }

    public ContentValues getValues() {
        ContentValues c = new ContentValues();
        c.put("bodega", bodega);
        c.put("descripcion", descripcion);
        c.put("localizacion", localizacion);
        return c;
    }

    public int countBodegas(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT COUNT(*) FROM bodega");
        return sq.getInteger(db);
    }

    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM bodega");
    }

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("bodega", null, getValues());
    }

    public Object[][] selectBodegas(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT bodega,descripcion,localizacion " +
                "FROM bodega");
        return sq.getRecords(db);
    }

    public int insertEmpty(SQLiteDatabase db) {
        Bodega emptyBodega = new Bodega("-1", "Seleccione una bodega","NN,NN");
        return emptyBodega.insert(db);
    }
}
