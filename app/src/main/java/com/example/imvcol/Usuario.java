package com.example.imvcol;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

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
    private Integer modo;
    private Boolean datosEnviados;
    private Integer nProductos;
    private Integer nBodegas;
    private Integer nGrupos;
    private Integer nSubgrupos;
    private Integer nSubgrupos2;
    private Integer nSubgrupos3;
    private Integer nClases;

    public final static int MODO_LISTA = 0;
    public final static int MODO_BARRAS = 1;

    public Usuario() {

    }

    public Usuario(String usuario,
                   String clave,
                   String currBodega,
                   String currGrupo,
                   String currSubgr,
                   String currSubgr2,
                   String currSubgr3,
                   String currClase,
                   Integer currConteo,
                   Integer modo,
                   Boolean datosEnviados,
                   Integer nProductos,
                   Integer nBodegas,
                   Integer nGrupos,
                   Integer nSubgrupos,
                   Integer nSubgrupos2,
                   Integer nSubgrupos3,
                   Integer nClases) {
        this.usuario = usuario;
        this.clave = clave;
        this.currBodega = currBodega;
        this.currGrupo = currGrupo;
        this.currSubgr = currSubgr;
        this.currSubgr2 = currSubgr2;
        this.currSubgr3 = currSubgr3;
        this.currClase = currClase;
        this.currConteo = currConteo;
        this.modo = modo;
        this.datosEnviados = datosEnviados;
        this.nProductos = nProductos;
        this.nBodegas = nBodegas;
        this.nGrupos = nGrupos;
        this.nSubgrupos = nSubgrupos;
        this.nSubgrupos2 = nSubgrupos2;
        this.nSubgrupos3 = nSubgrupos3;
        this.nClases = nClases;
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
        c.put("modo", modo);
        c.put("datos_enviados", datosEnviados);
        c.put("nProductos", nProductos);
        c.put("nBodegas", nBodegas);
        c.put("nGrupos", nGrupos);
        c.put("nSubgrupos", nSubgrupos);
        c.put("nSubgrupos2", nSubgrupos2);
        c.put("nSubgrupos3", nSubgrupos3);
        c.put("nClases", nClases);
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
        c.put("modo", modo);
        c.put("datos_enviados", datosEnviados);
        c.put("nProductos", nProductos);
        c.put("nBodegas", nBodegas);
        c.put("nGrupos", nGrupos);
        c.put("nSubgrupos", nSubgrupos);
        c.put("nSubgrupos2", nSubgrupos2);
        c.put("nSubgrupos3", nSubgrupos3);
        c.put("nClases", nClases);
        return c;
    }

    public int countUsuario(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT COUNT(*) FROM usuario");
        return sq.getInteger(db);
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
        SQLiteQuery sq = new SQLiteQuery("SELECT * " +
                "FROM usuario");

        Object[] rawUsuario = sq.getRecord(db);
        if (rawUsuario != null && rawUsuario.length > 0) {
            return new Usuario(rawUsuario[0].toString(),
                    rawUsuario[1].toString(),
                    rawUsuario[2] != null ? rawUsuario[2].toString() : null,
                    rawUsuario[3] != null ? rawUsuario[3].toString() : null,
                    rawUsuario[4] != null ? rawUsuario[4].toString() : null,
                    rawUsuario[5] != null ? rawUsuario[5].toString() : null,
                    rawUsuario[6] != null ? rawUsuario[6].toString() : null,
                    rawUsuario[7] != null ? rawUsuario[7].toString() : null,
                    rawUsuario[8] != null ? Integer.parseInt(rawUsuario[8].toString()) : null,
                    rawUsuario[9] != null ? Integer.parseInt(rawUsuario[9].toString()) : null,
                    rawUsuario[10] != null ? (Integer.parseInt(rawUsuario[10].toString()) == 0 ? false : true) : null,
                    rawUsuario[11] != null ? Integer.parseInt(rawUsuario[11].toString()) : null,
                    rawUsuario[12] != null ? Integer.parseInt(rawUsuario[12].toString()) : null,
                    rawUsuario[13] != null ? Integer.parseInt(rawUsuario[13].toString()) : null,
                    rawUsuario[14] != null ? Integer.parseInt(rawUsuario[14].toString()) : null,
                    rawUsuario[15] != null ? Integer.parseInt(rawUsuario[15].toString()) : null,
                    rawUsuario[16] != null ? Integer.parseInt(rawUsuario[16].toString()) : null,
                    rawUsuario[17] != null ? Integer.parseInt(rawUsuario[17].toString()) : null);
        } else {
            return null;
        }
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

    public int getModo() {
        return modo;
    }

    public void setModo(int modo) {
        this.modo = modo;
    }

    public boolean getDatosEnviados() {
        return datosEnviados;
    }

    public void setDatosEnviados(boolean datosEnviados) {
        this.datosEnviados = datosEnviados;
    }

    public Integer getnProductos() {
        return nProductos;
    }

    public void setnProductos(Integer nProductos) {
        this.nProductos = nProductos;
    }

    public Integer getnBodegas() {
        return nBodegas;
    }

    public void setnBodegas(Integer nBodegas) {
        this.nBodegas = nBodegas;
    }

    public Integer getnGrupos() {
        return nGrupos;
    }

    public void setnGrupos(Integer nGrupos) {
        this.nGrupos = nGrupos;
    }

    public Integer getnSubgrupos() {
        return nSubgrupos;
    }

    public void setnSubgrupos(Integer nSubgrupos) {
        this.nSubgrupos = nSubgrupos;
    }

    public Integer getnSubgrupos2() {
        return nSubgrupos2;
    }

    public void setnSubgrupos2(Integer nSubgrupos2) {
        this.nSubgrupos2 = nSubgrupos2;
    }

    public Integer getnSubgrupos3() {
        return nSubgrupos3;
    }

    public void setnSubgrupos3(Integer nSubgrupos3) {
        this.nSubgrupos3 = nSubgrupos3;
    }

    public Integer getnClases() {
        return nClases;
    }

    public void setnClases(Integer nClases) {
        this.nClases = nClases;
    }
}
