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

    public Usuario selectUsuario(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT usuario,clave,curr_bodega,curr_grupo,curr_subgr,curr_subgr2,curr_subgr3,curr_clase,curr_conteo " +
                "FROM usuario");

        Object[] rawUsuario = sq.getRecord(db);

        return new Usuario(rawUsuario[0].toString(),
                rawUsuario[1].toString(),
                rawUsuario[2].toString(),
                rawUsuario[3].toString(),
                rawUsuario[4] != null ? rawUsuario[4].toString() : null,
                rawUsuario[5] != null ? rawUsuario[5].toString() : null,
                rawUsuario[6] != null ? rawUsuario[6].toString() : null,
                rawUsuario[7] != null ? rawUsuario[7].toString() : null,
                Integer.parseInt(rawUsuario[8].toString()));
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getCurrBodega() {
        return currBodega;
    }

    public void setCurrBodega(String currBodega) {
        this.currBodega = currBodega;
    }

    public String getCurrGrupo() {
        return currGrupo;
    }

    public void setCurrGrupo(String currGrupo) {
        this.currGrupo = currGrupo;
    }

    public String getCurrSubgr() {
        return currSubgr;
    }

    public void setCurrSubgr(String currSubgr) {
        this.currSubgr = currSubgr;
    }

    public String getCurrSubgr2() {
        return currSubgr2;
    }

    public void setCurrSubgr2(String currSubgr2) {
        this.currSubgr2 = currSubgr2;
    }

    public String getCurrSubgr3() {
        return currSubgr3;
    }

    public void setCurrSubgr3(String currSubgr3) {
        this.currSubgr3 = currSubgr3;
    }

    public String getCurrClase() {
        return currClase;
    }

    public void setCurrClase(String currClase) {
        this.currClase = currClase;
    }

    public int getCurrConteo() {
        return currConteo;
    }

    public void setCurrConteo(int currConteo) {
        this.currConteo = currConteo;
    }
}
