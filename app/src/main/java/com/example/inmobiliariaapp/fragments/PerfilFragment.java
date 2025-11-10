package com.example.inmobiliariaapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.inmobiliariaapp.R;
import com.example.inmobiliariaapp.activities.CambiarClaveActivity;
import com.example.inmobiliariaapp.models.Propietario;
import com.example.inmobiliariaapp.network.ApiClient;
import com.example.inmobiliariaapp.network.ApiService;
import com.example.inmobiliariaapp.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    private EditText etNombre, etApellido, etEmail, etTelefono, etDni;
    private Button btnEditarGuardar, btnCambiarClave;
    private boolean modoEdicion = false;

    private ApiService apiService;
    private SessionManager sessionManager;

    private Propietario propietarioActual;

    public PerfilFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        etNombre = view.findViewById(R.id.etNombre);
        etApellido = view.findViewById(R.id.etApellido);
        etEmail = view.findViewById(R.id.etEmail);
        etTelefono = view.findViewById(R.id.etTelefono);
        etDni = view.findViewById(R.id.etDni);

        btnEditarGuardar = view.findViewById(R.id.btnEditarGuardar);
        btnCambiarClave = view.findViewById(R.id.btnCambiarClave);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(requireContext());

        cargarPerfil();

        btnEditarGuardar.setOnClickListener(v -> {
            if (!modoEdicion) {
                habilitarEdicion(true);
            } else {
                guardarCambios();
            }
        });

        btnCambiarClave.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), CambiarClaveActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void cargarPerfil() {
        String token = "Bearer " + sessionManager.getToken();
        apiService.getPerfil(token).enqueue(new Callback<Propietario>() {
            @Override
            public void onResponse(Call<Propietario> call, Response<Propietario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    propietarioActual = response.body();
                    mostrarDatos(propietarioActual);
                } else {
                    Toast.makeText(getContext(), "Error al obtener perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Propietario> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDatos(Propietario p) {
        etNombre.setText(p.getNombre());
        etApellido.setText(p.getApellido());
        etEmail.setText(p.getEmail());
        etTelefono.setText(p.getTelefono());
        etDni.setText(p.getDni());

        habilitarEdicion(false);
    }

    private void habilitarEdicion(boolean habilitar) {
        modoEdicion = habilitar;

        etNombre.setEnabled(habilitar);
        etApellido.setEnabled(habilitar);
        etEmail.setEnabled(habilitar);
        etTelefono.setEnabled(habilitar);

        btnEditarGuardar.setText(habilitar ? "Guardar" : "Editar");
    }

    private void guardarCambios() {
        propietarioActual.setNombre(etNombre.getText().toString());
        propietarioActual.setApellido(etApellido.getText().toString());
        propietarioActual.setEmail(etEmail.getText().toString());
        propietarioActual.setTelefono(etTelefono.getText().toString());

        String token = "Bearer " + sessionManager.getToken();
        apiService.actualizarPerfil(token, propietarioActual).enqueue(new Callback<Propietario>() {
            @Override
            public void onResponse(Call<Propietario> call, Response<Propietario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    habilitarEdicion(false);
                } else {
                    Toast.makeText(getContext(), "Error al guardar cambios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Propietario> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
