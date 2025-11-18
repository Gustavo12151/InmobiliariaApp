package com.example.inmobiliariaapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.inmobiliariaapp.R;
import com.example.inmobiliariaapp.network.ApiClient;
import com.example.inmobiliariaapp.network.ApiService;
import com.example.inmobiliariaapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CambiarClaveFragment extends Fragment {

    private TextInputEditText etClaveActual, etClaveNueva, etClaveRepetir;
    private Button btnGuardar;

    ApiService apiService;
    SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cambiar_clave, container, false);

        etClaveActual = view.findViewById(R.id.etClaveActual);
        etClaveNueva = view.findViewById(R.id.etClaveNueva);
        etClaveRepetir = view.findViewById(R.id.etClaveRepetir);
        btnGuardar = view.findViewById(R.id.btnGuardarClave);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(requireContext());

        btnGuardar.setOnClickListener(v -> cambiarClave());

        return view;
    }

    private void cambiarClave() {
        String actual = etClaveActual.getText().toString().trim();
        String nueva = etClaveNueva.getText().toString().trim();
        String repetir = etClaveRepetir.getText().toString().trim();

        if (actual.isEmpty() || nueva.isEmpty() || repetir.isEmpty()) {
            Toast.makeText(getContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nueva.equals(repetir)) {
            Toast.makeText(getContext(), "Las contraseñas nuevas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nueva.length() < 4) {
            Toast.makeText(getContext(), "La nueva clave debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = "Bearer " + sessionManager.getToken();

        apiService.cambiarClave(token, actual, nueva)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Clave cambiada correctamente", Toast.LENGTH_SHORT).show();
                            volverAtras();
                        } else {
                            Toast.makeText(getContext(), "Clave actual incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void volverAtras() {
        requireActivity().getSupportFragmentManager()
                .popBackStack();
    }
}
