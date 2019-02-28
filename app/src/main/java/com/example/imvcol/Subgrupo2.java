package com.example.imvcol;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Subgrupo2 {

    private String subgrupo2;
    private String grupo;
    private String subgrupo;
    private String descripcion;

    public Subgrupo2() {

    }

    public Subgrupo2(String subgrupo2, String grupo, String subgrupo, String descripcion) {
        this.subgrupo2 = subgrupo2;
        this.subgrupo = subgrupo;
        this.grupo = grupo;
        this.descripcion = descripcion;
    }

    public ContentValues getValues() {
        ContentValues c = new ContentValues();
        c.put("subgrupo2", subgrupo2);
        c.put("subgrupo", subgrupo);
        c.put("grupo", grupo);
        c.put("descripcion", descripcion);
        return c;
    }

    public int countSubgrupos2(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT COUNT(*) FROM subgrupo2");
        return sq.getInteger(db);
    }

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("subgrupo2", null, getValues());
    }

    public Object[][] selectSubgrupos2(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT DISTINCT s.subgrupo2,s.descripcion,s.grupo,s.subgrupo " +
                "FROM subgrupo2 s " +
                "LEFT JOIN producto p ON p.subgr2=s.subgrupo2 " +
                "WHERE p.subgr2=s.subgrupo2 OR s.subgrupo2=-1 " +
                "ORDER BY CAST(s.subgrupo2 as integer) ASC");

        return sq.getRecords(db);
    }

    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM subgrupo2");
    }

}
