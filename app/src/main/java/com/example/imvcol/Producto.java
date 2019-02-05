package com.example.imvcol;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Producto {

    private String producto;
    private String descripcion;
    private String cantidad;
    private String barras;

    public Producto() {

    }

    public Producto(String producto, String descripcion, String cantidad, String barras) {
        this.producto = producto;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.barras = barras;

    }

    public ContentValues getValues() {
        ContentValues c = new ContentValues();
        c.put("producto", producto);
        c.put("descripcion", descripcion);
        c.put("cantidad", cantidad);
        c.put("barras", barras);
        return c;
    }

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("producto", null, getValues());
    }

    public Object[][] selectProductos(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT producto,descripcion,cantidad,barras " +
                "FROM producto");
        return sq.getRecords(db);
    }

    public Object[] selectProductByNumber(SQLiteDatabase db, String numero, int option) throws Exception {
        String query = "SELECT * " +
                "FROM producto " +
                "WHERE ";
        if (!numero.equals("")) {
            if (option == 0) {
                query += "producto='" + numero + "' ";
            } else {
                query += "barras='" + numero + "' ";
            }
        } else {
            query += "descripcion=UPPER('" + descripcion + "') ";
        }

        System.out.println("AQUIIII QUERY " + query);

        SQLiteQuery sq = new SQLiteQuery(query);
        return sq.getRecord(db);
    }

    public Object[][] selectProductsByDescripcion(SQLiteDatabase db, String descripcion) throws Exception {
        String query = "SELECT * " +
                "FROM producto " +
                "WHERE ";
        query += "descripcion LIKE '%" + descripcion + "%' ";

        System.out.println("AQUIIII QUERY " + query);

        SQLiteQuery sq = new SQLiteQuery(query);
        return sq.getRecords(db);
    }

    public Object[][] selectProductsNotOnInventario(SQLiteDatabase db, Integer conteo) throws Exception {

        String condition = conteo == null ? "" : (conteo == 1 ? "WHERE i.conteo1 IS NOT NULL " : (conteo == 2 ? "WHERE i.conteo2 IS NOT NULL " : (conteo == 3 ? "WHERE i.conteo3 IS NOT NULL " : "")));

        String query = "SELECT p.producto,p.descripcion " +
                "FROM producto p " +
                "WHERE p.producto NOT IN " +
                "(SELECT i.producto " +
                "FROM inventario i " +
                condition +
                ") " +
                "ORDER BY p.descripcion";
        System.out.println("AQUIIII QUERY " + query);

        SQLiteQuery sq = new SQLiteQuery(query);
        return sq.getRecords(db);
    }

    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM producto");
    }

}
