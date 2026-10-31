package com.example.georgiavet;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class agregar extends AppCompatActivity {

    EditText ed1, ed2, ed3, ed4;
    Spinner sp1;
    Button b1, b2;

    // Pantalla para registrar una mascota nueva
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar);
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
        b2 = (Button) findViewById(R.id.btnRegresar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.especies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapter);

        ed3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirFecha();
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Abre el calendario para elegir la fecha de nacimiento (no permite fechas futuras)
    private void elegirFecha() {
        Calendar c = Calendar.getInstance();
        int anio = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                ed3.setText(String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, month + 1, year));
            }
        }, anio, mes, dia);
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    // Valida los datos y guarda la mascota en la base de datos
    public void guardar() {
        String p_nombre = ed1.getText().toString().trim();
        String p_especie = sp1.getSelectedItem().toString();
        String p_raza = ed2.getText().toString().trim();
        String p_fecha = ed3.getText().toString().trim();
        String p_caracteristicas = ed4.getText().toString().trim();

        if (p_nombre.isEmpty()) {
            Toast.makeText(this, "ingrese nombre de la mascota", Toast.LENGTH_SHORT).show();
        } else if (p_raza.isEmpty()) {
            Toast.makeText(this, "ingrese raza", Toast.LENGTH_SHORT).show();
        } else if (p_fecha.isEmpty()) {
            Toast.makeText(this, "seleccione la fecha de nacimiento", Toast.LENGTH_SHORT).show();
        } else if (p_caracteristicas.isEmpty()) {
            Toast.makeText(this, "ingrese caracteristicas", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.100.38/crudGeorgia/insertar_mascota_.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.trim().equals("mascota registrada")) {
                                Toast.makeText(agregar.this, "Mascota registrada correctamente", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(agregar.this, "Error al registrar la mascota", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(agregar.this, "Error de conexion con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
            ) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("id_usuario", Sesion.id);
                    params.put("nombre", p_nombre);
                    params.put("especie", p_especie);
                    params.put("raza", p_raza);
                    params.put("fecha_nacimiento", Fechas.paraBD(p_fecha));
                    params.put("caracteristicas", p_caracteristicas);

                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(agregar.this);
            requestQueue.add(request);
        }
    }
}