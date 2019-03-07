package com.example.imvcol;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Producto {

    private String producto;
    private String descripcion;
    private String cantidad;
    private String barras;
    private String grupo;
    private String subgrupo;
    private String subgr2;
    private String subgr3;
    private String clase;
    private Boolean inventareado;

    public Producto() {

    }

    public Producto(String producto, String descripcion, String cantidad, String barras, String grupo, String subgrupo, String subgr2, String subgr3, String clase, Boolean inventareado) {
        this.producto = producto;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.barras = barras;
        this.grupo = grupo;
        this.subgrupo = subgrupo;
        this.subgr2 = subgr2;
        this.subgr3 = subgr3;
        this.clase = clase;
        this.inventareado = inventareado;

    }

    public ContentValues getValues() {
        ContentValues c = new ContentValues();
        c.put("producto", producto);
        c.put("descripcion", descripcion);
        c.put("cantidad", cantidad);
        c.put("barras", barras);
        c.put("grupo", grupo);
        c.put("subgrupo", subgrupo);
        c.put("subgr2", subgr2);
        c.put("subgr3", subgr3);
        c.put("clase", clase);
        c.put("inventareado", inventareado);
        return c;
    }

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("producto", null, getValues());
    }

    public Object[][] selectProductos(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT producto,descripcion,cantidad,barras,grupo,subgrupo,subgr2,subgr3,clase,inventareado " +
                "FROM producto");
        return sq.getRecords(db);
    }

    public Object[] selectProductByNumber(SQLiteDatabase db, String numero, int option, String grupo, String subgrupo, String subgr2, String subgr3, String clase) throws Exception {
        String query = "SELECT * " +
                "FROM producto " +
                "WHERE ";

        if (option == 0) {
            query += "producto='" + numero + "' ";
        } else {
            query += "barras='" + numero + "' ";
        }
        if (grupo != null) {
            query += "AND grupo='" + grupo + "' ";
        }
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


        System.out.println("AQUIIII QUERY " + query);

        SQLiteQuery sq = new SQLiteQuery(query);
        return sq.getRecord(db);
    }

    public Object[][] selectProductsByDescripcion(SQLiteDatabase db, String descripcion, String grupo, String subgrupo, String subgr2, String subgr3, String clase) throws Exception {
        String query = "SELECT * " +
                "FROM producto " +
                "WHERE ";
        query += "descripcion LIKE '%" + descripcion + "%' ";


        if (grupo != null) {
            query += "AND grupo='" + grupo + "' ";
        }
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

        System.out.println("AQUIIII QUERY " + query);

        SQLiteQuery sq = new SQLiteQuery(query);
        return sq.getRecords(db);
    }

    public int countProductos(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT COUNT(*) FROM producto");
        return sq.getInteger(db);
    }

    public Object[][] selectProductsNotOnInventario(SQLiteDatabase db, Integer conteo, String grupo, String subgrupo, String subgr2, String subgr3, String clase) throws Exception {

        String condition = conteo == null ? "" : (conteo == 1 ? "WHERE i.conteo1 IS NOT NULL " :
                (conteo == 2 ? "WHERE i.conteo2 IS NOT NULL OR i.conteo1=p.cantidad  " :
                        (conteo == 3 ? "WHERE i.conteo3 IS NOT NULL OR i.conteo1=p.cantidad OR i.conteo2=p.cantidad" : "")));

        String secCondition = "";
        if (grupo != null) {
            secCondition += "AND p.grupo='" + grupo + "' ";
        }
        if (subgrupo != null) {
            secCondition += "AND p.subgrupo='" + subgrupo + "' ";
        }
        if (subgr2 != null) {
            secCondition += "AND p.subgr2='" + subgr2 + "' ";
        }
        if (subgr3 != null) {
            secCondition += "AND p.subgr3='" + subgr3 + "' ";
        }
        if (clase != null) {
            secCondition += "AND p.clase='" + clase + "' ";
        }

        String query = "SELECT p.producto,p.descripcion " +
                "FROM producto p " +
                "WHERE p.producto NOT IN " +
                "(SELECT i.producto " +
                "FROM inventario i " +
                "INNER JOIN producto p on p.producto=i.producto " +
                condition +
                ") " +
                secCondition +
                "ORDER BY p.descripcion ASC";
        System.out.println("AQUIIII QUERY " + query);

        SQLiteQuery sq = new SQLiteQuery(query);
        return sq.getRecords(db);
    }

    public void updateProductosOnInventario(SQLiteDatabase db) {
        String query = "UPDATE producto " +
                "SET inventareado = 1 " +
                "WHERE producto IN (SELECT producto FROM inventario)" ;

        db.execSQL(query);
    }

    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM producto");
    }

}
