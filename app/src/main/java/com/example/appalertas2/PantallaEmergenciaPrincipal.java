package com.example.appalertas2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cache.Session;

public class PantallaEmergenciaPrincipal extends AppCompatActivity {
    ImageButton btnEmergenciaPrincipal, btnMiPerfilPrincipal, btnMisContactos, btnCerrarSesion;
    static String telefono_contacto;
    String ID_USUARIO;
    Session sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_emergencia_principal);
        conectar();
        Bundle b = getIntent().getExtras();
        sesion = new Session(this);

        if (b != null){
            ID_USUARIO = b.getString("Id");
        }
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sesion.saveSessionIdCache(null);
                startActivity(new Intent(getApplicationContext(), PantallaInicial1.class));
            }
        });
        btnMisContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PantallaMisContactos.class).putExtra("Id", ID_USUARIO);
                startActivity(i);
            }
        });
        btnMiPerfilPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PantallaMiPerfil.class).putExtra("Id", ID_USUARIO);
                startActivity(i);
            }
        });


        if (ActivityCompat.checkSelfPermission(PantallaEmergenciaPrincipal.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PantallaEmergenciaPrincipal.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {ActivityCompat.requestPermissions(PantallaEmergenciaPrincipal.this, new String[]
                {
                            Manifest.permission.SEND_SMS,}, 1000);
        }else{

        };

        recibirTelefonoContacto(ID_USUARIO);
        btnEmergenciaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enviarMensaje("3234729383","Ayuda al usuario" + ID_USUARIO +" Esta ubicado en : UNIVERSIDAD DE MEDELLIN");

            }
        });


    }
    public void recibirTelefonoContacto(final String idUsuario){


        String URL_recibirTelefono = "http://labs.ebotero.com/servicios_ayudapp/public/telefono_contacto_usuario";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_recibirTelefono,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonobj = new JSONObject(response);
                            JSONArray list = jsonobj.optJSONArray("data");

                            if(list.length() != 0) {
                                telefono_contacto = jsonobj.getString("celular");
                            } else
                            {
                                Toast.makeText(getApplicationContext(), "No tiene ningun contacto Registrado", Toast.LENGTH_LONG).show();
                            }

                        }catch (JSONException ex){

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("IdUsuario", idUsuario);
                return parametros;
            }
        };


        RequestQueue requestqueue = Volley.newRequestQueue(this);
        requestqueue.add(stringRequest);
    }


    private void enviarMensaje (String numero, String mensaje){
        try{
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numero,null,mensaje,null,null);
            Toast.makeText(getApplicationContext(),"Mensaje Enviado",Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "Mensaje no enviado", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void conectar(){
        btnMiPerfilPrincipal = findViewById(R.id.btnMiPerfilPrincipal);
        btnEmergenciaPrincipal = findViewById(R.id.btnEmergenciaPrincipal);
        btnMisContactos = (ImageButton)findViewById(R.id.btnMisContactosPrincipal);
        btnCerrarSesion =(ImageButton)findViewById(R.id.imageButtonCerrarSesion);
    }

}
