package com.eneko.gastospersonalesaplicacion.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.eneko.gastospersonalesaplicacion.R;
import com.eneko.gastospersonalesaplicacion.model.Gasto;

import java.util.List;

public class GastoAdapter extends RecyclerView.Adapter<GastoAdapter.GastoViewHolder> {

    private List<Gasto> listaGastos;

    public GastoAdapter(List<Gasto> listaGastos) {
        this.listaGastos = listaGastos;
    }

    // Interfaz para clic largo
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
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gasto, parent, false);
        return new GastoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull GastoViewHolder holder, int position) {
        Gasto gasto = listaGastos.get(position);
        holder.textDescripcion.setText(gasto.descripcion);
        holder.textCantidad.setText(String.format("%.2f €", gasto.cantidad));
        holder.textCategoria.setText("Categoría: " + gasto.categoria);
        holder.textFecha.setText(gasto.fecha);

        // Manejo del clic largo
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
        TextView textDescripcion, textCantidad, textCategoria, textFecha;

        public GastoViewHolder(@NonNull View itemView) {
            super(itemView);
            textDescripcion = itemView.findViewById(R.id.textDescripcion);
            textCantidad = itemView.findViewById(R.id.textCantidad);
            textCategoria = itemView.findViewById(R.id.textCategoria);
            textFecha = itemView.findViewById(R.id.textFecha);
        }
    }
}
