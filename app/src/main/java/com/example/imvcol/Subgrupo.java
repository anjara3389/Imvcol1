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
        SQLiteQuery sq = new SQLiteQuery("SELECT DISTINCT s.subgrupo,s.descripcion,s.grupo " +
                "FROM subgrupo s " +
                "LEFT JOIN producto p ON p.subgrupo=s.subgrupo  " +
                "WHERE p.subgrupo=s.subgrupo OR s.subgrupo=-1 " +
                "ORDER BY CAST(s.subgrupo as integer) ASC");
        return sq.getRecords(db);
    }

    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM subgrupo");
    }

}
