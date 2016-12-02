package io.mariachi.bastoniotbeacon;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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


public class MainActivity extends AppCompatActivity {


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
    }

    public void enviar(View v)
    {
        Retrofit retrofitUbidot = new Retrofit.Builder()
                .baseUrl("http://things.ubidots.com")
                .build();

        JSONObject corazon = new JSONObject();
        JSONObject giro = new JSONObject();
        JSONObject cabeceo = new JSONObject();

        Random rand = new Random();

        try {
            //TODO sustituir los random por los datos del arduino
            corazon.put("value", rand.nextInt(100));
            giro.put("value", rand.nextInt(100));
            cabeceo.put("value", rand.nextInt(100));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody bodyCorazon = RequestBody.create(JSON, corazon.toString());
        RequestBody bodyGiro = RequestBody.create(JSON, giro.toString());
        RequestBody bodyCabeceo = RequestBody.create(JSON, cabeceo.toString());

        Toast.makeText(this, "datacora: "+corazon.toString()+"\ndataGiro: "+giro.toString()+"\ndataCabeceo: "+cabeceo.toString(), Toast.LENGTH_LONG).show();

        ApiService access = retrofitUbidot.create(ApiService.class);
        Call<ResponseBody> cora = access.setCorazon(bodyCorazon);
        cora.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
        Call<ResponseBody> gir = access.setGiro(bodyGiro);
        gir.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
        Call<ResponseBody> cabe = access.setCabeceo(bodyCabeceo);
        cabe.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }
}