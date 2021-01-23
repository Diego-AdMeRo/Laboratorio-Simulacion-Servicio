package co.edu.unipiloto.laboratorio_simulacion_odometro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import co.edu.unipiloto.laboratorio_simulacion_odometro.R;

public class MainActivity extends AppCompatActivity implements ServiceConnection, View.OnClickListener {

    private OdometroService odometro;
    private boolean estaEnlazado = false;
    private TextView lblOdometro, lblOdomentroServicio;
    private Button btnSimulacion, btnHabilitarServicio, btnServicioUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.lblOdometro = (TextView) findViewById(R.id.lbl_odometro);
        this.btnSimulacion = (Button) findViewById(R.id.btn_simulacion);
        this.lblOdomentroServicio = (TextView) findViewById(R.id.lbl_odometro_servicio);
        this.btnHabilitarServicio = (Button) findViewById(R.id.btn_iniciar_servicio_ubicacion);
        this.btnServicioUbicacion = (Button) findViewById(R.id.btn_servicio_ubicacion);

        this.btnSimulacion.setOnClickListener(this);
        this.btnHabilitarServicio.setOnClickListener(this);
        this.btnServicioUbicacion.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnSimulacion.getId()) {
            mostrarDistancia();
        } else if (v.getId() == btnHabilitarServicio.getId()) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            } else {
                odometro.iniciarBusquedaDistancia();
            }
        } else if (v.getId() == btnServicioUbicacion.getId()) {
            mostrarDistanciaServicio();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                odometro.iniciarBusquedaDistancia();
            }
        }
    }

    //-----------------------------------Traer Datos de Servicio------------------------------
    private void mostrarDistancia() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (estaEnlazado && odometro != null) {
                    lblOdometro.setText(String.format(
                            Locale.getDefault(), "%1$,.2f millas", odometro.getDistancia()));
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void mostrarDistanciaServicio() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (estaEnlazado && odometro != null) {
                    lblOdomentroServicio.setText(String.format(
                            Locale.getDefault(), "%1$,.4f metros", odometro.getDistanciaServicio()));
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    //-----------------------Estados del Activity y Creación Servicio------------------------
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this, OdometroService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (estaEnlazado) {
            unbindService(this);
            estaEnlazado = false;
        }
    }

    //------------------------------------Conexión con el Servicio-------------------
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        OdometroService.OdometroBinder odometroBinder = (OdometroService.OdometroBinder) service;
        this.odometro = odometroBinder.getOdometro();
        this.estaEnlazado = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.estaEnlazado = false;
    }

}