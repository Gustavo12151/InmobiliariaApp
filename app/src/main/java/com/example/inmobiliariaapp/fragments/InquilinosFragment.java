package com.example.inmobiliariaapp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.inmobiliariaapp.R;
import com.example.inmobiliariaapp.adapters.InquilinoAdapter;
import com.example.inmobiliariaapp.models.Inmueble;
import com.example.inmobiliariaapp.network.ApiClient;
import com.example.inmobiliariaapp.network.ApiService;
import com.example.inmobiliariaapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InquilinosFragment extends Fragment {

    private RecyclerView rv;
    private InquilinoAdapter adapter;
    private ApiService apiService;
    private SessionManager sessionManager;
    private List<Inmueble> lista = new ArrayList<>();

    public InquilinosFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inquilinos, container, false);

        rv = view.findViewById(R.id.rvInquilinos);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(requireContext());

        cargarInmueblesAlquilados();

        return view;
    }

    private void cargarInmueblesAlquilados() {
        String token = "Bearer " + sessionManager.getToken();

        apiService.getInmueblesConContrato(token).enqueue(new Callback<List<Inmueble>>() {
            @Override
            public void onResponse(Call<List<Inmueble>> call, Response<List<Inmueble>> response) {
                if (response.isSuccessful()) {
                    lista = response.body();
                    adapter = new InquilinoAdapter(lista, inmueble -> abrirDetalleInquilino(inmueble));
                    rv.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "No hay inmuebles alquilados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Inmueble>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirDetalleInquilino(Inmueble inmueble) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("inmueble", inmueble);

        DetalleInquilinoFragment fragment = new DetalleInquilinoFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
