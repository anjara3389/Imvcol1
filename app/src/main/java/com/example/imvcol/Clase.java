package com.example.imvcol;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Clase {

    private String clase;
    private String descripcion;

    public Clase() {

    }

    public Clase(String clase, String descripcion) {
        this.clase = clase;
        this.descripcion = descripcion;
    }

    public ContentValues getValues() {
        ContentValues c = new ContentValues();
        c.put("clase", clase);
        c.put("descripcion", descripcion);
        return c;
    }

    public int countClases(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT COUNT(*) FROM clase");
        return sq.getInteger(db);
    }

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("clase", null, getValues());
    }

    public Object[][] selectClases(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT DISTINCT c.clase,c.descripcion " +
                "FROM clase c " +
                "LEFT JOIN producto p ON p.clase=c.clase " +
                "WHERE (p.clase=c.clase OR c.clase=-1) " +
                "AND (p.inventareado<>1 OR p.inventareado is NULL) " +
                "ORDER BY CAST(c.clase as integer) ASC");
        return sq.getRecords(db);
    }

    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM clase");
    }

    public int insertEmpty(SQLiteDatabase db) {
        Clase empty = new Clase("-1", "Seleccione una clase");
        return empty.insert(db);
    }

}
