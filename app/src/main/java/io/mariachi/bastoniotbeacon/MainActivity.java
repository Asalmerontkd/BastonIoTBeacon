package io.mariachi.bastoniotbeacon;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity implements LocationListener {
    BluetoothAdapter bluetoothAdapter;
    public double lat, lon;
    private LocationManager locationManager;
    private String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Beacon.class);
                startActivity(i);
            }
        });

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) // If null -> cerrar la aplicación, el dispositivo no tiene bluetooth
        {
            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setMessage("Bluetooth no encontrado en el dispositivo.");
            alerta.setTitle("Bluetooth no encontrado.");
            alerta.setIcon(android.R.drawable.ic_dialog_alert);
            alerta.setCancelable(false);
            alerta.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Cerrar la aplicació por que no se encontró bluetooth.
                    finish();
                }
            });
            alerta.show();
        } else if (!bluetoothAdapter.isEnabled()) //if Bluetooth no esta activo
        {
            AlertDialog.Builder activaBT = new AlertDialog.Builder(this);
            activaBT.setMessage("El bluetooth no se encuentra activo.\n¿Desea activarlo?");
            activaBT.setTitle("Bluetooth desactivado.");
            activaBT.setIcon(android.R.drawable.ic_dialog_alert);
            activaBT.setCancelable(false);
            activaBT.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Activar el bluetooth
                    Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(bluetoothIntent, RESULT_OK);
                }
            });
            activaBT.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Si no se activa el bluetooth, salir de la aplicación
                    finish();
                }
            });
            activaBT.show();
        }
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }


    //TODO separar al corazon por que se envia a cada minuto, giro y cabeceo se envia cuando aaron quiera

    public void enviar(View v) //Metodo para envias datos a UBIDOTS, pasar parametros (corazon, giro, cabeceo) y dejar de depender del boton
    {
        Retrofit retrofitUbidot = new Retrofit.Builder()
                .baseUrl("http://things.ubidots.com")
                .build();

        JSONObject corazon = new JSONObject();
        JSONObject giro = new JSONObject();
        JSONObject cabeceo = new JSONObject();
        JSONObject gpsObject = new JSONObject();

        Random rand = new Random();

        try {
            //TODO sustituir los random por los datos del arduino
            corazon.put("value", rand.nextInt(110));
            gpsObject.put("lat", lat);
            gpsObject.put("lng", lon);
            corazon.put("context",gpsObject);
            giro.put("value", rand.nextInt(200));
            cabeceo.put("value", rand.nextInt(200));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json;charset=utf-8"); //Crea BODY para el POST
        RequestBody bodyCorazon = RequestBody.create(JSON, corazon.toString());
        RequestBody bodyGiro = RequestBody.create(JSON, giro.toString());
        RequestBody bodyCabeceo = RequestBody.create(JSON, cabeceo.toString());

        //Toast.makeText(this, "datacora: "+corazon.toString()+"\ndataGiro: "+giro.toString()+"\ndataCabeceo: "+cabeceo.toString(), Toast.LENGTH_LONG).show();
        Toast.makeText(this, "GPS: "+gpsObject.toString(), Toast.LENGTH_SHORT).show();

        ApiService access = retrofitUbidot.create(ApiService.class);//Instancia a la ApiService


        Call<ResponseBody> cora = access.setCorazon(bodyCorazon); //Enviar datos del corazon
        cora.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


        Call<ResponseBody> gir = access.setGiro(bodyGiro); //Enviar datos del giro
        gir.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


        Call<ResponseBody> cabe = access.setCabeceo(bodyCabeceo); //Envia datos del cabeceo
        cabe.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        lat=location.getLatitude();
        lon=location.getLongitude();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}