package com.example.georgiavet;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class admin extends AppCompatActivity {

    TextView t1;
    Button b1;
    ListView lst1;
    ArrayList<String> consultas_ = new ArrayList<String>();
    ArrayList<Consulta> consultas = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    // Panel del administrador: lista las consultas de los clientes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        t1 = (TextView) findViewById(R.id.tvHolaAdmin);
        b1 = (Button) findViewById(R.id.btnCerrar);
        lst1 = (ListView) findViewById(R.id.lstConsultas);

        t1.setText("Hola, " + Sesion.nombre);

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_lista, consultas_);
        lst1.setAdapter(arrayAdapter);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        lst1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mostrarOpciones(consultas.get(position));
            }
        });
    }

    // Cada vez que se vuelve a esta pantalla se recarga la lista
    @Override
    protected void onResume() {
        super.onResume();
        ListarConsultas();
    }

    // Pide al servidor todas las consultas y las muestra en la lista
    public void ListarConsultas() {

        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.100.38/crudGeorgia/mostrar_citas_.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        consultas_.clear();
                        consultas.clear();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String exito = jsonObject.getString("exito");
                            JSONArray jsonArray = jsonObject.getJSONArray("datos");
                            if (exito.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String id = object.getString("id");
                                    String mascota = object.getString("mascota");
                                    String especie = object.getString("especie");
                                    String servicio = object.getString("servicio");
                                    String cliente = object.getString("cliente");
                                    String telefono = object.getString("telefono");
                                    String correo = object.getString("correo");
                                    String horario = object.getString("horario");
                                    String estado = object.getString("estado");
                                    String fecha = object.getString("fecha_solicitud");

                                    String icono = "🟡";
                                    if (estado.equals("Aprobada")) {
                                        icono = "🟢";
                                    } else if (estado.equals("Rechazada")) {
                                        icono = "🔴";
                                    }

                                    consultas_.add(icono + " " + estado + "\n"
                                            + mascota + " (" + especie + ") - " + servicio + "\n"
                                            + "Cliente: " + cliente + "\n"
                                            + "Horario: " + horario + "\n"
                                            + "Solicitada: " + fecha.substring(0, 10));
                                    consultas.add(new Consulta(id, mascota, especie, servicio, cliente, telefono, correo, horario, estado, fecha));
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
                Toast.makeText(admin.this, "Error de conexion con el servidor", Toast.LENGTH_SHORT).show();
            }
        }
        ) {

        };
        RequestQueue requestQueue = Volley.newRequestQueue(admin.this);
        requestQueue.add(request);
    }

    // Muestra las acciones del admin para una consulta: aprobar, rechazar o contactar
    private void mostrarOpciones(final Consulta c) {
        String[] opciones = {"Aprobar", "Rechazar", "Llamar al cliente", "Enviar correo al cliente"};

        new AlertDialog.Builder(admin.this)
                .setTitle(c.getMascota() + " - " + c.getServicio())
                .setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            cambiarEstado(c.getId(), "Aprobada");
                        } else if (which == 1) {
                            cambiarEstado(c.getId(), "Rechazada");
                        } else if (which == 2) {
                            llamarCliente(c.getTelefono());
                        } else {
                            enviarCorreo(c);
                        }
                    }
                })
                .show();
    }

    // Cambia el estado de la consulta (Aprobada o Rechazada) en la base de datos
    public void cambiarEstado(final String id, final String estado) {
        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.100.38/crudGeorgia/actualizar_cita_.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("cita actualizada")) {
                            Toast.makeText(admin.this, "Consulta " + estado.toLowerCase(), Toast.LENGTH_SHORT).show();
                            ListarConsultas();
                        } else {
                            Toast.makeText(admin.this, "Error al actualizar la consulta", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(admin.this, "Error de conexion con el servidor", Toast.LENGTH_SHORT).show();
            }
        }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("estado", estado);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(admin.this);
        requestQueue.add(request);
    }

    // Abre el marcador del telefono con el numero del cliente
    private void llamarCliente(String telefono) {
        try {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telefono)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No se pudo abrir el telefono", Toast.LENGTH_SHORT).show();
        }
    }

    // Abre la app de correo con un mensaje listo para el cliente
    private void enviarCorreo(Consulta c) {
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + c.getCorreo()));
        i.putExtra(Intent.EXTRA_SUBJECT, "Georgia's Vet - Tu consulta de " + c.getServicio());
        i.putExtra(Intent.EXTRA_TEXT, "Hola " + c.getCliente() + ", te escribimos por la consulta de "
                + c.getMascota() + " (" + c.getHorario() + ").");
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No hay una app de correo instalada", Toast.LENGTH_SHORT).show();
        }
    }

    // Limpia la sesion y regresa a la pantalla de inicio
    public void cerrarSesion() {
        Sesion.cerrar();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}