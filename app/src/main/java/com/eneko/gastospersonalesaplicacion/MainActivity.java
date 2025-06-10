package com.eneko.gastospersonalesaplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.eneko.gastospersonalesaplicacion.activities.AddEditActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.eneko.gastospersonalesaplicacion.adapters.GastoAdapter;
import com.eneko.gastospersonalesaplicacion.database.AppDatabase;
import com.eneko.gastospersonalesaplicacion.model.Gasto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerGastos;
    private GastoAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "gastos-db")
                .allowMainThreadQueries()
                .build();

        recyclerGastos = findViewById(R.id.recyclerGastos);
        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregar);
        FloatingActionButton fabExportar = findViewById(R.id.fabExportar); // <- asegúrate que esté en tu layout

        recyclerGastos.setLayoutManager(new LinearLayoutManager(this));
        List<Gasto> gastos = db.gastoDao().getAll();
        adapter = new GastoAdapter(gastos);
        recyclerGastos.setAdapter(adapter);

        fabAgregar.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            startActivity(intent);
        });

        fabExportar.setOnClickListener(view -> {
            List<Gasto> gastosParaExportar = db.gastoDao().getAll();
            exportarGastosComoTxt(gastosParaExportar);
        });

        adapter.setOnItemLongClickListener(gasto -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Eliminar gasto")
                    .setMessage("¿Seguro que quieres eliminar este gasto?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        db.gastoDao().delete(gasto);
                        List<Gasto> gastosActualizados = db.gastoDao().getAll();
                        adapter.setListaGastos(gastosActualizados);
                        Toast.makeText(MainActivity.this, "Gasto eliminado", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }


    private void exportarGastosComoTxt(List<Gasto> gastos) {
        StringBuilder data = new StringBuilder();
        for (Gasto g : gastos) {
            data.append("Descripción: ").append(g.descripcion).append("\n");
            data.append("Cantidad: ").append(g.cantidad).append(" €\n");
            data.append("Categoría: ").append(g.categoria).append("\n");
            data.append("Fecha: ").append(g.fecha).append("\n\n");
        }

        File archivo = new File(getExternalFilesDir(null), "gastos_exportados.txt");

        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            fos.write(data.toString().getBytes());
            Toast.makeText(this, "Exportado a: " + archivo.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al exportar", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        List<Gasto> gastosActualizados = db.gastoDao().getAll();
        adapter.setListaGastos(gastosActualizados);
    }
}
