package com.eneko.gastospersonalesaplicacion.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Gasto {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String descripcion;
    public double cantidad;
    public String categoria;
    public String fecha;

    public Gasto(String descripcion, double cantidad, String categoria, String fecha) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.categoria = categoria;
        this.fecha = fecha;
    }
}
