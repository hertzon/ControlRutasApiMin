package com.coltrack.controlrutasapimin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
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
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class ListadoEstudiantes extends AppCompatActivity {
    String LOGTAG="log";
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    ExpandableListAdapter listAdapter1;
    ExpandableListView expListView1;
    List<String> listDataHeader1;
    HashMap<String, List<String>> listDataChild1;

    String ruta;
    EditText gradoEstudiante;
    EditText estudianteSeleccionado;
    Button subio;
    Button bajo;
    Button subioSms;
    Button bajoSms;
    Button llamar;
    double longitude=0;
    double latitude=0;
    SQLiteDatabase myDB;
    Cursor c;
    Cursor cc;
    JSONObject jsonObject;
    String correoSeleccionado=null;
    String estudiante=null;
    String cuerpoMensaje=null;
    TextView titulo;
    List<String> top250;
    List<String> top251;
    TextView estudianteSelect;
    TextView eventos;
    EditText editTextTelefonoAcudiente;
    TextView movilAcudiente;
    EditText editTextgrado;
    TextView grado;
    EditText editTextEstudianteSeleccionado;
    TextView textViewestudianteSeleccionado;
    Button buttonCargarDB;
    AlertDialog.Builder builder;
    Button btnNovedades;
    String telefonoAcudiente;
    String asunto=null;
    Calendar cal;
    int dia=0;
    int mes=0;
    int ano=0;
    int hora=0;
    int minuto=0;
    String usuario;





    int tipoAccion=0;
    boolean showBotonCargarEstudiantes=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setContentView(R.layout.activity_listado_estudiantes);


        estudianteSeleccionado=(EditText)findViewById(R.id.editText_estudianteSeleccionado);
        gradoEstudiante=(EditText)findViewById(R.id.editText_gradoEstudiante);
        movilAcudiente=(EditText)findViewById(R.id.editText_telefonoAcudiente);
        subio=(Button)findViewById(R.id.button_subio);
        bajo=(Button)findViewById(R.id.button_bajo);
        llamar=(Button)findViewById(R.id.llamarAcudiente);
        titulo=(TextView)findViewById(R.id.titulo);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        expListView1 = (ExpandableListView) findViewById(R.id.lvExp1);
        estudianteSelect=(TextView)findViewById(R.id.testudianteSeleccionado);
        eventos=(TextView)findViewById(R.id.textView10);
        editTextTelefonoAcudiente=(EditText)findViewById(R.id.editText_telefonoAcudiente);
        movilAcudiente=(TextView)findViewById(R.id.textView9);
        editTextgrado=(EditText)findViewById(R.id.editText_gradoEstudiante);
        grado=(TextView)findViewById(R.id.textView8);
        editTextEstudianteSeleccionado=(EditText)findViewById(R.id.editText_estudianteSeleccionado);
        textViewestudianteSeleccionado=(TextView)findViewById(R.id.testudianteSeleccionado);
        buttonCargarDB=(Button)findViewById(R.id.buttonCargarDB);
        new GPSTracker(ListadoEstudiantes.this);
        estudianteSeleccionado.setKeyListener(null);
        gradoEstudiante.setKeyListener(null);
        editTextTelefonoAcudiente.setKeyListener(null);
        btnNovedades=(Button)findViewById(R.id.btnNovedades);

        buttonCargarDB.setVisibility(View.GONE);
        if (!showBotonCargarEstudiantes){
            //buttonCargarDB.setVisibility(View.GONE);
        }

        hideControls(true);

        Bundle bundle=getIntent().getExtras();
        usuario=bundle.getString("usuario").toString();
        ruta=bundle.getString("ruta").toString();
        Log.d(LOGTAG, "usuario rx: " + usuario);
        Log.d(LOGTAG, "ruta rx: " + ruta);
        titulo.setText("Listado Estudiantes Ruta " + ruta);
        builder = new AlertDialog.Builder(this);

        buttonCargarDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        llamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + editTextTelefonoAcudiente.getText().toString()));
                startActivity(intent);
            }
        });

        btnNovedades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latitude  = GPSTracker.latitude; // latitude
                longitude = GPSTracker.longitude; // latitude
                DecimalFormat numberFormat = new DecimalFormat("#.#####");
                String strLatitud=numberFormat.format(latitude);
                strLatitud=strLatitud.replace(",",".");
                String strLongitud=numberFormat.format(longitude);
                strLongitud=strLongitud.replace(",",".");
                Calendar cal = Calendar.getInstance();
                int dia=cal.get(Calendar.DAY_OF_MONTH);
                int mes=cal.get(Calendar.MONTH);
                int ano=cal.get(Calendar.YEAR);
                int hora=cal.get(Calendar.HOUR_OF_DAY);
                int minuto=cal.get(Calendar.MINUTE);



                Intent i=new Intent(getApplicationContext(), com.coltrack.controlrutasapimin.eventos.class);
                i.putExtra("estudiante", estudiante);
                i.putExtra("ruta", ruta);
                i.putExtra("correo",correoSeleccionado);
                i.putExtra("telefonoAcudiente",telefonoAcudiente);
                String ubicacion =  "http://maps.google.es/maps?q=" + strLatitud + "," + strLongitud;
                String fecha=dia+"/"+mes+"/"+ano;
                String hour=hora+":"+minuto;
                i.putExtra("fecha",fecha);
                i.putExtra("hora",hour);
                i.putExtra("posicion",ubicacion);
                startActivity(i);
                hideControls(true);


            }
        });

        myDB = this.openOrCreateDatabase("estudiantesRenetur", MODE_PRIVATE, null);
        top250 = new ArrayList<String>();
        top251 = new ArrayList<String>();
        pupulateItems();

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //Subir estudiantes ruta
                tipoAccion=1;
                showBotonCargarEstudiantes=false;
                hideControls(false);
                subio.setEnabled(true);bajo.setEnabled(false);
                estudianteSelect.setText("Estudiante Seleccionado subir:");
                estudiante = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                //Toast.makeText(getApplicationContext(), estudiante, Toast.LENGTH_SHORT).show();
                estudianteSeleccionado.setText(estudiante);
                c=myDB.rawQuery("SELECT * FROM estudiantes WHERE nombre=" + "'" + estudiante + "'", null);
                c.moveToFirst();
                int column = c.getColumnIndex("grado");
                int column1=c.getColumnIndex("correoAcudiente");
                if (c != null) {
                    do {
                        String grado = null;
                        grado = c.getString(column);
                        correoSeleccionado=c.getString(column1);
                        Log.d(LOGTAG, "grado: " + grado);
                        Log.d(LOGTAG,"Correo Seleccionado: "+correoSeleccionado);
                        gradoEstudiante.setText(grado);
                    } while (c.moveToNext());
                }

                c=myDB.rawQuery("SELECT telefonoAcudiente FROM estudiantes WHERE nombre=" + "'" + estudiante + "'", null);
                c.moveToFirst();
                column = c.getColumnIndex("telefonoAcudiente");
                if (c != null) {
                    do {
                        telefonoAcudiente = null;
                        telefonoAcudiente = c.getString(column);
                        Log.d(LOGTAG, "telefonoAcudiente: " + telefonoAcudiente);
                        editTextTelefonoAcudiente.setText(telefonoAcudiente);
                    } while (c.moveToNext());
                }
                parent.collapseGroup(groupPosition);
                //v.setBackgroundColor(Color.parseColor("#ffc600"));
                bajo.setVisibility(View.GONE);
                return false;
            }
        });
        expListView1.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //bajar estudiantes ruta
                tipoAccion=2;
                showBotonCargarEstudiantes=false;
                hideControls(false);
                bajo.setEnabled(true);subio.setEnabled(false);
                estudianteSelect.setText("Estudiante Seleccionado bajar:");
                estudiante = listDataChild1.get(listDataHeader1.get(groupPosition)).get(childPosition);
                //Toast.makeText(getApplicationContext(), estudiante, Toast.LENGTH_SHORT).show();
                estudianteSeleccionado.setText(estudiante);
                c=myDB.rawQuery("SELECT * FROM estudiantes WHERE nombre=" + "'" + estudiante + "'", null);
                c.moveToFirst();
                int column = c.getColumnIndex("grado");
                int column1=c.getColumnIndex("correoAcudiente");
                if (c != null) {
                    do {
                        String grado = null;
                        grado = c.getString(column);
                        correoSeleccionado=c.getString(column1);
                        Log.d(LOGTAG, "grado: " + grado);
                        Log.d(LOGTAG,"Correo Seleccionado: "+correoSeleccionado);
                        gradoEstudiante.setText(grado);
                    } while (c.moveToNext());
                }

                c=myDB.rawQuery("SELECT telefonoAcudiente FROM estudiantes WHERE nombre=" + "'" + estudiante + "'", null);
                c.moveToFirst();
                column = c.getColumnIndex("telefonoAcudiente");
                if (c != null) {
                    do {
                        telefonoAcudiente = null;
                        telefonoAcudiente = c.getString(column);
                        Log.d(LOGTAG, "telefonoAcudiente: " + telefonoAcudiente);
                        editTextTelefonoAcudiente.setText(telefonoAcudiente);
                    } while (c.moveToNext());
                }
                parent.collapseGroup(groupPosition);
                //v.setBackgroundColor(Color.parseColor("#ffc600"));
                subio.setVisibility(View.GONE);
                btnNovedades.setVisibility(View.GONE);
                return false;
            }
        });

        subio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leerFechaHora();
                Log.d(LOGTAG, "Actualizando DB....");
                myDB.execSQL("UPDATE estudiantes SET subio='SI' WHERE nombre=" + "'" + estudiante + "'");
                asunto="Evento: estudiante "+estudiante+" subio ruta "+ruta+" "+hora+":"+minuto+" "+dia+"/"+mes+"/"+ano;
                //revisando DB
                c=myDB.rawQuery("SELECT * FROM estudiantes WHERE subio='SI'", null);
                int Column1 = c.getColumnIndex("nombre");
                int Column2 = c.getColumnIndex("subio");
                c.moveToFirst();
                String Data;
                Log.d(LOGTAG, "Estudiantes que ya subieron: ");
                if (c != null && c.getCount()>0) {
                    do {
                        Data=c.getString(Column1)+'\t'+c.getString(Column2);
                        Log.d(LOGTAG, Data);
                    }while(c.moveToNext());
                }
                pupulateItems();
                latitude  = GPSTracker.latitude; // latitude
                longitude = GPSTracker.longitude; // latitude
                BackgroundTask bkgTask = new BackgroundTask();
                bkgTask.execute();
                hideControls(true);
            }
        });
        bajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leerFechaHora();
                myDB.execSQL("UPDATE estudiantes SET subio='NO' WHERE nombre=" + "'" + estudiante + "'");
                //revisando DB
                asunto="Evento: estudiante "+estudiante+" bajo ruta "+ruta+" "+hora+":"+minuto+" "+dia+"/"+mes+"/"+ano;
                c=myDB.rawQuery("SELECT * FROM estudiantes WHERE subio='NO'", null);
                int Column1 = c.getColumnIndex("nombre");
                int Column2 = c.getColumnIndex("subio");
                c.moveToFirst();
                String Data;
                Log.d(LOGTAG, "Estudiantes que se han bajado: ");
                if (c != null && c.getCount()>0) {
                    do {
                        Data=c.getString(Column1)+'\t'+c.getString(Column2);
                        Log.d(LOGTAG, Data);
                    }while(c.moveToNext());
                }
                pupulateItems();
                latitude  = GPSTracker.latitude; // latitude
                longitude = GPSTracker.longitude; // latitude
                BackgroundTask bkgTask1 = new BackgroundTask();
                bkgTask1.execute();
                hideControls(true);
                //subio.setVisibility(View.GONE);
            }
        });
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Chao", Toast.LENGTH_SHORT).show();
        if (myDB.isOpen()) {
            myDB.close();
        }
    }

    public void leerServerDB(){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ruta", ruta);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("json", jsonObject.toString()));

            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost httpPost = new HttpPost("http://107.170.38.31/phpDir/holaphp.php");
            HttpPost httpPost = new HttpPost("http://107.170.38.31/phpDir/apiEstudiantesRenetur1.php");
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                String jsonResult = inputStreamToString(httpResponse.getEntity().getContent()).toString();
                Log.d(LOGTAG, "jsonResult: " + jsonResult);
                String json=jsonResult;
                json=json.replace("[", "");
                json=json.replace("]", "");

                int nrows=countOccurrences(json, '{');
                Log.d(LOGTAG, "nrows: " + nrows);
                String[]parts=json.split(Pattern.quote("}"));
                Log.d(LOGTAG,"Creando DB...");
                //SQLiteDatabase myDB = this.openOrCreateDatabase("DatabaseName", MODE_PRIVATE, null);
                myDB = this.openOrCreateDatabase("estudiantesRenetur", MODE_PRIVATE, null);
                myDB.execSQL("DROP TABLE IF EXISTS estudiantes");//borramos tabla

                myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                        + "estudiantes"
                        + " (nombre TEXT, grado TEXT, nombreAcudiente TEXT, telefonoAcudiente TEXT, correoAcudiente TEXT, subio TEXT, ruta TEXT);");
                for (int i=0;i<nrows;i++){
                    parts[i]=parts[i].replace("{", "");
                    if (i>0){
                        parts[i]=parts[i].substring(1);
                    }
                    parts[i]=parts[i].replace("\"", "");
                    Log.d(LOGTAG, "Parte: " + i + ":" + parts[i]);
                    String[] partes=parts[i].split(Pattern.quote(","));
                    String nombre=null;
                    String grado=null;
                    String nombreAcudiente=null;
                    String telefonoAcudiente=null;
                    String correoAcudiente=null;
                    for (int j=0;j<partes.length;j++){                        String[] subParts=partes[j].split(Pattern.quote(":"));
                        Log.d(LOGTAG, "subparte " + j + ":" + subParts[1]);
                        switch (j){
                            case 0:
                                nombre=subParts[1];
                                break;
                            case 1:
                                grado=subParts[1];
                                break;
                            case 2:
                                telefonoAcudiente=subParts[1];
                                break;
                            case 3:
                                nombreAcudiente=subParts[1];
                                break;
                            case 4:
                                correoAcudiente=subParts[1];
                                break;
                        }
                    }
                    myDB.execSQL("INSERT INTO "
                            + "estudiantes"
                            + " (nombre, grado, nombreAcudiente, telefonoAcudiente, correoAcudiente, subio, ruta)"
                            + " VALUES ("+"'"+ nombre+"'" + ", "+"'"+grado+"'"+", "+"'"+nombreAcudiente+"'"+", "+"'"+telefonoAcudiente+"'"+", "+"'"+correoAcudiente +"'"+", "+"'"+"NO" +"'"+", "+"'"+ruta+"'"+");");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void hideControls(boolean state){
        if (state){
            subio.setVisibility(View.GONE);
            bajo.setVisibility(View.GONE);
            llamar.setVisibility(View.GONE);
            eventos.setVisibility(View.GONE);
            editTextTelefonoAcudiente.setVisibility(View.GONE);
            movilAcudiente.setVisibility(View.GONE);
            editTextgrado.setVisibility(View.GONE);
            grado.setVisibility(View.GONE);
            editTextEstudianteSeleccionado.setVisibility(View.GONE);
            textViewestudianteSeleccionado.setVisibility(View.GONE);
            btnNovedades.setVisibility(View.GONE);
        }else {
            subio.setVisibility(View.VISIBLE);
            bajo.setVisibility(View.VISIBLE);
            llamar.setVisibility(View.VISIBLE);
            eventos.setVisibility(View.VISIBLE);
            editTextTelefonoAcudiente.setVisibility(View.VISIBLE);
            movilAcudiente.setVisibility(View.VISIBLE);
            editTextgrado.setVisibility(View.VISIBLE);
            grado.setVisibility(View.VISIBLE);
            editTextEstudianteSeleccionado.setVisibility(View.VISIBLE);
            textViewestudianteSeleccionado.setVisibility(View.VISIBLE);
            btnNovedades.setVisibility(View.VISIBLE);
        }

    }


    public void pupulateItems(){
        //LIstado estudiantes subieron ruta
        // get the listview


        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        // Adding child data
        listDataHeader.add("SUBIR ESTUDIANTE");
        top250.clear();
        c = myDB.rawQuery("SELECT * FROM " + "estudiantes WHERE subio='NO'", null);
        int Column1=0;int Column2=0;int Column3=0;int Column4=0;int Column5=0;

        Column1 = c.getColumnIndex("nombre");
        Column2 = c.getColumnIndex("grado");
        Column3 = c.getColumnIndex("nombreAcudiente");
        Column4 = c.getColumnIndex("telefonoAcudiente");
        Column5 = c.getColumnIndex("correoAcudiente");
        // Check if our result was valid.
        c.moveToFirst();
        String Data=null;
        if (c != null && c.getCount()>0) {
            // Loop through all Results
            do {
                Data=c.getString(Column1)+'\t'+c.getString(Column2)+'\t'+c.getString(Column3)+'\t'+c.getString(Column4)+'\t'+c.getString(Column5);
                Log.d(LOGTAG, Data);
                //top250.add(c.getString(Column1)+" Grado: "+c.getString(Column2));
                top250.add(c.getString(Column1));
            }while(c.moveToNext());
        }
        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listAdapter=new com.coltrack.controlrutasapimin.ExpandableListAdapter(this, listDataHeader, listDataChild);
        //listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);

        //Listado estudiantes bajaron ruta
        // get the listview
        listDataHeader1 = new ArrayList<String>();
        listDataChild1 = new HashMap<String, List<String>>();
        // Adding child data
        listDataHeader1.add("BAJAR ESTUDIANTE");

        top251.clear();
        c = myDB.rawQuery("SELECT * FROM " + "estudiantes WHERE subio='SI'", null);
        Column1 = c.getColumnIndex("nombre");
        Column2 = c.getColumnIndex("grado");
        Column3 = c.getColumnIndex("nombreAcudiente");
        Column4 = c.getColumnIndex("telefonoAcudiente");
        Column5 = c.getColumnIndex("correoAcudiente");
        c.moveToFirst();
        Data=null;
        if (c != null && c.getCount()>0){
            // Loop through all Results
            do {
                top251.add(c.getString(Column1));
            }while(c.moveToNext());
        }
        listDataChild1.put(listDataHeader1.get(0), top251); // Header, Child data
        listAdapter1=new com.coltrack.controlrutasapimin.ExpandableListAdapter(this, listDataHeader1, listDataChild1);
        expListView1.setAdapter(listAdapter1);

    }

    private class NetTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(ListadoEstudiantes.this, "Leyendo datos de servidor Coltrack", "Conectando...");
            Log.d(LOGTAG,"pre  execute");
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean status=true;
            leerServerDB();
            //pupulateItems();
            return status;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(LOGTAG,"post execute");
            pupulateItems();
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if (!result) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //update ui here
                        // display toast here
                        Toast.makeText(ListadoEstudiantes.this,"Error cargando datos de servidor!!!!",Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(ListadoEstudiantes.this,"Datos leidos correctamente desde servidor!!!!",Toast.LENGTH_SHORT).show();
            }
        }


    }


    private class BackgroundTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(ListadoEstudiantes.this, "Enviando Notificacion", "Conectando...");
            Log.d(LOGTAG, "pre  execute");
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean status=false;
            Log.d(LOGTAG, "doing");
            jsonObject = new JSONObject();
            try {
                leerFechaHora();
                DecimalFormat numberFormat = new DecimalFormat("#.#####");
                String strLatitud=numberFormat.format(latitude);
                strLatitud=strLatitud.replace(",",".");
                String strLongitud=numberFormat.format(longitude);
                strLongitud=strLongitud.replace(",",".");
                String ubicacion =  "http://maps.google.es/maps?q=" + strLatitud + "," + strLongitud;
                if (tipoAccion==1) {
                    cuerpoMensaje = "Buenos dias, el estudiante " + estudiante + " se ha subido a la ruta " + ruta + " a las " + hora + ":" + minuto + " del " + dia + "/" + mes + "/" + ano
                            + " en la ubicacion: " + ubicacion;
                }
                if (tipoAccion==2){
                    cuerpoMensaje = "Buenos dias, el estudiante " + estudiante + " se ha bajado de la ruta " + ruta + " a las " + hora + ":" + minuto + " del " + dia + "/" + mes + "/" + ano
                            + " en la ubicacion: " + ubicacion;
                }
                Log.d(LOGTAG, "body: " + cuerpoMensaje);
                jsonObject.put("correo",correoSeleccionado);

                jsonObject.put("asunto",asunto);
                jsonObject.put("cuerpoMensaje",cuerpoMensaje);
                //notificaciones de eventos:
                Log.d(LOGTAG,"Usuario: "+usuario);
                jsonObject.put("usuario",usuario);
                jsonObject.put("ruta",ruta);
                jsonObject.put("nombreEstudiante",estudiante);
                if (tipoAccion==1) {
                    jsonObject.put("evento", "subio");
                }
                if (tipoAccion==2) {
                    jsonObject.put("evento", "bajo");
                }
                jsonObject.put("latitud",strLatitud);
                jsonObject.put("longitud",strLongitud);
                //SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                Log.d(LOGTAG, "date: " + currentDateandTime);
                jsonObject.put("date",currentDateandTime);





                Log.d(LOGTAG,"Enviando datos de subida a servidor....");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("json", jsonObject.toString()));
                String response = makePOSTRequest("http://107.170.38.31/phpDir/sendmail.php", nameValuePairs );
                Log.d(LOGTAG,"mail php response: "+response);
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
                        Toast.makeText(ListadoEstudiantes.this,"Problema al enviar el correo...",Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(ListadoEstudiantes.this,"Notificacion enviada correctamente!",Toast.LENGTH_SHORT).show();
                subio.setEnabled(false);bajo.setEnabled(false);
            }
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
    public static int countOccurrences(String haystack, char needle)
    {
        int count = 0;
        for (int i=0; i < haystack.length(); i++)
        {
            if (haystack.charAt(i) == needle)
            {
                count++;
            }
        }
        return count;
    }
    public void leerFechaHora(){
        cal = Calendar.getInstance();
        dia=cal.get(Calendar.DAY_OF_MONTH);
        mes=cal.get(Calendar.MONTH);
        ano=cal.get(Calendar.YEAR);
        hora=cal.get(Calendar.HOUR_OF_DAY);
        minuto=cal.get(Calendar.MINUTE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cargarEstudiantes:
                //Toast.makeText(getApplicationContext(),"Cargnado estudiantes de servidor",Toast.LENGTH_LONG).show();
                builder.setTitle("Desea cargar listado estudiantes desde el servidor");
                builder.setMessage("Confirmar?");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do do my action here
                        //leerServerDB();
                        NetTask NetTaskk=new NetTask();
                        NetTaskk.execute();

                        Toast.makeText(getApplicationContext(),"Listado cargado con exito desde servidor",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // I do not need any action here you might
                        Toast.makeText(getApplicationContext(),"No se cargo el listado desde el servidor!!",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            case R.id.Ayuda:
                Toast.makeText(getApplicationContext(),"Ayuda",Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
