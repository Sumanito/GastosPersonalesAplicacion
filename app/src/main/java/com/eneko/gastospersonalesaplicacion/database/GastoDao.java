package com.eneko.gastospersonalesaplicacion.database;

import androidx.room.*;
import com.eneko.gastospersonalesaplicacion.model.Gasto;
import java.util.List;

@Dao
public interface GastoDao {
    @Query("SELECT * FROM Gasto ORDER BY id DESC")
    List<Gasto> getAll();

    @Insert
    void insert(Gasto gasto);

    @Delete
    void delete(Gasto gasto);

    @Update
    void update(Gasto gasto);

    @Query("SELECT SUM(cantidad) FROM Gasto WHERE tipo = :tipo")
    Double getSumaPorTipo(String tipo);
}
