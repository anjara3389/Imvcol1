package com.example.imvcol;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.nio.DoubleBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Inventario {

    private String fecha;
    private String bodega;
    private String producto;
    private Double conteo1;
    private String usuario1;
    private Double conteo2;
    private String usuario2;
    private Double conteo3;
    private String usuario3;

    public Inventario() {

    }

    public Inventario(String fecha, String bodega, String producto, Double conteo1, String usuario1, Double conteo2, String usuario2, Double conteo3, String usuario3) {
        this.fecha = fecha;
        this.bodega = bodega;
        this.producto = producto;
        this.conteo1 = conteo1;
        this.usuario1 = usuario1;
        this.conteo2 = conteo2;
        this.usuario2 = usuario2;
        this.conteo3 = conteo3;
        this.usuario3 = usuario3;
    }

    public Inventario(int numConteo, Double conteo, String usuario) {
        if (numConteo == 1) {
            this.conteo1 = conteo;
            this.usuario1 = usuario;
        } else if (numConteo == 2) {
            this.conteo2 = conteo;
            this.usuario2 = usuario;
        } else if (numConteo == 3) {
            this.conteo3 = conteo;
            this.usuario3 = usuario;
        }
    }

    public ContentValues getValues() {
        ContentValues c = new ContentValues();
        c.put("fecha", fecha);
        c.put("bodega", bodega);
        c.put("producto", producto);
        c.put("conteo1", conteo1);
        c.put("usuario1", usuario1);
        c.put("conteo2", conteo2);
        c.put("usuario2", usuario2);
        c.put("conteo3", conteo3);
        c.put("usuario3", usuario3);
        return c;
    }

    public ContentValues getCurrentValues(int conteo) {
        ContentValues c = new ContentValues();

        if (conteo == 1) {
            c.put("conteo1", conteo1);
            c.put("usuario1", usuario1);
        } else if (conteo == 2) {
            c.put("conteo2", conteo2);
            c.put("usuario2", usuario2);
        } else {
            c.put("conteo3", conteo3);
            c.put("usuario3", usuario3);
        }
        return c;
    }

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("inventario", null, getValues());
    }

    public int countInventarios(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT COUNT(*) FROM inventario");
        return sq.getInteger(db);
    }

    public Inventario selectInventario(SQLiteDatabase db, String producto) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT fecha,bodega,producto,conteo1,usuario1,conteo2,usuario2,conteo3,usuario3 " +
                "FROM inventario " +
                "WHERE producto= '" + producto + "'");
        Object[] rawInventario = sq.getRecord(db);

        if (rawInventario != null && rawInventario.length > 0) {
            return new Inventario(rawInventario[0].toString(),
                    rawInventario[1].toString(),
                    rawInventario[2].toString(),
                    Double.parseDouble(rawInventario[3].toString()),
                    rawInventario[4].toString(),
                    rawInventario[5] != null ? Double.parseDouble(rawInventario[5].toString()) : null,
                    rawInventario[6] != null ? rawInventario[6].toString() : null,
                    rawInventario[7] != null ? Double.parseDouble(rawInventario[7].toString()) : null,
                    rawInventario[8] != null ? rawInventario[8].toString() : null);
        }
        return null;
    }

    public void insertProductsNotOnInventario(SQLiteDatabase db, String bodega, String fecha, String usuario, String grupo,
                                              String subgrupo, String subgr2, String subgr3, String clase,String ubicacion) throws Exception {
        Object[][] productos = new Producto().selectProductsNotOnInventario(db, null,
                grupo,
                subgrupo,
                subgr2,
                subgr3,
                clase,
                ubicacion);
        if (productos != null) {
            for (int i = 0; i < productos.length; i++) {
                Inventario inventario = new Inventario(fecha, bodega, productos[i][0].toString(), null, usuario, null, usuario, null, usuario);
                inventario.insert(db);
            }
        }
    }

    public ArrayList<Inventario> selectInventarios(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT fecha,bodega,producto,conteo1,usuario1,conteo2,usuario2,conteo3,usuario3 " +
                "FROM inventario ");
        Object[][] rawInventario = sq.getRecords(db);

        return getInventarios(rawInventario);
    }

    public Object[][] selectInventariosTotales(SQLiteDatabase db, boolean diferencia, String grupo, String subgrupo, String subgr2, String subgr3, String clase) throws Exception {
        String query = "SELECT p.producto,p.descripcion,p.cantidad,i.conteo1,i.conteo2,i.conteo3  " +
                "FROM producto p " +
                "LEFT JOIN inventario i on p.producto=i.producto " +
                "WHERE grupo='" + grupo + "' ";
        if (subgrupo != null) {
            query += "AND subgrupo='" + subgrupo + "' ";
        }
        if (subgr2 != null) {
            query += "AND subgr2='" + subgr2 + "' ";
        }
        if (subgr3 != null) {
            query += "AND subgr3='" + subgr3 + "' ";
        }
        if (clase != null) {
            query += "AND clase='" + clase + "' ";
        }
        if (diferencia) {
            query += "AND " +
                    "(CASE WHEN (i.conteo1>=0 AND i.conteo2 IS NULL AND i.conteo3 IS NULL) " +
                    "THEN (p.cantidad<>i.conteo1 OR i.conteo1 IS NULL) " +
                    "WHEN(i.conteo2>=0 AND i.conteo3 IS NULL) " +
                    "THEN (p.cantidad<>i.conteo2 OR i.conteo2 IS NULL) " +
                    "ELSE (p.cantidad<>i.conteo3 OR i.conteo3 IS NULL) END) ";
        }
        query += "ORDER BY p.descripcion ASC";

        SQLiteQuery sq = new SQLiteQuery(query);
        Object[][] rawInventario = sq.getRecords(db);

        return rawInventario;
    }

    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM inventario");
    }


    private ArrayList<Inventario> getInventarios(Object[][] rawInventario) {
        ArrayList<Inventario> inventarios = new ArrayList<>();
        if (rawInventario != null && rawInventario.length > 0) {
            for (int i = 0; i < rawInventario.length; i++) {
                inventarios.add(new Inventario(rawInventario[i][0].toString(),
                        rawInventario[i][1].toString(),
                        rawInventario[i][2].toString(),
                        rawInventario[i][3] != null ? Double.parseDouble(rawInventario[i][3].toString()) : null,
                        rawInventario[i][4] != null ? rawInventario[i][4].toString() : null,
                        rawInventario[i][5] != null ? Double.parseDouble(rawInventario[i][5].toString()) : null,
                        rawInventario[i][6] != null ? rawInventario[i][6].toString() : null,
                        rawInventario[i][7] != null ? Double.parseDouble(rawInventario[i][7].toString()) : null,
                        rawInventario[i][8] != null ? rawInventario[i][8].toString() : null));
            }
            return inventarios;
        }
        return null;
    }

    public void updateCurrent(SQLiteDatabase db, int conteo, String producto) {
        db.update("inventario", getCurrentValues(conteo), "producto='" + producto + "'", null);
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getBodega() {
        return bodega;
    }

    public void setBodega(String bodega) {
        this.bodega = bodega;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public Double getConteo1() {
        return conteo1;
    }

    public void setConteo1(Double conteo1) {
        this.conteo1 = conteo1;
    }

    public String getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(String usuario1) {
        this.usuario1 = usuario1;
    }

    public Double getConteo2() {
        return conteo2;
    }

    public void setConteo2(Double conteo2) {
        this.conteo2 = conteo2;
    }

    public String getUsuario2() {
        return usuario2;
    }

    public void setUsuario2(String usuario2) {
        this.usuario2 = usuario2;
    }

    public Double getConteo3() {
        return conteo3;
    }

    public void setConteo3(Double conteo3) {
        this.conteo3 = conteo3;
    }

    public String getUsuario3() {
        return usuario3;
    }

    public void setUsuario3(String usuario3) {
        this.usuario3 = usuario3;
    }

}
