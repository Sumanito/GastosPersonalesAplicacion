package com.eneko.gastospersonalesaplicacion.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Gasto implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String descripcion;
    public double cantidad;
    public String categoria;
    public String fecha;
    public String tipo;

    public Gasto(String descripcion, double cantidad, String categoria, String fecha, String tipo) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.categoria = categoria;
        this.fecha = fecha;
        this.tipo = tipo;
    }
}
