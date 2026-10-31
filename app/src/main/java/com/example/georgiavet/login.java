package com.example.georgiavet;

import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

    EditText ed1, ed2;
    Button b1;
    TextView t1;

    // Pantalla de inicio de sesion (cliente y administrador)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
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

        ed1 = (EditText) findViewById(R.id.etCorreo);
        ed2 = (EditText) findViewById(R.id.etPassword);
        b1 = (Button) findViewById(R.id.btnIngresar);
        t1 = (TextView) findViewById(R.id.tvRegistrate);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingresar();
            }
        });
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), registro.class));
                finish();
            }
        });
    }

    // Valida los campos y envia correo y contrasena al servidor
    public void ingresar() {
        String p_correo = ed1.getText().toString().trim();
        String p_password = ed2.getText().toString().trim();

        if (p_correo.isEmpty()) {
            Toast.makeText(this, "ingrese correo", Toast.LENGTH_SHORT).show();
        } else if (p_password.isEmpty()) {
            Toast.makeText(this, "ingrese contraseña", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.100.38/crudGeorgia/login_.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            procesarLogin(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(login.this, "Error de conexion con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
            ) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("correo", p_correo);
                    params.put("password", p_password);

                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(login.this);
            requestQueue.add(request);
        }
    }

    // Lee la respuesta y abre el menu del cliente o el panel del administrador
    private void procesarLogin(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String exito = jsonObject.getString("exito");

            if (exito.equals("1")) {
                Sesion.id = jsonObject.getString("id");
                Sesion.nombre = jsonObject.getString("nombre");
                Sesion.rol = jsonObject.getString("rol");

                if (Sesion.rol.equals("admin")) {
                    startActivity(new Intent(getApplicationContext(), admin.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), menu.class));
                }
                finish();
            } else {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Respuesta invalida del servidor", Toast.LENGTH_SHORT).show();
        }
    }
}