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
        SQLiteQuery sq = new SQLiteQuery("SELECT DISTINCT g.grupo,g.descripcion " +
                "FROM grupo g " +
                "LEFT JOIN producto p ON p.grupo=g.grupo  " +
                "WHERE (p.grupo=g.grupo OR g.grupo=-1) " +
                "AND (p.inventareado<>1 OR p.inventareado is NULL) " +
                "ORDER BY CAST(g.grupo as integer) ASC");
        return sq.getRecords(db);
    }

    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM grupo");
    }

    public int insertEmpty(SQLiteDatabase db) {
        Grupo empty = new Grupo("-1", "Seleccione un grupo");
        return empty.insert(db);
    }
    public int count(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT COUNT(*) FROM grupo");
        return sq.getInteger(db);
    }


}
