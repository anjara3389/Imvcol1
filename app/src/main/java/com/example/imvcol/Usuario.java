package com.example.imvcol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Window;
import android.widget.Toast;

import com.example.imvcol.Utils.NetUtils;
import com.example.imvcol.WebserviceConnection.AsyncRemoteQuery.AsyncRemoteQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Usuario {

    private String usuario;
    private String clave;
    private String currBodega;
    private String currGrupo;
    private String currSubgr;
    private String currSubgr2;
    private String currSubgr3;
    private String currClase;
    private String currUbicacion;
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
    private String fecha;

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
                   String currUbicacion,
                   Integer currConteo,
                   Integer modo,
                   Boolean datosEnviados,
                   Integer nProductos,
                   Integer nBodegas,
                   Integer nGrupos,
                   Integer nSubgrupos,
                   Integer nSubgrupos2,
                   Integer nSubgrupos3,
                   Integer nClases,
                   String fecha) {
        this.usuario = usuario;
        this.clave = clave;
        this.currBodega = currBodega;
        this.currGrupo = currGrupo;
        this.currSubgr = currSubgr;
        this.currSubgr2 = currSubgr2;
        this.currSubgr3 = currSubgr3;
        this.currClase = currClase;
        this.currUbicacion = currUbicacion;
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
        this.fecha = fecha;
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
        c.put("curr_ubicacion", currUbicacion);
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
        c.put("fecha", fecha);
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
        c.put("curr_ubicacion", currUbicacion);
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
        c.put("fecha", fecha);
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
            return new Usuario(rawUsuario[0].toString(),//usuario
                    rawUsuario[1].toString(),//clave
                    rawUsuario[2] != null ? rawUsuario[2].toString() : null,//bodega
                    rawUsuario[3] != null ? rawUsuario[3].toString() : null,//grupo
                    rawUsuario[4] != null ? rawUsuario[4].toString() : null,//subr
                    rawUsuario[5] != null ? rawUsuario[5].toString() : null,//subr2
                    rawUsuario[6] != null ? rawUsuario[6].toString() : null,//subr3
                    rawUsuario[7] != null ? rawUsuario[7].toString() : null,//clase
                    rawUsuario[8] != null ? rawUsuario[8].toString() : null,//ubicacion
                    rawUsuario[9] != null ? Integer.parseInt(rawUsuario[9].toString()) : null,//currConteo
                    rawUsuario[10] != null ? Integer.parseInt(rawUsuario[10].toString()) : null,//modo
                    rawUsuario[11] != null ? (Integer.parseInt(rawUsuario[11].toString()) == 0 ? false : true) : null,//datos enviados
                    rawUsuario[12] != null ? Integer.parseInt(rawUsuario[12].toString()) : null,//nProductos
                    rawUsuario[13] != null ? Integer.parseInt(rawUsuario[13].toString()) : null,//nBodegas
                    rawUsuario[14] != null ? Integer.parseInt(rawUsuario[14].toString()) : null,//nGrupos
                    rawUsuario[15] != null ? Integer.parseInt(rawUsuario[15].toString()) : null,//nSubgrupos
                    rawUsuario[16] != null ? Integer.parseInt(rawUsuario[16].toString()) : null,//nSubgrupos2
                    rawUsuario[17] != null ? Integer.parseInt(rawUsuario[17].toString()) : null,//nSubgrupos3
                    rawUsuario[18] != null ? Integer.parseInt(rawUsuario[18].toString()) : null,//Nclases
                    rawUsuario[19] != null ? rawUsuario[19].toString() : null);//fecha
        } else {
            return null;
        }
    }

    public String getFilterQueryForWebservice() {

        String complement = "";
        if (getCurrGrupo() != null) {
            complement += "AND r.grupo='" + getCurrGrupo() + "' ";
        }
        if (getCurrSubgr() != null) {
            complement += "AND r.subgrupo='" + getCurrSubgr() + "' ";
        }
        if (getCurrSubgr2() != null) {
            complement += "AND r.subgrupo2='" + getCurrSubgr2() + "' ";
        }
        if (getCurrSubgr3() != null) {
            complement += "AND r.subgrupo3='" + getCurrSubgr3() + "' ";
        }
        if (getCurrClase() != null) {
            complement += "AND r.clase='" + getCurrClase() + "' ";
        }
        if (getCurrUbicacion() != null) {
            complement += "AND f.ubicacion='" + getCurrUbicacion() + "' ";
        }

        return complement;
    }

    public void deleteSession(Context context) {
        SQLiteDatabase db = BaseHelper.getReadable(context);
        new Usuario().delete(db);
        new Inventario().delete(db);
        new Bodega().delete(db);
        new Producto().delete(db);
        new Grupo().delete(db);
        new Subgrupo().delete(db);
        new Subgrupo2().delete(db);
        new Subgrupo3().delete(db);
        new Clase().delete(db);
        BaseHelper.tryClose(db);
        Intent i = new Intent(context, FrmLogin.class);
        ((Activity) context).startActivityForResult(i, 1);
        ((Activity) context).finish();
    }

    public boolean deleteOldSesion(Context context, Usuario usuario, Window window) throws Exception {
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");

        Date now = dformat.parse(dformat.format(new Date()));
        Date fechaLogin = dformat.parse(this.getfecha());

        if (fechaLogin.compareTo(now) != 0) {
            freeFisicosFromWebservice(context, usuario, window, null);
            return true;
        }
        return false;
    }

    private void freeFisicosFromWebservice(final Context ctx, final Usuario usuario, final Window window, final Class going) throws Exception {
        if (!NetUtils.isOnlineNet(ctx)) {
            throw new Exception("No hay conexión a internet");
        } else {
            @SuppressLint("StaticFieldLeak") AsyncRemoteQuery remote = new AsyncRemoteQuery() {
                @Override
                public void receiveData(Object object) throws Exception {
                    checkWebserviceFisicos(ctx, usuario, window, going);
                }
            };
            ArrayList queryDatos = new ArrayList();
            remote.init(ctx, window, "Cargando");
            String query = "UPDATE f SET fisico=0 " +
                    "FROM referencias_fis f " +
                    "JOIN referencias r on r.codigo=f.codigo " +
                    "JOIN v_referencias_sto s on f.codigo=s.codigo AND f.bodega=s.bodega " +
                    "WHERE f.bodega='" + usuario.getCurrBodega() + "' " +
                    "AND s.ano=YEAR(getdate()) " +
                    "AND s.mes=MONTH(getdate()) " +
                    "AND f.fisico=1  ";

            query += usuario.getFilterQueryForWebservice();
            queryDatos.add(query);
            System.out.println("QUERYYYYYY///" + query);
            remote.setQuery(queryDatos);
            remote.execute();
        }
    }

    private void checkWebserviceFisicos(final Context ctx, final Usuario usuario, Window window, final Class going) throws Exception {
        if (!NetUtils.isOnlineNet(ctx)) {
            throw new Exception("No hay conexión a internet");
        } else {
            @SuppressLint("StaticFieldLeak") AsyncRemoteQuery remote = new AsyncRemoteQuery() {
                @Override
                public void receiveData(Object object) throws Exception {
                    SQLiteDatabase db = BaseHelper.getWritable(ctx);
                    ArrayList resultsDatos = (ArrayList) object;

                    ArrayList rawResults = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), ctx);
                    if (resultsDatos.get(0).equals("[]")) {
                        BaseHelper.tryClose(db);
                        Toast.makeText(ctx, "No se han podido liberar selección, intente nuevamente", Toast.LENGTH_LONG).show();
                    } else {
                        boolean validar = true;

                        for (int i = 0; i < rawResults.size(); i++) {
                            JSONObject fisico = ((JSONObject) rawResults.get(i));
                            if (fisico.getInt("fisico") == 1) {
                                validar = false;
                            }
                        }

                        if (validar == true) {
                            db = BaseHelper.getWritable(ctx);

                            usuario.setCurrGrupo(null);
                            usuario.setCurrSubgr(null);
                            usuario.setCurrSubgr2(null);
                            usuario.setCurrSubgr3(null);
                            usuario.setCurrClase(null);
                            usuario.setCurrUbicacion(null);
                            usuario.setCurrConteo(1);
                            usuario.updateCurrent(db);

                            if (going != null) {
                                new Inventario().delete(db);
                                Intent i = new Intent(ctx, going);
                                ((Activity) ctx).startActivityForResult(i, 1);
                                Toast.makeText(ctx, "Se ha liberado la selección exitosamente", Toast.LENGTH_LONG).show();
                                ((Activity) ctx).finish();
                            } else {
                                deleteSession(ctx);
                                Toast.makeText(ctx, "La sesión es antigua. Debe iniciar sesión otra vez", Toast.LENGTH_LONG).show();
                            }
                            db.close();
                        } else {
                            Toast.makeText(ctx, "No se han podido liberar selección, intente nuevamente", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            };
            remote.init(ctx, window, "Cargando");

            ArrayList queryDatos = new ArrayList();

            String query = "SELECT fisico " +
                    "FROM referencias_fis f " +
                    "JOIN referencias r on r.codigo=f.codigo " +
                    "JOIN v_referencias_sto s on f.codigo=s.codigo AND f.bodega=s.bodega " +
                    "WHERE f.bodega='" + usuario.getCurrBodega() + "' " +
                    "AND s.ano=YEAR(getdate()) " +
                    "AND s.mes=MONTH(getdate()) ";

            query += usuario.getFilterQueryForWebservice();

            queryDatos.add(query);
            System.out.println("QUERYYYYYY///" + query);
            remote.setQuery(queryDatos);
            remote.execute();
        }
    }

    public String getQueryInsertLog(String log) {
        return "INSERT INTO CRM_citas " +
                "(nit," +
                "id_gru," +
                "id_sub," +
                "fecha_hora," +
                "hora,comentario," +
                "usuario)" +
                "VALUES " +
                "((SELECT nit FROM usuarios WHERE usuario='" + this.usuario + "')," +
                "0," +
                "0," +
                "GETDATE ( )," +
                "0 ," +
                "'App Invfiscol 4.2 : " + log + "'," +
                "UPPER('" + this.usuario + "'))";
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

    public String getCurrUbicacion() {
        return currUbicacion;
    }

    public void setCurrUbicacion(String currUbicacion) {
        this.currUbicacion = currUbicacion;
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

    public String getfecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

}
