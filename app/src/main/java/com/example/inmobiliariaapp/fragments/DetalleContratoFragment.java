package com.example.inmobiliariaapp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inmobiliariaapp.R;
import com.example.inmobiliariaapp.models.Contrato;
import com.example.inmobiliariaapp.models.Inquilino;
import com.example.inmobiliariaapp.models.Inmueble;
import com.example.inmobiliariaapp.network.ApiClient;
import com.example.inmobiliariaapp.network.ApiService;
import com.example.inmobiliariaapp.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleContratoFragment extends Fragment {

    private TextView tvCodigo, tvFechaInicio, tvFechaFin, tvMonto, tvInquilino, tvInmueble;
    private Button btnPagos;
    private ApiService apiService;
    private SessionManager sessionManager;
    private Inmueble inmueble;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detalle_contrato, container, false);

        // Referencias a vistas
        tvCodigo = view.findViewById(R.id.tvCodigoContrato);
        tvFechaInicio = view.findViewById(R.id.tvFechaInicio);
        tvFechaFin = view.findViewById(R.id.tvFechaFin);
        tvMonto = view.findViewById(R.id.tvMonto);
        tvInquilino = view.findViewById(R.id.tvInquilino);
        tvInmueble = view.findViewById(R.id.tvInmueble);
        btnPagos = view.findViewById(R.id.btnPagos);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(requireContext());

        if (getArguments() != null) {
            inmueble = (Inmueble) getArguments().getSerializable("inmueble");
            cargarContrato(inmueble.getIdInmueble());
        }

        return view;
    }

    private void cargarContrato(int idInmueble) {
        String token = "Bearer " + sessionManager.getToken();

        apiService.getContratoPorInmueble(token, idInmueble).enqueue(new Callback<Contrato>() {
            @Override
            public void onResponse(Call<Contrato> call, Response<Contrato> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mostrarDatos(response.body());
                } else {
                    Toast.makeText(getContext(), "El inmueble no tiene contrato vigente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Contrato> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDatos(Contrato c) {
        tvCodigo.setText(String.valueOf(c.getIdContrato()));
        tvFechaInicio.setText(c.getFechaInicio());
        tvFechaFin.setText(c.getFechaFin());
        tvMonto.setText("$" + c.getMonto());

        Inquilino inq = c.getInquilino();
        if (inq != null) {
            tvInquilino.setText(inq.getNombre() + " " + inq.getApellido());
        }

        tvInmueble.setText("Inmueble en " + inmueble.getDireccion());

        // 🔹 Botón PAGOS → abrir fragment de pagos
        btnPagos.setOnClickListener(v -> abrirPagos(c.getIdContrato()));
    }

    private void abrirPagos(int idContrato) {
        Bundle bundle = new Bundle();
        bundle.putInt("idContrato", idContrato);

        PagosFragment fragment = new PagosFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
