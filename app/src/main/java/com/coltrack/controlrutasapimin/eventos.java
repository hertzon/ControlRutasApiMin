package com.coltrack.controlrutasapimin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Nelson Rodriguez on 31/03/2016.
 */
public class eventos extends AppCompatActivity{
    Button btnVolver;
    String estudiante;
    String ruta;
    String correo;
    String telefonoAcudiente;
    String LOGTAG="log";
    TextView txtViewEstudiante;
    RadioButton rBtnEnfermedad;
    RadioButton rBtnAusente;
    RadioButton rBtnOtraRuta;
    EditText editTxt;
    String evento;
    String ubicacion;
    String fecha;
    String strHora;
    String posicion;
    Button btnEnviarNovedad;
    JSONObject jsonObject;
    Calendar cal;
    int dia=0;
    int mes=0;
    int ano=0;
    int hora=0;
    int minuto=0;
    String cuerpoMensaje=null;
    EditText editTextEvento;
    String asunto;
    String usuario;
    String strLatitud;
    String strLongitud;
    String novedad;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.eventos);
        btnVolver=(Button)findViewById(R.id.btnVolver);
        txtViewEstudiante=(TextView)findViewById(R.id.txtViewEstudiante);
        rBtnEnfermedad=(RadioButton)findViewById(R.id.rBtnEnfermedad);
        rBtnAusente=(RadioButton)findViewById(R.id.rBtnAusente);
        rBtnOtraRuta=(RadioButton)findViewById(R.id.rBtnOtraRtua);
        editTxt=(EditText)findViewById(R.id.editTextEvento);
        btnEnviarNovedad=(Button)findViewById(R.id.btnEnviarNovedad);
        editTextEvento=(EditText)findViewById(R.id.editTextEvento);


        Bundle bundle=getIntent().getExtras();
        estudiante=bundle.getString("estudiante").toString();
        ruta=bundle.getString("ruta").toString();
        correo=bundle.getString("correo").toString();
        telefonoAcudiente=bundle.getString("telefonoAcudiente");
        ubicacion=bundle.getString("ubicacion");
        fecha=bundle.getString("fecha");
        strHora=bundle.getString("hora");
        posicion=bundle.getString("posicion");
        usuario=bundle.getString("usuario");
        strLatitud=bundle.getString("strLatitud");
        strLongitud=bundle.getString("strLongitud");


        Log.d(LOGTAG, "Datos que llegan al activity:");
        Log.d(LOGTAG, estudiante);
        Log.d(LOGTAG, ruta);
        Log.d(LOGTAG, correo);
        Log.d(LOGTAG, telefonoAcudiente);
        txtViewEstudiante.setText("Estudiante: " + estudiante);

        rBtnEnfermedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evento = "";
                evento = "El estudiante " + estudiante + " no subio a la ruta " + ruta + " por la novedad";
                evento = evento + " enfermedad ";
                evento = evento + "a las " + strHora + " del " + fecha + " en la posicion " + posicion;
                editTxt.setText(evento);
                novedad="enfermedad";
            }
        });

        rBtnAusente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evento="";
                evento="El estudiante "+estudiante+" no subio a la ruta "+ruta+" por la novedad";
                evento=evento+" ausente o no abordo ";
                evento=evento+ "a las "+strHora+" del "+fecha+" en la posicion "+posicion;
                editTxt.setText(evento);
                novedad="ausente o no abordo";
            }
        });
        rBtnOtraRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evento="";
                evento="El estudiante "+estudiante+" no subio a la ruta "+ruta+" por la novedad";
                evento=evento+" abordo otra ruta      ";
                evento=evento+ "a las "+strHora+" del "+fecha+" en la posicion "+posicion;
                editTxt.setText(evento);
                novedad="abordo otra ruta";
            }
        });

        btnEnviarNovedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextEvento.getText().toString().equals("")) {
                    BackgroundTask enviarMail = new BackgroundTask();
                    enviarMail.execute();
                }else {
                    Toast.makeText(getApplicationContext(),"Escriba o seleccione la novedad!!!",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i=new Intent(getApplicationContext(),com.coltrack.controlrutasapimin.ListadoEstudiantes.class);
                //startActivity(i);
                finish();
            }
        });
    }
    private class BackgroundTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(eventos.this, "Enviando Correo a acudiente", "Conectando...");
            Log.d(LOGTAG, "pre  execute");
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean status=false;
            Log.d(LOGTAG, "doing");
            jsonObject = new JSONObject();
            try {
                leerFechaHora();
//                DecimalFormat numberFormat = new DecimalFormat("#.#####");
//                String strLatitud=numberFormat.format(latitude);
//                strLatitud=strLatitud.replace(",",".");
//                String strLongitud=numberFormat.format(longitude);
//                strLongitud=strLongitud.replace(",",".");
                //ubicacion =  "http://maps.google.es/maps?q=" + strLatitud + "," + strLongitud;
//                //if (tipoAccion==1) {
//                    cuerpoMensaje = "Buenos dias, el estudiante " + estudiante + " se ha subido a la ruta " + ruta + " a las " + hora + ":" + minuto + " del " + dia + "/" + mes + "/" + ano
//                            + " en la ubicacion: " + ubicacion;
//                //}
//                //if (tipoAccion==2){
//                    cuerpoMensaje = "Buenos dias, el estudiante " + estudiante + " se ha bajado de la ruta " + ruta + " a las " + hora + ":" + minuto + " del " + dia + "/" + mes + "/" + ano
//                            + " en la ubicacion: " + ubicacion;
//                //}
//                Log.d(LOGTAG, "body: " + cuerpoMensaje);
                jsonObject.put("correo",correo);

                asunto="";
                asunto="Novedad: estudiante "+estudiante+" "+dia+"/"+mes+"/"+ano+" "+hora+":"+minuto;
                jsonObject.put("asunto",asunto);
                jsonObject.put("cuerpoMensaje",evento);

                //datos para los eventos
                jsonObject.put("usuario",usuario);
                jsonObject.put("ruta",ruta);
                jsonObject.put("nombreEstudiante",estudiante);
                jsonObject.put("evento", novedad);
                jsonObject.put("latitud",strLatitud);
                jsonObject.put("longitud",strLongitud);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                Log.d(LOGTAG, "date: " + currentDateandTime);
                jsonObject.put("date",currentDateandTime);


                Log.d(LOGTAG, "Enviando datos de mail a servidor....");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("json", jsonObject.toString()));
                String response = makePOSTRequest("http://107.170.38.31/phpDir/sendmail.php", nameValuePairs);
                Log.d(LOGTAG, "mail php response: "+response);
                if (response.equals("correo enviado")){
                    status=true;
                }else {
                    status=false;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(LOGTAG,"post execute");
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if (!result) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //update ui here
                        // display toast here
                        Toast.makeText(eventos.this, "Problema enviando novedad...", Toast.LENGTH_LONG).show();
                    }
                });
            }else {
                Toast.makeText(eventos.this,"Novedad enviada correctamente!",Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
    public String makePOSTRequest(String url, List<NameValuePair> nameValuePairs) {
        String response = "";

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                String jsonResult = inputStreamToString(httpResponse.getEntity().getContent()).toString();
                JSONObject object = null;
                Log.d(LOGTAG, "Response:" + jsonResult);
                try {
                    object = new JSONObject(jsonResult);
                    String estadoLogin = object.getString("action");

                    Log.d(LOGTAG, "Estado Login:" + estadoLogin);
                    response=estadoLogin;

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(LOGTAG, "Error1:" + e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    private StringBuilder inputStreamToString(InputStream is)
    {
        String rLine = "";
        StringBuilder answer = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try
        {
            while ((rLine = rd.readLine()) != null)
            {
                answer.append(rLine);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return answer;
    }
    public void leerFechaHora(){
        cal = Calendar.getInstance();
        dia=cal.get(Calendar.DAY_OF_MONTH);
        mes=cal.get(Calendar.MONTH);
        ano=cal.get(Calendar.YEAR);
        hora=cal.get(Calendar.HOUR_OF_DAY);
        minuto=cal.get(Calendar.MINUTE);
    }
}
