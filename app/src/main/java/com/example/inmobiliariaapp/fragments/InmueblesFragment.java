package com.example.inmobiliariaapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmobiliariaapp.R;
import com.example.inmobiliariaapp.adapters.InmuebleAdapter;
import com.example.inmobiliariaapp.models.Inmueble;
import com.example.inmobiliariaapp.network.ApiClient;
import com.example.inmobiliariaapp.network.ApiService;
import com.example.inmobiliariaapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InmueblesFragment extends Fragment {

    private RecyclerView rv;
    private FloatingActionButton btnAgregar;
    private InmuebleAdapter adapter;
    private ApiService apiService;
    private SessionManager sessionManager;
    private List<Inmueble> lista = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inmuebles, container, false);

        rv = view.findViewById(R.id.rvInmuebles);
        btnAgregar = view.findViewById(R.id.btnAgregarInmueble);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(requireContext());

        // Mostrar inmuebles en grid de 2 columnas
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        cargarInmuebles();

        // NAV → Abrir pantalla para agregar inmueble
        btnAgregar.setOnClickListener(v -> abrirAgregarInmueble());

        return view;
    }

    private void cargarInmuebles() {
        String token = "Bearer " + sessionManager.getToken();

        apiService.obtenerInmuebles(token).enqueue(new Callback<List<Inmueble>>() {
            @Override
            public void onResponse(Call<List<Inmueble>> call, Response<List<Inmueble>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lista = response.body();
                    adapter = new InmuebleAdapter(lista, inmueble -> abrirDetalle(inmueble));
                    rv.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "No se pudieron obtener los inmuebles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Inmueble>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión con la API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirDetalle(Inmueble inmueble) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("inmueble", inmueble);

        DetalleInmuebleFragment fragment = new DetalleInmuebleFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void abrirAgregarInmueble() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AgregarInmuebleFragment())
                .addToBackStack(null)
                .commit();
    }
}
