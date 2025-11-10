package com.example.inmobiliariaapp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.inmobiliariaapp.R;
import com.example.inmobiliariaapp.adapters.InmuebleAdapter;
import com.example.inmobiliariaapp.models.Inmueble;
import com.example.inmobiliariaapp.network.ApiClient;
import com.example.inmobiliariaapp.network.ApiService;
import com.example.inmobiliariaapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContratosFragment extends Fragment {

    private RecyclerView rv;
    private InmuebleAdapter adapter;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inmuebles, container, false);

        rv = view.findViewById(R.id.rvInmuebles);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(requireContext());

        cargarContratos();

        return view;
    }

    private void cargarContratos() {
        String token = "Bearer " + sessionManager.getToken();

        apiService.getInmueblesConContrato(token).enqueue(new Callback<List<Inmueble>>() {
            @Override
            public void onResponse(Call<List<Inmueble>> call, Response<List<Inmueble>> response) {
                if (response.isSuccessful()) {
                    adapter = new InmuebleAdapter(response.body(), inmueble -> abrirDetalleContrato(inmueble));
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Inmueble>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirDetalleContrato(Inmueble inmueble) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("inmueble", inmueble);

        DetalleContratoFragment fragment = new DetalleContratoFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
