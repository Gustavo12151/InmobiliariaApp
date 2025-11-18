package com.example.inmobiliariaapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.inmobiliariaapp.R;
import com.example.inmobiliariaapp.models.Inmueble;
import com.example.inmobiliariaapp.network.ApiClient;
import com.example.inmobiliariaapp.network.ApiService;
import com.example.inmobiliariaapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarInmuebleFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 100;

    private Uri imagenUri = null;

    private ImageView imgPreview;
    private TextInputEditText etDireccion, etUso, etTipo, etAmbientes, etPrecio;
    private Button btnSeleccionarImagen, btnGuardar;

    ApiService apiService;
    SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agregar_inmueble, container, false);

        imgPreview = view.findViewById(R.id.imgPreview);
        etDireccion = view.findViewById(R.id.etDireccion);
        etUso = view.findViewById(R.id.etUso);
        etTipo = view.findViewById(R.id.etTipo);
        etAmbientes = view.findViewById(R.id.etAmbientes);
        etPrecio = view.findViewById(R.id.etPrecio);

        btnSeleccionarImagen = view.findViewById(R.id.btnSeleccionarImagen);
        btnGuardar = view.findViewById(R.id.btnGuardarInmueble);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(requireContext());

        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());
        btnGuardar.setOnClickListener(v -> guardarInmueble());

        return view;
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            imagenUri = data.getData();
            imgPreview.setImageURI(imagenUri);
        }
    }

    private void guardarInmueble() {
        if (imagenUri == null) {
            Toast.makeText(getContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String direccion = etDireccion.getText().toString();
            String uso = etUso.getText().toString();
            String tipo = etTipo.getText().toString();
            int ambientes = Integer.parseInt(etAmbientes.getText().toString());
            double precio = Double.parseDouble(etPrecio.getText().toString());

            if (direccion.isEmpty() || uso.isEmpty() || tipo.isEmpty()) {
                Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear JSON correcto según tu API
            JSONObject json = new JSONObject();
            json.put("direccion", direccion);
            json.put("uso", uso);
            json.put("tipo", tipo);
            json.put("ambientes", ambientes);
            json.put("precio", precio);
            json.put("disponible", false); // por defecto deshabilitado

            // Convertir JSON a RequestBody
            RequestBody inmuebleBody =
                    RequestBody.create(MediaType.parse("application/json"), json.toString());

            // Convertir imagen
            String path = obtenerPath(imagenUri);
            File file = new File(path);

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/*"), file);

            MultipartBody.Part imagenPart =
                    MultipartBody.Part.createFormData("imagen", file.getName(), requestFile);

            String token = "Bearer " + sessionManager.getToken();

            apiService.cargarInmueble(token, imagenPart, inmuebleBody)
                    .enqueue(new Callback<Inmueble>() {
                        @Override
                        public void onResponse(Call<Inmueble> call, Response<Inmueble> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Inmueble cargado", Toast.LENGTH_SHORT).show();
                                volverAListado();
                            } else {
                                Toast.makeText(getContext(), "Error al cargar inmueble", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Inmueble> call, Throwable t) {
                            Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Datos inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void volverAListado() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new InmueblesFragment())
                .commit();
    }

    // Ruta real de imagen desde la galería
    private String obtenerPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireActivity().getContentResolver().query(uri, projection,
                null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }
}
