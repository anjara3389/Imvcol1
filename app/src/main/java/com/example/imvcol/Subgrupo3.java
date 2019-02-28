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
        SQLiteQuery sq = new SQLiteQuery("SELECT DISTINCT s.subgrupo3,s.descripcion,s.grupo,s.subgrupo,s.subgrupo2 " +
                "FROM subgrupo3 s " +
                "LEFT JOIN producto p ON p.subgr3=s.subgrupo3 OR p.subgr3=-1 " +
                "WHERE (p.subgr3=s.subgrupo3 " +
                "AND p.grupo=s.grupo " +
                "AND p.subgrupo=s.subgrupo " +
                "AND p.subgr2=s.subgrupo2 ) " +
                "OR s.subgrupo3=-1 " +
                "ORDER BY CAST(s.subgrupo3 as integer) ASC");

        return sq.getRecords(db);
    }

    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM subgrupo3");
    }

    public int insertEmpty(SQLiteDatabase db) {
        Subgrupo3 empty = new Subgrupo3("-1", "-1", "-1", "-1", "Seleccione un subgrupo 3");
        return empty.insert(db);
    }
}
