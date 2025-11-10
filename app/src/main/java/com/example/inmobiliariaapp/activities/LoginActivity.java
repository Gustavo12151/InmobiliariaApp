package com.example.inmobiliariaapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inmobiliariaapp.MainActivity;
import com.example.inmobiliariaapp.R;
import com.example.inmobiliariaapp.network.ApiClient;
import com.example.inmobiliariaapp.network.ApiService;
import com.example.inmobiliariaapp.utils.SessionManager;

import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etClave;
    private Button btnLogin;
    private TextView tvOlvideClave;

    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etClave = findViewById(R.id.etClave);
        btnLogin = findViewById(R.id.btnLogin);
        tvOlvideClave = findViewById(R.id.tvOlvideClave);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);

        btnLogin.setOnClickListener(v -> intentarLogin());
        tvOlvideClave.setOnClickListener(v -> resetearClaveLocal());
    }

    private void intentarLogin() {
        String email = etEmail.getText().toString().trim();
        String clave = etClave.getText().toString().trim();

        if (email.isEmpty() || clave.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.login(email, clave).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String token = response.body().string();  // ✅ token leído correctamente
                        sessionManager.saveToken(token);

                        Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Error al procesar token", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // 🔹 Función que genera una nueva clave aleatoria y la actualiza
    private void resetearClaveLocal() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Ingrese su email para generar una nueva clave", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generar una nueva clave aleatoria (6 caracteres)
        String nuevaClave = generarClaveAleatoria(6);

        // Llamar a la API para cambiar la clave
        apiService.resetearClave(email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Mostrar la nueva clave al usuario
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Nueva clave generada")
                            .setMessage("Tu nueva clave es: " + nuevaClave + "\nPor favor, guárdala para ingresar.")
                            .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                            .show();
                } else {
                    Toast.makeText(LoginActivity.this, "No se pudo actualizar la clave", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔹 Generar clave aleatoria de letras y números
    private String generarClaveAleatoria(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }
}
