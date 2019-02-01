package com.example.imvcol;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Inventario {

    private String fecha;
    private String bodega;
    private String producto;
    private Integer conteo1;
    private String usuario1;
    private Integer conteo2;
    private String usuario2;
    private Integer conteo3;
    private String usuario3;

    public Inventario() {

    }


    public Inventario(String fecha, String bodega, String producto, Integer conteo1, String usuario1, Integer conteo2, String usuario2, Integer conteo3, String usuario3) {
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

    public Inventario(int numConteo, Integer conteo, String usuario) {
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
        c.put("conteo" + conteo, (conteo == 1 ? conteo1 : (conteo == 2 ? conteo2 : conteo3)));
        c.put("usuario" + conteo, (conteo == 1 ? usuario1 : (conteo == 2 ? usuario2 : usuario3)));
        return c;
    }

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("inventario", null, getValues());
    }

    public Inventario selectInventario(SQLiteDatabase db, String producto) throws Exception, ParseException {
        SQLiteQuery sq = new SQLiteQuery("SELECT fecha,bodega,producto,conteo1,usuario1,conteo2,usuario2,conteo3,usuario3 " +
                "FROM inventario " +
                "WHERE producto= " + producto);
        Object[] rawInventario = sq.getRecord(db);

        if (rawInventario != null && rawInventario.length > 0) {
            return new Inventario(rawInventario[0].toString(),
                    rawInventario[1].toString(),
                    rawInventario[2].toString(),
                    Integer.parseInt(rawInventario[3].toString()),
                    rawInventario[4].toString(),
                    rawInventario[5] != null ? Integer.parseInt(rawInventario[5].toString()) : null,
                    rawInventario[6] != null ? rawInventario[6].toString() : null,
                    rawInventario[7] != null ? Integer.parseInt(rawInventario[7].toString()) : null,
                    rawInventario[8] != null ? rawInventario[8].toString() : null);
        }
        return null;
    }

    public void insertProductsNotOnInventario(SQLiteDatabase db, String bodega, String fecha) throws Exception {
        Object[][] productos = new Producto().selectProductsNotOnInventario(db);
        for (int i = 0; i < productos.length; i++) {
            Inventario inventario = new Inventario(fecha, bodega, productos[i][0].toString(), null, null, null, null, null, null);
            inventario.insert(db);
        }
    }

    public ArrayList<Inventario> selectInventarios(SQLiteDatabase db) throws Exception, ParseException {
        SQLiteQuery sq = new SQLiteQuery("SELECT fecha,bodega,producto,conteo1,usuario1,conteo2,usuario2,conteo3,usuario3 " +
                "FROM inventario ");
        Object[][] rawInventario = sq.getRecords(db);

        return getInventarios(rawInventario);
    }

    public Object[][] selectInventariosTotales(SQLiteDatabase db, boolean diferencia) throws Exception, ParseException {
        SQLiteQuery sq = new SQLiteQuery("SELECT p.producto,p.descripcion,p.cantidad,i.conteo1,i.conteo2,i.conteo3  " +
                "FROM producto p " +
                "LEFT JOIN inventario i on p.producto=i.producto " +
                (diferencia ? "WHERE (p.cantidad!=i.conteo1 " +
                        "AND p.cantidad!=i.conteo2 " +
                        "AND p.cantidad!=i.conteo3)" : ""));
        Object[][] rawInventario = sq.getRecords(db);

        return rawInventario;
    }

    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM usuario");
    }


    private ArrayList<Inventario> getInventarios(Object[][] rawInventario) {
        ArrayList<Inventario> inventarios = new ArrayList<>();
        if (rawInventario != null && rawInventario.length > 0) {
            for (int i = 0; i < rawInventario.length; i++) {
                for (int j = 0; j < rawInventario[0].length; j++) {
                    inventarios.add(new Inventario(rawInventario[i][j].toString(),
                            rawInventario[i][j].toString(),
                            rawInventario[i][j].toString(),
                            Integer.parseInt(rawInventario[3][j].toString()),
                            rawInventario[i][j].toString(),
                            rawInventario[i][j] != null ? Integer.parseInt(rawInventario[5].toString()) : null,
                            rawInventario[i][j] != null ? rawInventario[6].toString() : null,
                            rawInventario[i][j] != null ? Integer.parseInt(rawInventario[7].toString()) : null,
                            rawInventario[i][j] != null ? rawInventario[8].toString() : null));
                }
            }
            return inventarios;
        }
        return null;
    }

    public void updateCurrent(SQLiteDatabase db, int conteo, String producto) {
        db.update("inventario", getCurrentValues(conteo), "producto=" + producto, null);
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

    public Integer getConteo1() {
        return conteo1;
    }

    public void setConteo1(Integer conteo1) {
        this.conteo1 = conteo1;
    }

    public String getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(String usuario1) {
        this.usuario1 = usuario1;
    }

    public Integer getConteo2() {
        return conteo2;
    }

    public void setConteo2(Integer conteo2) {
        this.conteo2 = conteo2;
    }

    public String getUsuario2() {
        return usuario2;
    }

    public void setUsuario2(String usuario2) {
        this.usuario2 = usuario2;
    }

    public Integer getConteo3() {
        return conteo3;
    }

    public void setConteo3(Integer conteo3) {
        this.conteo3 = conteo3;
    }

    public String getUsuario3() {
        return usuario3;
    }

    public void setUsuario3(String usuario3) {
        this.usuario3 = usuario3;
    }

}
