package com.example.imvcol;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Grupo {

    private String grupo;
    private String descripcion;

    public Grupo() {

    }

    public Grupo(String grupo, String descripcion) {
        this.grupo = grupo;
        this.descripcion = descripcion;
    }

    public ContentValues getValues() {
        ContentValues c = new ContentValues();
        c.put("grupo", grupo);
        c.put("descripcion", descripcion);
        return c;
    }

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("grupo", null, getValues());
    }

    public Object[][] selectGrupos(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT grupo,descripcion " +
                "FROM grupo");
        return sq.getRecords(db);
    }
    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM grupo");
    }

}
