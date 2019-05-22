package com.example.appalertas2;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


public class PantallaRegistrar1 extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnRegistrarGuardar, btnIngresarRegistro, btnRegistrarSiguiente, btnRegistrarAnterior;
    LinearLayout lyRegistrar1, lyRegistrar2;
    EditText txtNombres, txtApellidos, txtNumeroDocumento, txtEmail, txtContraseña, txtTelefonoCelular, txtTelefonoFijo,
            txtDireccionResidencia;
    TextView txtFechaNacimiento;
    Spinner spnTipoDocumento, spnTipoSangre, spnEPS;
    ArrayAdapter<String> adapter;
    ArrayList<String> listaEPS = new ArrayList<>();
    ArrayList<String> listaIdEPS = new ArrayList<>();
    ArrayList<String> listaTipoSangre = new ArrayList<>();
    ArrayList<String> listaTipoDocumento = new ArrayList<>();
    Integer dia, mes, ano;
    String fechaNacimiento;
    static boolean valido = false ;

    String idEPS;
    String validarEmail;
    static String nombre, apellido, tipoDocumento, numeroDocumento, numeroCelular, correoElec, contrasena;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_registrar_1);
        conectar();
        llenarSpnTipoDocumento();
        llenarSpnTipoSangre();
        llenarSpnEPS();
        valido = false;
        lyRegistrar1.setVisibility(View.VISIBLE);
        lyRegistrar2.setVisibility(View.GONE);


        btnRegistrarSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtNombres.getText().toString().equals("") || !txtEmail.getText().toString().equals("") || !txtContraseña.getText().toString().equals("") ||
                        !txtNumeroDocumento.getText().toString().equals("") || !txtApellidos.getText().toString().equals("") || !txtTelefonoCelular.getText().toString().equals("")) {
                    lyRegistrar1.setVisibility(View.GONE);
                    lyRegistrar2.setVisibility(View.VISIBLE);
                    nombre = txtNombres.getText().toString();
                    apellido = txtApellidos.getText().toString();
                    tipoDocumento = String.valueOf(spnTipoDocumento.getSelectedItemPosition() + 1);
                    numeroDocumento = txtNumeroDocumento.getText().toString();
                    correoElec = txtEmail.getText().toString().replace(" ", "").toLowerCase();
                    contrasena = txtContraseña.getText().toString();
                    numeroCelular =txtTelefonoCelular.getText().toString();
                    timer();

                } else {
                    Toast.makeText(getApplicationContext(), "FAVOR LLENAR LOS CAMPOS OBLIGATORIOS", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRegistrarAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lyRegistrar1.setVisibility(View.VISIBLE);
                lyRegistrar2.setVisibility(View.GONE);


            }
        });


        btnRegistrarGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validarEmail.equals("102")) {
                    seleccionarIdEPS(spnEPS.getSelectedItemPosition());

       registrarUsuario(String.valueOf(spnTipoSangre.getSelectedItemPosition()+1),idEPS,tipoDocumento,numeroDocumento,
                            nombre,apellido,txtTelefonoFijo.getText().toString(),numeroCelular,correoElec,
                            txtDireccionResidencia.getText().toString(),contrasena,fechaNacimiento);

                    valido=true;
                    Toast.makeText(getApplicationContext(), "REGISTRO EXITOSO!", Toast.LENGTH_LONG).show();

                  Intent i = new Intent(getApplicationContext(), PantallaInicial2.class);
                    startActivity(i);

                } else {
                    Toast.makeText(getApplicationContext(), "EL EMAIL YA SE ENCUENTRA REGISTRADO", Toast.LENGTH_LONG).show();
                }


            }
        });


        btnIngresarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PantallaInicial2.class);
                startActivity(i);
            }
        });


        txtFechaNacimiento.setOnClickListener(this);

    }
    private void seleccionarIdEPS(Integer posicion) {

        idEPS = listaIdEPS.get(posicion);

    }


    @Override
    public void onClick(View v) {
        if (v == txtFechaNacimiento) {
            final Calendar c = Calendar.getInstance();
            dia = c.get(Calendar.DAY_OF_MONTH);
            mes = c.get(Calendar.MONTH);
            ano = c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    txtFechaNacimiento.setText(dayOfMonth + " / " + (monthOfYear + 1) + " / " + year);
                    asignarFechaFormatoAAAAMMDD(year, monthOfYear, dayOfMonth);
                }
            }, dia, mes, ano);
            datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            datePickerDialog.show();
        }
    }


    private void asignarFechaFormatoAAAAMMDD(int year, int monthOfYear, int dayOfMonth) {
        if ((dayOfMonth == 1 || dayOfMonth == 2 || dayOfMonth == 3 || dayOfMonth == 4 || dayOfMonth == 5 || dayOfMonth == 6 || dayOfMonth == 7 ||
                dayOfMonth == 8 || dayOfMonth == 9) && (monthOfYear == 0 || monthOfYear == 1 || monthOfYear == 2 || monthOfYear == 3 || monthOfYear == 4 ||
                monthOfYear == 5 || monthOfYear == 6 || monthOfYear == 7 || monthOfYear == 8)) {
            fechaNacimiento = year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth;
        } else if ((dayOfMonth == 1 || dayOfMonth == 2 || dayOfMonth == 3 || dayOfMonth == 4 || dayOfMonth == 5 || dayOfMonth == 6 || dayOfMonth == 7 ||
                dayOfMonth == 8 || dayOfMonth == 9) && (monthOfYear == 9 || monthOfYear == 10 || monthOfYear == 11)) {
            fechaNacimiento = year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth;

        } else if ((dayOfMonth != 1 || dayOfMonth != 2 || dayOfMonth != 3 || dayOfMonth != 4 || dayOfMonth != 5 || dayOfMonth != 6 || dayOfMonth != 7 ||
                dayOfMonth != 8 || dayOfMonth != 9) && (monthOfYear == 0 || monthOfYear == 1 || monthOfYear == 2 || monthOfYear == 3 || monthOfYear == 4 ||
                monthOfYear == 5 || monthOfYear == 6 || monthOfYear == 7 || monthOfYear == 8)) {
            fechaNacimiento = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth;
        } else {
            fechaNacimiento = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        }
    }


    private void timer(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
                                      @Override
                                      public void run() {
                                          buscarEmailRegistrado(correoElec);
                                      }
                                  },
                5000, 1000);   // 1000 Millisecond  = 1 second
        if(valido){
            timer.cancel();
        }
    }
    private void llenarSpnEPS() {
        String URL_EPS = "http://labs.ebotero.com/servicios_ayudapp/public/eps";

        JsonObjectRequest jsonObjRqst = new JsonObjectRequest(Request.Method.GET,
                URL_EPS,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        llenarListaEPS(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error :" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestqueue = Volley.newRequestQueue(getApplicationContext());
        requestqueue.add(jsonObjRqst);

    }


    private void llenarListaEPS(JSONObject obj) {
        try {
            JSONArray list = obj.optJSONArray("data");
            for (int i = 0; i < list.length(); i++) {
                JSONObject json_data = list.getJSONObject(i);
                String contenido = json_data.getString("Nombre");
                listaEPS.add(contenido);
                contenido = json_data.getString("Id") ;
                listaIdEPS.add(contenido);

            }
            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listaEPS);
            spnEPS.setAdapter(adapter);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
        }
    }


    private void buscarEmailRegistrado(final String email) {

        String URL_BuscarEmail = "http://labs.ebotero.com/servicios_ayudapp/public/buscar_usuario_email";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_BuscarEmail,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonobj = new JSONObject(response);
                            validarEmail = jsonobj.getString("status");
                        } catch (JSONException ex) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("Email", email);
                return parametros;
            }
        };
        RequestQueue requestqueue = Volley.newRequestQueue(getApplicationContext());
        requestqueue.add(stringRequest);
    }


    private void registrarUsuario(final String idRh, final String idEps, final String idTipoDocumento, final String numeroDocumento, final String nombres, final String apellidos,
                                  final String telefonoFijo, final String telefonoCelular, final String email, final String direccion, final String contrasena, final String fecha) {

        String URL_RegistroUsuario = "http://labs.ebotero.com/servicios_ayudapp/public/crear_usuario";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RegistroUsuario,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("IdRh", idRh);
                parametros.put("IdEps", idEps);
                parametros.put("IdTipoDeDocumento", idTipoDocumento);
                parametros.put("NroDeDocumento", numeroDocumento);
                parametros.put("Nombre", nombres);
                parametros.put("Apellido", apellidos);
                parametros.put("TelefonoFijo", telefonoFijo);
                parametros.put("Celular", telefonoCelular);
                parametros.put("Email", email);
                parametros.put("Direccion", direccion);
                parametros.put("Constrasena", contrasena);
                parametros.put("FechaDeNacimiento", fecha);
                return parametros;
            }
        };

        RequestQueue requestqueue = Volley.newRequestQueue(getApplicationContext());
        requestqueue.add(stringRequest);
    }


    private void llenarSpnTipoDocumento() {
        listaTipoDocumento.clear();
        listaTipoDocumento.add("Cédula de ciudadanía");
        listaTipoDocumento.add("Tarjeta de identidad");
        listaTipoDocumento.add("Cédula de extranjería");
        listaTipoDocumento.add("Pasaporte");
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listaTipoDocumento);
        spnTipoDocumento.setAdapter(adapter);
    }


    private void llenarSpnTipoSangre() {
        listaTipoSangre.clear();
        listaTipoSangre.add("O -");
        listaTipoSangre.add("O +");
        listaTipoSangre.add("A -");
        listaTipoSangre.add("A +");
        listaTipoSangre.add("B -");
        listaTipoSangre.add("B +");
        listaTipoSangre.add("AB -");
        listaTipoSangre.add("AB +");

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listaTipoSangre);
        spnTipoSangre.setAdapter(adapter);
    }


    private void conectar(){
        btnRegistrarGuardar = findViewById(R.id.btnRegistrarGuardar);
        btnIngresarRegistro = findViewById(R.id.btnIngresarRegistro);
        txtFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        txtNombres = findViewById(R.id.etNombres);
        txtApellidos = findViewById(R.id.etApellidos);
        txtNumeroDocumento = findViewById(R.id.etDocumentoID);
        txtEmail = findViewById(R.id.etEmail);
        txtContraseña = findViewById(R.id.etContraseña);
        txtTelefonoCelular = findViewById(R.id.etTelefonoCelularRegistro);
        txtTelefonoFijo = findViewById(R.id.etTelefonoFijoRegistro);
        txtDireccionResidencia = findViewById(R.id.etDireccion);
        spnEPS = findViewById(R.id.spnEPS);
        spnTipoDocumento = findViewById(R.id.spnTipoDocumentoID);
        spnTipoSangre = findViewById(R.id.spnTipoSangre);
        btnRegistrarSiguiente = findViewById(R.id.btnRegistrarSiguiente);
        btnRegistrarAnterior = findViewById(R.id.btnRegistrarAnterior);
        lyRegistrar1 = findViewById(R.id.lyRegistrar1);
        lyRegistrar2 = findViewById(R.id.lyRegistrar2);
    }

}
