package com.example.georgiavet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class registro extends AppCompatActivity {

    EditText ed1, ed2, ed3, ed4, ed5;
    Button b1;
    TextView t1;

    // Pantalla para crear la cuenta de un cliente
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Boton de retroceder (arriba a la izquierda)
        findViewById(R.id.btnAtras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ed1 = (EditText) findViewById(R.id.etNombre);
        ed2 = (EditText) findViewById(R.id.etCorreo);
        ed3 = (EditText) findViewById(R.id.etTelefono);
        ed4 = (EditText) findViewById(R.id.etPassword);
        ed5 = (EditText) findViewById(R.id.etConfirmar);
        b1 = (Button) findViewById(R.id.btnRegistrarse);
        t1 = (TextView) findViewById(R.id.tvIniciaSesion);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), login.class));
                finish();
            }
        });
    }

    // Valida los datos y los envia al servidor para crear la cuenta
    public void registrar() {
        String p_nombre = ed1.getText().toString().trim();
        String p_correo = ed2.getText().toString().trim();
        String p_telefono = ed3.getText().toString().trim();
        String p_password = ed4.getText().toString().trim();
        String p_confirmar = ed5.getText().toString().trim();

        if (p_nombre.isEmpty()) {
            Toast.makeText(this, "ingrese nombre completo", Toast.LENGTH_SHORT).show();
        } else if (p_correo.isEmpty()) {
            Toast.makeText(this, "ingrese correo", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(p_correo).matches()) {
            Toast.makeText(this, "correo no valido", Toast.LENGTH_SHORT).show();
        } else if (p_telefono.isEmpty()) {
            Toast.makeText(this, "ingrese telefono", Toast.LENGTH_SHORT).show();
        } else if (p_password.length() < 6) {
            Toast.makeText(this, "la contraseña debe tener minimo 6 caracteres", Toast.LENGTH_SHORT).show();
        } else if (!p_password.equals(p_confirmar)) {
            Toast.makeText(this, "las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.100.38/crudGeorgia/registro_.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String r = response.trim();
                            if (r.equals("registrado")) {
                                Toast.makeText(registro.this, "Cuenta creada, inicia sesion", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), login.class));
                                finish();
                            } else if (r.equals("correo existe")) {
                                Toast.makeText(registro.this, "Ese correo ya esta registrado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(registro.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(registro.this, "Error de conexion con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
            ) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("nombre", p_nombre);
                    params.put("correo", p_correo);
                    params.put("telefono", p_telefono);
                    params.put("password", p_password);

                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(registro.this);
            requestQueue.add(request);
        }
    }
}