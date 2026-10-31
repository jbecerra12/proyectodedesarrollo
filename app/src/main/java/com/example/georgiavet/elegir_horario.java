package com.example.georgiavet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

public class elegir_horario extends AppCompatActivity {

    TextView t1, t2;
    RadioGroup rg1;
    Button b1;
    String idServicio, nombreServicio, idMascota, nombreMascota;

    // Tercer paso: el cliente escoge el horario y confirma la consulta
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_elegir_horario);
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

        t1 = (TextView) findViewById(R.id.tvServicio);
        t2 = (TextView) findViewById(R.id.tvMascota);
        rg1 = (RadioGroup) findViewById(R.id.rgHorarios);
        b1 = (Button) findViewById(R.id.btnConfirmar);

        Intent i = getIntent();
        idServicio = i.getStringExtra("id_servicio");
        nombreServicio = i.getStringExtra("nombre_servicio");
        idMascota = i.getStringExtra("id_mascota");
        nombreMascota = i.getStringExtra("nombre_mascota");

        t1.setText("Servicio: " + nombreServicio);
        t2.setText("Mascota: " + nombreMascota);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmar();
            }
        });
    }

    // Verifica que haya un horario elegido y envia la consulta al servidor
    public void confirmar() {
        int elegido = rg1.getCheckedRadioButtonId();

        if (elegido == -1) {
            Toast.makeText(this, "seleccione un horario", Toast.LENGTH_SHORT).show();
        } else {
            RadioButton rb = (RadioButton) findViewById(elegido);
            String p_horario = rb.getText().toString();

            StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.100.38/crudGeorgia/insertar_cita_.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.trim().equals("cita registrada")) {
                                mostrarConfirmacion(p_horario);
                            } else {
                                Toast.makeText(elegir_horario.this, "Error al enviar la solicitud", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(elegir_horario.this, "Error de conexion con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
            ) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("id_usuario", Sesion.id);
                    params.put("id_mascota", idMascota);
                    params.put("id_servicio", idServicio);
                    params.put("horario", p_horario);

                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(elegir_horario.this);
            requestQueue.add(request);
        }
    }

    // Muestra el resumen de la solicitud y regresa al menu del cliente
    private void mostrarConfirmacion(String horario) {
        new AlertDialog.Builder(this)
                .setTitle("¡Solicitud enviada!")
                .setMessage("Servicio: " + nombreServicio + "\nMascota: " + nombreMascota + "\nHorario: " + horario
                        + "\n\nLa veterinaria revisara tu solicitud y la confirmara.")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(), menu.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                    }
                })
                .show();
    }
}