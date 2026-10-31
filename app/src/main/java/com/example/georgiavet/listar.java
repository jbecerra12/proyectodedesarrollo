package com.example.georgiavet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

public class listar extends AppCompatActivity {

    Button b1;
    ListView lst1;
    ArrayList<String> mascotas_ = new ArrayList<String>();
    public static ArrayList<Mascota> mascotas = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    Mascota mascota;

    // Pantalla Mis mascotas: lista las mascotas y permite agregar o editar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listar);
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

        b1 = (Button) findViewById(R.id.btnAgregar);
        lst1 = (ListView) findViewById(R.id.lstMascotas);

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_lista, mascotas_);
        lst1.setAdapter(arrayAdapter);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), agregar.class));
            }
        });

        lst1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(), editar.class).putExtra("position", position));
            }
        });
    }

    // Cada vez que se vuelve a esta pantalla se recarga la lista
    @Override
    protected void onResume() {
        super.onResume();
        ListarDatos();
    }

    // Pide al servidor las mascotas del cliente y las muestra en la lista
    public void ListarDatos() {

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
                                    mascota = new Mascota(id, nombre, especie, raza, fecha_nacimiento, caracteristicas);
                                    mascotas.add(mascota);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(listar.this, "Error de conexion con el servidor", Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(listar.this);
        requestQueue.add(request);

    }

}