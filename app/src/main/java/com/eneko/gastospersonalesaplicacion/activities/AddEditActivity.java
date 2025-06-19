package com.eneko.gastospersonalesaplicacion.activities;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.room.Room;

import com.eneko.gastospersonalesaplicacion.R;
import com.eneko.gastospersonalesaplicacion.database.AppDatabase;
import com.eneko.gastospersonalesaplicacion.model.Gasto;
import com.google.android.material.textfield.TextInputEditText;

import android.app.DatePickerDialog;
import java.util.Calendar;
import java.util.Locale;

public class AddEditActivity extends AppCompatActivity {

    private TextInputEditText editDescripcion, editCantidad, editFecha;
    private Spinner spinnerCategoria;
    private RadioGroup radioTipo;
    private AppDatabase db;
    private Gasto gastoExistente = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        crearCanalNotificaciones();
        solicitarPermisoNotificaciones();

        editDescripcion = findViewById(R.id.editDescripcion);
        editCantidad = findViewById(R.id.editCantidad);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        editFecha = findViewById(R.id.editFecha);
        radioTipo = findViewById(R.id.radioTipo);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categorias_predefinidas,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);

        editFecha.setFocusable(false);
        editFecha.setOnClickListener(v -> mostrarSelectorFecha());

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "gastos-db")
                .allowMainThreadQueries()
                .build();

        gastoExistente = (Gasto) getIntent().getSerializableExtra("gasto");
        if (gastoExistente != null) {
            rellenarCamposParaEditar();
        }

        btnGuardar.setOnClickListener(view -> {
            if (gastoExistente != null) {
                confirmarYActualizar();
            } else {
                guardarNuevoGasto();
            }
        });
    }

    private void rellenarCamposParaEditar() {
        editDescripcion.setText(gastoExistente.descripcion);
        editCantidad.setText(String.valueOf(gastoExistente.cantidad));
        editFecha.setText(gastoExistente.fecha);

        String[] categorias = getResources().getStringArray(R.array.categorias_predefinidas);
        for (int i = 0; i < categorias.length; i++) {
            if (categorias[i].equalsIgnoreCase(gastoExistente.categoria)) {
                spinnerCategoria.setSelection(i);
                break;
            }
        }

        if (gastoExistente.tipo.equals("ingreso")) {
            radioTipo.check(R.id.radioIngreso);
        } else {
            radioTipo.check(R.id.radioGasto);
        }
    }

    private void guardarNuevoGasto() {
        Gasto nuevo = construirGastoDesdeInputs();
        if (nuevo == null) return;

        db.gastoDao().insert(nuevo);
        mostrarNotificacion(getString(R.string.notificacion_guardado_titulo),
                getString(R.string.notificacion_guardado_mensaje, nuevo.tipo));
        Toast.makeText(this, getString(R.string.gasto_guardado), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmarYActualizar() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirmar_cambio_idioma)
                .setMessage(R.string.confirmar_modificacion)
                .setPositiveButton(R.string.si, (dialog, which) -> {
                    Gasto actualizado = construirGastoDesdeInputs();
                    if (actualizado == null) return;
                    actualizado.id = gastoExistente.id;
                    db.gastoDao().update(actualizado);
                    Toast.makeText(this, R.string.gasto_modificado, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    private Gasto construirGastoDesdeInputs() {
        String descripcion = editDescripcion.getText().toString().trim();
        String cantidadStr = editCantidad.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String fecha = editFecha.getText().toString().trim();

        int selectedTipoId = radioTipo.getCheckedRadioButtonId();
        if (TextUtils.isEmpty(descripcion) || TextUtils.isEmpty(cantidadStr)
                || TextUtils.isEmpty(categoria) || TextUtils.isEmpty(fecha) || selectedTipoId == -1) {
            Toast.makeText(this, R.string.completa_campos, Toast.LENGTH_SHORT).show();
            return null;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.cantidad_invalida, Toast.LENGTH_SHORT).show();
            return null;
        }

        RadioButton selectedTipo = findViewById(selectedTipoId);
        String tipo = selectedTipo.getId() == R.id.radioIngreso ? "ingreso" : "gasto";

        return new Gasto(descripcion, cantidad, categoria, fecha, tipo);
    }

    private void crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String canalID = "canal_gastos";
            CharSequence nombre = getString(R.string.canal_nombre);
            String descripcion = getString(R.string.canal_descripcion);
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel canal = new NotificationChannel(canalID, nombre, importancia);
            canal.setDescription(descripcion);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(canal);
            }
        }
    }

    private void mostrarNotificacion(String titulo, String mensaje) {
        String canalID = "canal_gastos";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canalID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1001, builder.build());
        }
    }

    private void solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }

    private void mostrarSelectorFecha() {
        Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialogoFecha = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String fechaFormateada = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            dayOfMonth, month + 1, year);
                    editFecha.setText(fechaFormateada);
                },
                año, mes, dia);

        dialogoFecha.show();
    }
}