package com.eneko.gastospersonalesaplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.eneko.gastospersonalesaplicacion.activities.AddEditActivity;
import com.eneko.gastospersonalesaplicacion.activities.SettingsActivity;
import com.eneko.gastospersonalesaplicacion.adapters.GastoAdapter;
import com.eneko.gastospersonalesaplicacion.database.AppDatabase;
import com.eneko.gastospersonalesaplicacion.model.Gasto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerGastos;
    private GastoAdapter adapter;
    private AppDatabase db;
    private TextView textSaldo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "gastos-db")
                .allowMainThreadQueries()
                .build();

        textSaldo = findViewById(R.id.textSaldo);
        recyclerGastos = findViewById(R.id.recyclerGastos);
        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregar);
        FloatingActionButton fabExportar = findViewById(R.id.fabExportar);

        recyclerGastos.setLayoutManager(new LinearLayoutManager(this));
        List<Gasto> gastos = db.gastoDao().getAll();
        adapter = new GastoAdapter(gastos);
        recyclerGastos.setAdapter(adapter);

        actualizarSaldo();

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
                    .setTitle(getString(R.string.eliminar))
                    .setMessage(getString(R.string.confirmar_eliminar))
                    .setPositiveButton(getString(R.string.si), (dialog, which) -> {
                        db.gastoDao().delete(gasto);
                        List<Gasto> gastosActualizados = db.gastoDao().getAll();
                        adapter.setListaGastos(gastosActualizados);
                        actualizarSaldo();
                        Toast.makeText(MainActivity.this, getString(R.string.gasto_eliminado), Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(getString(R.string.cancelar), null)
                    .show();
            return true;
        });
    }

    private void actualizarSaldo() {
        double ingresos = db.gastoDao().getSumaPorTipo("ingreso") != null ? db.gastoDao().getSumaPorTipo("ingreso") : 0;
        double gastos = db.gastoDao().getSumaPorTipo("gasto") != null ? db.gastoDao().getSumaPorTipo("gasto") : 0;
        double saldo = ingresos - gastos;

        String saldoFormateado = String.format(Locale.getDefault(), "%.2f €", saldo);
        textSaldo.setText(getString(R.string.saldo_total, saldoFormateado));
    }

    private void exportarGastosComoTxt(List<Gasto> gastos) {
        StringBuilder data = new StringBuilder();
        for (Gasto g : gastos) {
            data.append(getString(R.string.tipo_gasto)).append(": ").append(g.tipo).append("\n");
            data.append(getString(R.string.descripcion)).append(": ").append(g.descripcion).append("\n");
            data.append(getString(R.string.cantidad)).append(": ").append(g.cantidad).append(" €\n");
            data.append(getString(R.string.categoria)).append(": ").append(g.categoria).append("\n");
            data.append(getString(R.string.fecha)).append(": ").append(g.fecha).append("\n\n");
        }

        File archivo = new File(getExternalFilesDir(null), "gastos_exportados.txt");

        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            fos.write(data.toString().getBytes());
            Toast.makeText(this, getString(R.string.exportado_en, archivo.getAbsolutePath()), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error_exportar), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_ajustes) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Gasto> gastosActualizados = db.gastoDao().getAll();
        adapter.setListaGastos(gastosActualizados);
        actualizarSaldo();
    }
}
