package com.example.georgiavet;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class editar extends AppCompatActivity {

    EditText ed1, ed2, ed3, ed4;
    Spinner sp1;
    Button b1, b2;
    private int position;
    String idMascota;

    // Pantalla para editar o eliminar una mascota ya registrada
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar);
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
        sp1 = (Spinner) findViewById(R.id.spEspecie);
        ed2 = (EditText) findViewById(R.id.etRaza);
        ed3 = (EditText) findViewById(R.id.etFecha);
        ed4 = (EditText) findViewById(R.id.etCaracteristicas);
        b1 = (Button) findViewById(R.id.btnGuardar);
        b2 = (Button) findViewById(R.id.btnEliminar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.especies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapter);

        Intent i = getIntent();
        position = i.getExtras().getInt("position");

        idMascota = listar.mascotas.get(position).getId();
        ed1.setText(listar.mascotas.get(position).getNombre());
        ed2.setText(listar.mascotas.get(position).getRaza());
        ed3.setText(Fechas.paraVista(listar.mascotas.get(position).getFecha_nacimiento()));
        ed4.setText(listar.mascotas.get(position).getCaracteristicas());
        seleccionarEspecie(listar.mascotas.get(position).getEspecie());

        ed3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirFecha();
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarEliminar();
            }
        });
    }

    // Deja seleccionada en el spinner la especie que ya tenia la mascota
    private void seleccionarEspecie(String especie) {
        String[] lista = getResources().getStringArray(R.array.especies);
        for (int i = 0; i < lista.length; i++) {
            if (lista[i].equalsIgnoreCase(especie)) {
                sp1.setSelection(i);
            }
        }
    }

    // Abre el calendario con la fecha actual de la mascota (no permite fechas futuras)
    private void elegirFecha() {
        Calendar c = Calendar.getInstance();
        int anio = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        String[] p = ed3.getText().toString().split("/");
        if (p.length == 3) {
            dia = Integer.parseInt(p[0]);
            mes = Integer.parseInt(p[1]) - 1;
            anio = Integer.parseInt(p[2]);
        }

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                ed3.setText(String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, month + 1, year));
            }
        }, anio, mes, dia);
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    // Valida los datos y actualiza la mascota en la base de datos
    public void Update() {
        String nombre = ed1.getText().toString().trim();
        String especie = sp1.getSelectedItem().toString();
        String raza = ed2.getText().toString().trim();
        String fecha = ed3.getText().toString().trim();
        String caracteristicas = ed4.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "ingrese nombre de la mascota", Toast.LENGTH_SHORT).show();
        } else if (raza.isEmpty()) {
            Toast.makeText(this, "ingrese raza", Toast.LENGTH_SHORT).show();
        } else if (fecha.isEmpty()) {
            Toast.makeText(this, "seleccione la fecha de nacimiento", Toast.LENGTH_SHORT).show();
        } else if (caracteristicas.isEmpty()) {
            Toast.makeText(this, "ingrese caracteristicas", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.100.38/crudGeorgia/actualizar_mascota_.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.trim().equals("mascota actualizada")) {
                                Toast.makeText(editar.this, "actualizo correctamente", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(editar.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(editar.this, "Error de conexion con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
            ) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("id", idMascota);
                    params.put("id_usuario", Sesion.id);
                    params.put("nombre", nombre);
                    params.put("especie", especie);
                    params.put("raza", raza);
                    params.put("fecha_nacimiento", Fechas.paraBD(fecha));
                    params.put("caracteristicas", caracteristicas);

                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(editar.this);
            requestQueue.add(request);
        }
    }

    // Pregunta al usuario si realmente quiere eliminar la mascota
    private void confirmarEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar mascota")
                .setMessage("¿Seguro que deseas eliminar a " + ed1.getText().toString() + "? Tambien se eliminaran sus consultas.")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Eliminar();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Elimina la mascota de la base de datos
    public void Eliminar() {
        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.100.38/crudGeorgia/eliminar_mascota_.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("mascota eliminada")) {
                            Toast.makeText(editar.this, "Mascota eliminada", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(editar.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(editar.this, "Error de conexion con el servidor", Toast.LENGTH_SHORT).show();
            }
        }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("id", idMascota);
                params.put("id_usuario", Sesion.id);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(editar.this);
        requestQueue.add(request);
    }
}