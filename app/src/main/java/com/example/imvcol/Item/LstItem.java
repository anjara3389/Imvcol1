package com.example.imvcol.Item;

public class LstItem {
    private String nombre;
    private Double cantidad;
    private String conteo;

    public LstItem(String nombre, Double cantidad, String conteo) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.conteo = conteo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public String getConteo() {
        return conteo;
    }

    public void setConteo(String conteo) {
        this.conteo = conteo;
    }
}
