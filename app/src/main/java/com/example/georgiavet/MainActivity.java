package com.example.georgiavet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.media.MediaPlayer;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button b1, b2;
    MediaPlayer sonido;
    static boolean yaSono = false;

    // Pantalla de inicio: botones para iniciar sesion o registrarse
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // La app esta disenada solo en modo claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Reproduce el sonido de inicio solo la primera vez que abre la app
        if (!yaSono) {
            sonido = MediaPlayer.create(this, R.raw.introsound);
            if (sonido != null) {
                sonido.start();
            }
            yaSono = true;
        }

        b1 = (Button) findViewById(R.id.btnIniciar);
        b2 = (Button) findViewById(R.id.btnRegistrar);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), registro.class));
            }
        });
    }
    // Libera el sonido de la memoria al cerrar la pantalla
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sonido != null) {
            sonido.release();
            sonido = null;
        }
    }
}