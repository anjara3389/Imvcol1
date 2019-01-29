package com.example.imvcol;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Usuario {

    private String usuario;
    private String clave;
    private String currBodega;
    private String currGrupo;
    private String currSubgr;
    private String currSubgr2;
    private String currSubgr3;
    private String currClase;
    private int currConteo;

    public Usuario() {

    }

    public Usuario(String usuario, String clave, String currBodega, String currGrupo, String currSubgr, String currSubgr2, String currSubgr3, String currClase, int currConteo) {
        this.usuario = usuario;
        this.clave = clave;
        this.currBodega = currBodega;
        this.currGrupo = currGrupo;
        this.currSubgr = currSubgr;
        this.currSubgr2 = currSubgr2;
        this.currSubgr3 = currSubgr3;
        this.currClase = currClase;
        this.currConteo = currConteo;
    }

    public ContentValues getValues() {
        ContentValues c = new ContentValues();
        c.put("usuario", usuario);
        c.put("clave", clave);
        c.put("curr_bodega", currBodega);
        c.put("curr_grupo", currGrupo);
        c.put("curr_subgr", currSubgr);
        c.put("curr_subgr2", currSubgr2);
        c.put("curr_subgr3", currSubgr3);
        c.put("curr_clase", currClase);
        c.put("curr_conteo", currConteo);
        return c;
    }

    public ContentValues getCurrentValues() {
        ContentValues c = new ContentValues();
        c.put("curr_bodega", currBodega);
        c.put("curr_grupo", currGrupo);
        c.put("curr_subgr", currSubgr);
        c.put("curr_subgr2", currSubgr2);
        c.put("curr_subgr3", currSubgr3);
        c.put("curr_clase", currClase);
        c.put("curr_conteo", currConteo);
        return c;
    }

    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM usuario");
    }

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("usuario", null, getValues());
    }

    public void updateCurrent(SQLiteDatabase db) {
        db.update("usuario", getCurrentValues(), null, null);
    }

    public Object[] selectUsuario(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT usuario,clave,curr_bodega,curr_grupo,curr_subgr,curr_subgr2,curr_subgr3,curr_clase,curr_conteo " +
                "FROM usuario");
        return sq.getRecord(db);
    }

}
