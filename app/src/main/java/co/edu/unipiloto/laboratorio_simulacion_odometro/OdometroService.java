package co.edu.unipiloto.laboratorio_simulacion_odometro;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class OdometroService extends Service implements LocationListener {

    private final IBinder binder = new OdometroBinder();
    private final Random aleatorio = new Random();
    private static LocationManager locationManager;
    private static Location ultimaUbicacion = null;
    private static double distanciaMetros;

    public void iniciarBusquedaDistancia() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String provedor = locationManager.getBestProvider(new Criteria(), true);
            if (provedor != null) {
                locationManager.requestLocationUpdates(provedor, 1000, 0, this);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
        locationManager = null;
    }

    public double getDistancia() {
        return aleatorio.nextDouble();
    }

    public double getDistanciaServicio() {
        return distanciaMetros;
    }

    @Override
    public void onLocationChanged(Location location) {
        ultimaUbicacion = ultimaUbicacion == null ? location : ultimaUbicacion;
        distanciaMetros += location.distanceTo(ultimaUbicacion);
        ultimaUbicacion = location;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class OdometroBinder extends Binder {

        public OdometroService getOdometro() {
            return OdometroService.this;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
