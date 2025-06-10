package com.eneko.gastospersonalesaplicacion.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.eneko.gastospersonalesaplicacion.model.Gasto;

@Database(entities = {Gasto.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GastoDao gastoDao();
}
