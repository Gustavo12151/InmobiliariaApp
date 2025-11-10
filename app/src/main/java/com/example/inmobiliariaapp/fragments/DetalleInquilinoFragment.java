package com.example.inmobiliariaapp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inmobiliariaapp.R;
import com.example.inmobiliariaapp.models.Contrato;
import com.example.inmobiliariaapp.models.Inmueble;
import com.example.inmobiliariaapp.models.Inquilino;
import com.example.inmobiliariaapp.network.ApiClient;
import com.example.inmobiliariaapp.network.ApiService;
import com.example.inmobiliariaapp.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleInquilinoFragment extends Fragment {

    private TextView tvNombre, tvDni, tvTelefono, tvEmail;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detalle_inquilino, container, false);

        tvNombre = view.findViewById(R.id.tvNombre);
        tvDni = view.findViewById(R.id.tvDni);
        tvTelefono = view.findViewById(R.id.tvTelefono);
        tvEmail = view.findViewById(R.id.tvEmail);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(requireContext());

        Bundle bundle = getArguments();
        if (bundle != null) {
            Inmueble inmueble = (Inmueble) bundle.getSerializable("inmueble");
            if (inmueble != null) {
                cargarInquilino(inmueble.getIdInmueble());
            }
        }

        return view;
    }

    private void cargarInquilino(int idInmueble) {

        String token = "Bearer " + sessionManager.getToken();

        apiService.getContratoPorInmueble(token, idInmueble).enqueue(new Callback<Contrato>() {
            @Override
            public void onResponse(Call<Contrato> call, Response<Contrato> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Inquilino inq = response.body().getInquilino();

                    tvNombre.setText(inq.getNombre() + " " + inq.getApellido());
                    tvDni.setText(String.valueOf(inq.getDni()));
                    tvTelefono.setText(inq.getTelefono());
                    tvEmail.setText(inq.getEmail());

                } else {
                    Toast.makeText(getContext(), "No se encontró inquilino", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Contrato> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
