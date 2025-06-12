package com.eneko.gastospersonalesaplicacion.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eneko.gastospersonalesaplicacion.R;
import com.eneko.gastospersonalesaplicacion.model.Gasto;

import java.util.List;
import java.util.Locale;

public class GastoAdapter extends RecyclerView.Adapter<GastoAdapter.GastoViewHolder> {

    private List<Gasto> listaGastos;
    private Context context;

    public GastoAdapter(List<Gasto> listaGastos) {
        this.listaGastos = listaGastos;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Gasto gasto);
    }

    private OnItemLongClickListener longClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public GastoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext(); // Guardamos el contexto para getString
        View vista = LayoutInflater.from(context).inflate(R.layout.item_gasto, parent, false);
        return new GastoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull GastoViewHolder holder, int position) {
        Gasto gasto = listaGastos.get(position);

        holder.textDescripcion.setText(gasto.descripcion);
        holder.textCantidad.setText(String.format(Locale.getDefault(), "%.2f €", gasto.cantidad));
        holder.textCategoria.setText(context.getString(R.string.categoria) + ": " + gasto.categoria);
        holder.textFecha.setText(context.getString(R.string.fecha) + ": " + gasto.fecha);

        // Mostrar el tipo (Ingreso o Gasto)
        holder.textTipo.setText(context.getString(R.string.tipo_gasto) + ": " +
                (gasto.tipo.equals("ingreso") ? context.getString(R.string.ingreso) : context.getString(R.string.gasto)));

        // Cambiar color según tipo
        if (gasto.tipo.equals("ingreso")) {
            holder.textCantidad.setTextColor(Color.parseColor("#2e7d32")); // verde
        } else {
            holder.textCantidad.setTextColor(Color.parseColor("#c62828")); // rojo
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(gasto);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaGastos.size();
    }

    public void setListaGastos(List<Gasto> nuevosGastos) {
        this.listaGastos = nuevosGastos;
        notifyDataSetChanged();
    }

    static class GastoViewHolder extends RecyclerView.ViewHolder {
        TextView textDescripcion, textCantidad, textCategoria, textFecha, textTipo;

        public GastoViewHolder(@NonNull View itemView) {
            super(itemView);
            textDescripcion = itemView.findViewById(R.id.textDescripcion);
            textCantidad = itemView.findViewById(R.id.textCantidad);
            textCategoria = itemView.findViewById(R.id.textCategoria);
            textFecha = itemView.findViewById(R.id.textFecha);
            textTipo = itemView.findViewById(R.id.textTipo); // <- asegúrate de tener este en item_gasto.xml
        }
    }
}
