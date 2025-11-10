package com.example.inmobiliariaapp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.inmobiliariaapp.R;
import com.example.inmobiliariaapp.models.Inmueble;

public class DetalleInmuebleFragment extends Fragment {

    private TextView tvCodigo, tvDireccion, tvUso, tvTipo, tvAmbientes, tvPrecio;
    private CheckBox chkDisponible;
    private ImageView imgInmueble;

    private Inmueble inmueble;

    public DetalleInmuebleFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detalle_inmueble, container, false);

        tvCodigo = view.findViewById(R.id.tvCodigo);
        tvDireccion = view.findViewById(R.id.tvDireccion);
        tvUso = view.findViewById(R.id.tvUso);
        tvTipo = view.findViewById(R.id.tvTipo);
        tvAmbientes = view.findViewById(R.id.tvAmbientes);
        tvPrecio = view.findViewById(R.id.tvPrecio);

        chkDisponible = view.findViewById(R.id.chkDisponible);
        imgInmueble = view.findViewById(R.id.imgInmueble);

        // ✅ Recibir inmueble desde el bundle
        if (getArguments() != null) {
            inmueble = (Inmueble) getArguments().getSerializable("inmueble");
            mostrarDatos();
        } else {
            Toast.makeText(getContext(), "No se pudo cargar el inmueble", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void mostrarDatos() {
        tvCodigo.setText(String.valueOf(inmueble.getIdInmueble()));
        tvDireccion.setText(inmueble.getDireccion());
        tvUso.setText(inmueble.getUso());
        tvTipo.setText(inmueble.getTipo());
        tvAmbientes.setText(String.valueOf(inmueble.getAmbientes()));
        tvPrecio.setText("$ " + inmueble.getPrecio());

        chkDisponible.setChecked(inmueble.isDisponible());

        // ✅ Cargar imagen si existe
        if (inmueble.getImagenUrl() != null && !inmueble.getImagenUrl().isEmpty()) {

            String fullUrl = "https://inmobiliariaulp-amb5hwfqaraweyga.canadacentral-01.azurewebsites.net"
                    + inmueble.getImagenUrl();

            Glide.with(requireContext())
                    .load(fullUrl)
                    .placeholder(R.drawable.placeholder_house)
                    .error(R.drawable.placeholder_house)
                    .into(imgInmueble);

        } else {
            imgInmueble.setImageResource(R.drawable.placeholder_house);
        }
    }
}
