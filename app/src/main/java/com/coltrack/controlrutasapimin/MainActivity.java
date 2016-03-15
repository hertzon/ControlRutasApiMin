package com.coltrack.controlrutasapimin;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase myDB= null;
    String TableName = "myTable";
    String LOGTAG="log";
    String Data="";
    EditText editTextUsuario;
    EditText editTextPassword;
    EditText editTextRuta;
    Button buttonIngresar;
    String strUsuario;
    String strPass;
    String strRuta;
    JSONObject jsonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setContentView(R.layout.activity_main);
        editTextUsuario=(EditText)findViewById(R.id.editTextUsuario);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        editTextRuta=(EditText)findViewById(R.id.editTextRuta);
        buttonIngresar=(Button)findViewById(R.id.buttonIngresar);

        buttonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strUsuario=editTextUsuario.getText().toString();
                strPass=editTextPassword.getText().toString();
                strRuta=editTextRuta.getText().toString();
                boolean isError=false;
                if (TextUtils.isEmpty(editTextUsuario.getText())){
                    editTextUsuario.setError("Ingrese nombre del usuario!!");
                    isError=true;
                }
                if (TextUtils.isEmpty(editTextPassword.getText())){
                    editTextPassword.setError("Ingrese una contraseña!!");
                    isError=true;
                }
                if (TextUtils.isEmpty(editTextRuta.getText())){
                    editTextRuta.setError("Ingrese la ruta!!");
                    isError=true;
                }
                if (!isError) {
                    BackgroundTask bkgTask = new BackgroundTask();
                    bkgTask.execute();
                }else {
                    Toast.makeText(getApplicationContext(),"Revise datos de acceso!!!", Toast.LENGTH_LONG).show();
                }




            }
        });
//        buttonIngresar.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                    if(i == 66) {
//                        editTextPassword.requestFocus();
//                    }
//                    return false;
//            }
//        });



//
//
//        myDB = this.openOrCreateDatabase("DatabaseName", MODE_PRIVATE, null);
//        myDB.execSQL("CREATE TABLE IF NOT EXISTS "
//                + TableName
//                + " (Field1 VARCHAR, Field2 INT(3));");
//        myDB.execSQL("INSERT INTO "
//                + TableName
//                + " (Field1, Field2)"
//                + " VALUES ('Saranga', 22);");
//        Cursor c = myDB.rawQuery("SELECT * FROM " + TableName , null);
//        int Column1 = c.getColumnIndex("Field1");
//        int Column2 = c.getColumnIndex("Field2");
//
//        c.moveToFirst();
//        if (c != null) {
//            do {
//                String Name = c.getString(Column1);
//                int Age = c.getInt(Column2);
//                Data =Data +Name+"/"+Age+"\n";
//                Log.d(LOGTAG,Data);
//            }while(c.moveToNext());
//
//
//        }
//        if (myDB != null){
//            myDB.close();
//        }
    }
    private class BackgroundTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(MainActivity.this, "Estado Conexion Servidor", "Conectando...");
            Log.d(LOGTAG,"pre  execute");
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
                        Toast.makeText(MainActivity.this,"Usuario y/o contraseña invalidos!!!!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean status=false;
            Log.d(LOGTAG, "doing");
            jsonObject = new JSONObject();
            try {
                jsonObject.put("usuario", strUsuario);
                jsonObject.put("password", strPass);
                jsonObject.put("ruta", strRuta);

                //Toast.makeText(getApplicationContext(), json, Toast.LENGTH_LONG).show();
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("json", jsonObject.toString()));
                String response = makePOSTRequest("http://107.170.38.31/phpDir/login.php", nameValuePairs );
                if (response.equals("0")){
                    //Toast.makeText(MainActivity.this,"Usuario y/o contraseña errados!",Toast.LENGTH_SHORT).show();
                    status=false;
                }
                if (response.equals("1")){
                    //Toast.makeText(MainActivity.this,"Bienvenido!",Toast.LENGTH_SHORT).show();
                   Intent i=new Intent(getApplicationContext(),ListadoEstudiantes.class);i.putExtra("usuario",strUsuario);
                   i.putExtra("ruta", strRuta);
                    status=true;
                   try {
                        Thread.sleep(100);
                   } catch (InterruptedException e) {
                        e.printStackTrace();
                   }
                    startActivity(i);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
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

                    //String name = object.getString("nombre");
//                HttpEntity httpEntity = httpResponse.getEntity();
//                response = EntityUtils.toString(httpEntity);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //Log.d(LOGTAG, "POST Server Response >>> " + response);



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

    }
}
