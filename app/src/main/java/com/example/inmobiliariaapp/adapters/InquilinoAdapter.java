package com.example.inmobiliariaapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmobiliariaapp.R;
import com.example.inmobiliariaapp.models.Inmueble;
import com.example.inmobiliariaapp.models.Inquilino;
import com.example.inmobiliariaapp.models.Contrato;

import java.util.List;

public class InquilinoAdapter extends RecyclerView.Adapter<InquilinoAdapter.ViewHolder> {

    private List<Inmueble> lista;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(Inmueble inmueble);
    }

    public InquilinoAdapter(List<Inmueble> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inquilino, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inmueble inmueble = lista.get(position);

        // ✅ Obtener contrato vigente
        Contrato contrato = inmueble.getContrato();

        // ✅ Evitar crash si no hay contrato
        if (contrato != null) {
            Inquilino inq = contrato.getInquilino();
            if (inq != null) {
                holder.tvNombre.setText(inq.getNombre() + " " + inq.getApellido());
            } else {
                holder.tvNombre.setText("Sin inquilino asignado");
            }
        } else {
            holder.tvNombre.setText("Sin contrato");
        }

        holder.tvDireccion.setText(inmueble.getDireccion());
        holder.tvPrecio.setText("$ " + inmueble.getPrecio());

        holder.card.setOnClickListener(v -> listener.onClick(inmueble));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView card;
        TextView tvNombre, tvDireccion, tvPrecio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.cardInquilino);
            tvNombre = itemView.findViewById(R.id.tvNombreInquilino);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
        }
    }
}
