package com.example.georgiavet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class elegir_mascota extends AppCompatActivity {

    TextView t1;
    ListView lst1;
    ArrayList<String> mascotas_ = new ArrayList<String>();
    ArrayList<Mascota> mascotas = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    String idServicio, nombreServicio;

    // Segundo paso: el cliente escoge la mascota que recibira el servicio
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_elegir_mascota);
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
        lst1 = (ListView) findViewById(R.id.lstMascotas);

        Intent i = getIntent();
        idServicio = i.getStringExtra("id_servicio");
        nombreServicio = i.getStringExtra("nombre_servicio");
        t1.setText("Servicio: " + nombreServicio);

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_lista, mascotas_);
        lst1.setAdapter(arrayAdapter);

        lst1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(), elegir_horario.class)
                        .putExtra("id_servicio", idServicio)
                        .putExtra("nombre_servicio", nombreServicio)
                        .putExtra("id_mascota", mascotas.get(position).getId())
                        .putExtra("nombre_mascota", mascotas.get(position).getNombre()));
            }
        });

        ListarMascotas();
    }

    // Pide al servidor las mascotas del cliente y las muestra en la lista
    public void ListarMascotas() {

        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.100.38/crudGeorgia/mostrar_mascotas_.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        mascotas_.clear();
                        mascotas.clear();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String exito = jsonObject.getString("exito");
                            JSONArray jsonArray = jsonObject.getJSONArray("datos");
                            if (exito.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String id = object.getString("id");
                                    String nombre = object.getString("nombre");
                                    String especie = object.getString("especie");
                                    String raza = object.getString("raza");
                                    String fecha_nacimiento = object.getString("fecha_nacimiento");
                                    String caracteristicas = object.getString("caracteristicas");

                                    mascotas_.add(nombre + "\n" + especie + " - " + raza + "\nNació: " + Fechas.paraVista(fecha_nacimiento));
                                    mascotas.add(new Mascota(id, nombre, especie, raza, fecha_nacimiento, caracteristicas));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        arrayAdapter.notifyDataSetChanged();

                        if (mascotas.isEmpty()) {
                            Toast.makeText(elegir_mascota.this, "Primero registra una mascota en Mis mascotas", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(elegir_mascota.this, "Error de conexion con el servidor", Toast.LENGTH_SHORT).show();
            }
        }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("id_usuario", Sesion.id);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(elegir_mascota.this);
        requestQueue.add(request);

    }

}