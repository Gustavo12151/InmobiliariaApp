package com.example.inmobiliariaapp.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.inmobiliariaapp.R;
import com.example.inmobiliariaapp.adapters.PagoAdapter;
import com.example.inmobiliariaapp.models.Pago;
import com.example.inmobiliariaapp.network.ApiClient;
import com.example.inmobiliariaapp.network.ApiService;
import com.example.inmobiliariaapp.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagosFragment extends Fragment {

    private RecyclerView rv;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pagos, container, false);

        rv = view.findViewById(R.id.rvPagos);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(requireContext());

        if (getArguments() != null) {
            int idContrato = getArguments().getInt("idContrato");
            cargarPagos(idContrato);
        }

        return view;
    }

    private void cargarPagos(int idContrato) {
        String token = "Bearer " + sessionManager.getToken();

        apiService.getPagosPorContrato(token, idContrato).enqueue(new Callback<List<Pago>>() {
            @Override
            public void onResponse(Call<List<Pago>> call, Response<List<Pago>> response) {
                if (response.isSuccessful()) {
                    rv.setAdapter(new PagoAdapter(response.body()));
                } else {
                    Toast.makeText(getContext(), "No hay pagos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pago>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
