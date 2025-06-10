package com.eneko.gastospersonalesaplicacion.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.room.Room;

import com.eneko.gastospersonalesaplicacion.R;
import com.eneko.gastospersonalesaplicacion.database.AppDatabase;
import com.eneko.gastospersonalesaplicacion.model.Gasto;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditActivity extends AppCompatActivity {

    private TextInputEditText editDescripcion, editCantidad, editCategoria, editFecha;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        crearCanalNotificaciones();
        solicitarPermisoNotificaciones();

        editDescripcion = findViewById(R.id.editDescripcion);
        editCantidad = findViewById(R.id.editCantidad);
        editCategoria = findViewById(R.id.editCategoria);
        editFecha = findViewById(R.id.editFecha);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "gastos-db")
                .allowMainThreadQueries()
                .build();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarGasto();
            }
        });
    }

    private void guardarGasto() {
        String descripcion = editDescripcion.getText().toString().trim();
        String cantidadStr = editCantidad.getText().toString().trim();
        String categoria = editCategoria.getText().toString().trim();
        String fecha = editFecha.getText().toString().trim();

        if (TextUtils.isEmpty(descripcion) || TextUtils.isEmpty(cantidadStr) ||
                TextUtils.isEmpty(categoria) || TextUtils.isEmpty(fecha)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        Gasto nuevoGasto = new Gasto(descripcion, cantidad, categoria, fecha);
        db.gastoDao().insert(nuevoGasto);

        mostrarNotificacion("Nuevo gasto guardado", "Se ha registrado correctamente.");

        // 3. Toast y cerrar actividad
        Toast.makeText(this, "Gasto guardado", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void crearCanalNotificaciones() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String canalID = "canal_gastos";
            CharSequence nombre = "Notificaciones de Gastos";
            String descripcion = "Canal para notificaciones cuando se añade un gasto";
            int importancia = android.app.NotificationManager.IMPORTANCE_DEFAULT;

            android.app.NotificationChannel canal = new android.app.NotificationChannel(canalID, nombre, importancia);
            canal.setDescription(descripcion);

            android.app.NotificationManager notificationManager = getSystemService(android.app.NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(canal);
            }
        }
    }
    private void mostrarNotificacion(String titulo, String mensaje) {
        String canalID = "canal_gastos";

        // Crear canal (por si aún no se ha creado)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence nombre = "Gastos";
            String descripcion = "Notificaciones al guardar gastos";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel canal = new NotificationChannel(canalID, nombre, importancia);
            canal.setDescription(descripcion);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(canal);
            }
        }

        // Crear notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canalID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // usa un icono de tu drawable
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU ||
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1001, builder.build());
        }
    }
    private void solicitarPermisoNotificaciones() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }


}
