package com.example.imvcol;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Subgrupo3 {

    private String subgrupo3;
    private String grupo;
    private String subgrupo;
    private String subgrupo2;
    private String descripcion;

    public Subgrupo3() {

    }

    public Subgrupo3(String subgrupo3, String grupo, String subgrupo, String subgrupo2, String descripcion) {
        this.subgrupo3 = subgrupo3;
        this.grupo = grupo;
        this.subgrupo = subgrupo;
        this.subgrupo2 = subgrupo2;
        this.descripcion = descripcion;
    }

    public ContentValues getValues() {
        ContentValues c = new ContentValues();
        c.put("subgrupo3", subgrupo3);
        c.put("grupo", grupo);
        c.put("subgrupo", subgrupo);
        c.put("subgrupo2", subgrupo2);
        c.put("descripcion", descripcion);
        return c;
    }

    public int countSubgrupos3(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT COUNT(*) FROM subgrupo3");
        return sq.getInteger(db);
    }

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("subgrupo3", null, getValues());
    }

    public Object[][] selectSubgrupos3(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT subgrupo3,descripcion,grupo,subgrupo,subgrupo2 " +
                "FROM subgrupo3");
        return sq.getRecords(db);
    }
}