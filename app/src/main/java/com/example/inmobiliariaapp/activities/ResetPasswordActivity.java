package com.example.inmobiliariaapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.inmobiliariaapp.R;
import com.example.inmobiliariaapp.network.ApiClient;
import com.example.inmobiliariaapp.network.ApiService;
import com.example.inmobiliariaapp.utils.SessionManager;

import java.security.SecureRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnGenerar;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etEmail = findViewById(R.id.etEmail);
        btnGenerar = findViewById(R.id.btnEnviar);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);

        btnGenerar.setText("Generar nueva clave");
        btnGenerar.setOnClickListener(v -> generarClave());
    }

    private void generarClave() {
        String email = etEmail.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(this, "Ingrese su email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generar nueva contraseña temporal
        String nuevaClave = generarClaveAleatoria(8);

        String token = "Bearer " + sessionManager.getToken();

        apiService.cambiarClave(token, "", nuevaClave).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    mostrarNuevaClave(nuevaClave);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarNuevaClave(String nuevaClave) {
        new AlertDialog.Builder(this)
                .setTitle("Nueva contraseña generada")
                .setMessage("Tu nueva contraseña es:\n\n" + nuevaClave + "\n\nUsala para iniciar sesión.")
                .setPositiveButton("Aceptar", (dialog, which) -> finish())
                .show();
    }

    private String generarClaveAleatoria(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < longitud; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }
}
