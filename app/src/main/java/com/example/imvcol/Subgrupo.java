package com.example.imvcol;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Subgrupo {
    private String subgrupo;
    private String grupo;
    private String descripcion;

    public Subgrupo() {

    }

    public Subgrupo(String subgrupo, String grupo, String descripcion) {
        this.subgrupo = subgrupo;
        this.descripcion = descripcion;
        this.grupo = grupo;
    }

    public ContentValues getValues() {
        ContentValues c = new ContentValues();
        c.put("subgrupo", subgrupo);
        c.put("descripcion", descripcion);
        c.put("grupo", grupo);
        return c;
    }

    public int countSubgrupos(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT COUNT(*) FROM subgrupo");
        return sq.getInteger(db);
    }

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("subgrupo", null, getValues());
    }

    public Object[][] selectSubgrupos(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT subgrupo,descripcion,grupo " +
                "FROM subgrupo");
        return sq.getRecords(db);
    }

}
