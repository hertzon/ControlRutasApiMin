package com.coltrack.controlrutasapimin;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.HashMap;
import java.util.List;

public class ListadoEstudiantes extends AppCompatActivity {
    String LOGTAG="log";
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    String ruta;
    EditText codigoEstudiante;
    EditText movilAcudiente;
    EditText estudianteSeleccionado;
    Button subio;
    Button bajo;
    Button subioSms;
    Button bajoSms;
    Button llamar;
    double longitude=0;
    double latitude=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setContentView(R.layout.activity_listado_estudiantes);
    }
}
