package com.example.georgiavet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class servicios extends AppCompatActivity {

    ListView lst1;
    ArrayList<String> servicios_ = new ArrayList<String>();
    ArrayList<String> ids = new ArrayList<String>();
    ArrayList<String> nombres = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    // Pantalla de servicios: al elegir uno se pasa a escoger la mascota
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_servicios);
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

        lst1 = (ListView) findViewById(R.id.lstServicios);

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_lista, servicios_);
        lst1.setAdapter(arrayAdapter);

        lst1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(), elegir_mascota.class)
                        .putExtra("id_servicio", ids.get(position))
                        .putExtra("nombre_servicio", nombres.get(position)));
            }
        });

        ListarServicios();
    }

    // Pide al servidor los servicios y los muestra en la lista
    public void ListarServicios() {

        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.100.38/crudGeorgia/mostrar_servicios_.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        servicios_.clear();
                        ids.clear();
                        nombres.clear();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String exito = jsonObject.getString("exito");
                            JSONArray jsonArray = jsonObject.getJSONArray("datos");
                            if (exito.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String id = object.getString("id");
                                    String nombre = object.getString("nombre");
                                    String descripcion = object.getString("descripcion");

                                    servicios_.add(nombre + "\n" + descripcion);
                                    ids.add(id);
                                    nombres.add(nombre);
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
                Toast.makeText(servicios.this, "Error de conexion con el servidor", Toast.LENGTH_SHORT).show();
            }
        }
        ) {

        };
        RequestQueue requestQueue = Volley.newRequestQueue(servicios.this);
        requestQueue.add(request);

    }

}