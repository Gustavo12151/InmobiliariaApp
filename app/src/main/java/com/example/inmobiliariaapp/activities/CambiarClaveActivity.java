package com.example.inmobiliariaapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.inmobiliariaapp.R;
import com.example.inmobiliariaapp.network.ApiClient;
import com.example.inmobiliariaapp.network.ApiService;
import com.example.inmobiliariaapp.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CambiarClaveActivity extends AppCompatActivity {

    private EditText etClaveActual, etClaveNueva;
    private Button btnCambiar;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_clave);

        etClaveActual = findViewById(R.id.etClaveActual);
        etClaveNueva = findViewById(R.id.etClaveNueva);
        btnCambiar = findViewById(R.id.btnCambiar);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);

        btnCambiar.setOnClickListener(v -> cambiarClave());
    }

    private void cambiarClave() {
        String actual = etClaveActual.getText().toString().trim();
        String nueva = etClaveNueva.getText().toString().trim();

        if (actual.isEmpty() || nueva.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = "Bearer " + sessionManager.getToken();

        Call<Void> call = apiService.cambiarClave(token, actual, nueva);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CambiarClaveActivity.this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CambiarClaveActivity.this, "Error al cambiar clave", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CambiarClaveActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
