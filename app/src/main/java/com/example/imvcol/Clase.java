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
        SQLiteQuery sq = new SQLiteQuery("SELECT clase,descripcion " +
                "FROM clase");
        return sq.getRecords(db);
    }
    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM clase");
    }

}
