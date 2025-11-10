package com.example.inmobiliariaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.inmobiliariaapp.fragments.ContratosFragment;
import com.example.inmobiliariaapp.fragments.InmueblesFragment;
import com.example.inmobiliariaapp.fragments.InquilinosFragment;
import com.example.inmobiliariaapp.fragments.PerfilFragment;
import com.example.inmobiliariaapp.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;
import com.example.inmobiliariaapp.activities.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        // ✅ Si no hay sesión → volver al login
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // ✅ Inicializar Drawer + Toolbar
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // ✅ Primero fragment por defecto: Inmuebles
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new InmueblesFragment())
                    .commit();
            navView.setCheckedItem(R.id.nav_inmuebles);
        }

        // ✅ Listener del menú del Drawer
        navView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_perfil) {
                selectedFragment = new PerfilFragment();
            } else if (id == R.id.nav_inmuebles) {
                selectedFragment = new InmueblesFragment();
            } else if (id == R.id.nav_inquilinos) {
                selectedFragment = new InquilinosFragment();
            } else if (id == R.id.nav_contratos) {
                selectedFragment = new ContratosFragment();
            } else if (id == R.id.nav_logout) {

                // ✅ Cerrar sesión correctamente
                sessionManager.logout();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }

            // ✅ Cambiar fragment si corresponde
            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }
}
