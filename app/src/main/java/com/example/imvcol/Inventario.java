package com.example.imvcol;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class Inventario {

    private Date fecha;
    private String bodega;
    private String producto;
    private int conteo1;
    private String usuario1;
    private int conteo2;
    private String usuario2;
    private int conteo3;
    private String usuario3;

    public Inventario() {

    }

    public Inventario(Date fecha, String bodega, String producto, int conteo1, String usuario1, int conteo2, String usuario2, int conteo3, String usuario3) {
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

    public ContentValues getValues() {
        ContentValues c = new ContentValues();
        c.put("fecha", SQLiteQuery.dateTimeFormat.format(fecha));
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

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("inventario", null, getValues());
    }

    public Object[] selectInventario(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT fecha,bodega,producto,conteo1,usuario1,conteo2,usuario2,conteo3,usuario3 " +
                "FROM inventario");
        return sq.getRecord(db);
    }
}
