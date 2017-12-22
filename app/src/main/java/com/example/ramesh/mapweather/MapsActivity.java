package com.example.ramesh.mapweather;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    EditText loc;
    String serverURL="http://dataservice.accuweather.com/locations/v1/search?apikey=&q=";//add api
    String weatherp1="http://dataservice.accuweather.com/forecasts/v1/hourly/1hour/";
    String weatherp2="?apikey=";//add api
    String lockey;
    String plname;
    String strloc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void changePos(View view)
    {
        loc=(EditText) findViewById(R.id.locate);
        strloc=loc.getText().toString();
        String query=serverURL+strloc;
        new DownloadTask().execute(query);
        String weather=weatherp1+lockey+weatherp2;
        new WeatherTask().execute(weather);
    }

    private class DownloadTask extends AsyncTask<String, Void, Void>
    {
        JSONObject jobj;
        StringBuffer stringBuffer;

        @Override
        protected Void doInBackground(String... params) {
            URLConnection urlConn=null;
            BufferedReader bufferedReader=null;
            try
            {
                URL url = new URL(params[0]);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuffer.append(line);
                }
                String str=stringBuffer.toString();
                jobj= new JSONObject(str.substring(str.indexOf("{"), str.lastIndexOf("}") + 1));
            }
            catch(Exception ex) {}

            finally
            {
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return  null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            JSONObject gp;
            String lat="34",lon="-151";
            double latval,lonval;
            if(jobj==null)
                Toast.makeText(getApplicationContext(), "Location not found!", Toast.LENGTH_SHORT).show();
            else {
                try {
                    lockey = jobj.getString("Key");
                    plname = jobj.getString("EnglishName");
                    gp = jobj.getJSONObject("GeoPosition");
                    lat = gp.getString("Latitude");
                    lon = gp.getString("Longitude");
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    latval = Double.parseDouble(lat);
                    lonval = Double.parseDouble(lon);
                    LatLng ll = new LatLng(latval, lonval);
                    mMap.addMarker(new MarkerOptions().position(ll).title(plname));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
                }
            }

        }
    }

    private class WeatherTask extends AsyncTask<String,Void,Void>
    {
        JSONObject jobj;
        StringBuffer stringBuffer;

        @Override
        protected Void doInBackground(String... params)
        {
            URLConnection urlConn=null;
            BufferedReader bufferedReader=null;
            try
            {
                URL url=new URL(params[0]);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuffer.append(line);
                }
                String str=stringBuffer.toString();
                jobj= new JSONObject(str.substring(str.indexOf("{"), str.lastIndexOf("}") + 1));

            }
            catch(Exception e) {}
            finally
            {
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            String wphrase="";
            String temp="";
            if(jobj==null)
                Toast.makeText(getApplicationContext(), "weather unavailable", Toast.LENGTH_SHORT).show();
            else {
                try {
                    wphrase = jobj.getString("IconPhrase");
                    JSONObject jtemp = jobj.getJSONObject("Temperature");
                    String tval = Integer.toString(jtemp.getInt("Value"));
                    String tunit = jtemp.getString("Unit");
                    temp = tval + ((char) 176) + tunit;
                }
                catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(), "onpostexec cannot retreive data", Toast.LENGTH_SHORT).show();
                }
                finally
                {
                    TextView tv = (TextView) findViewById(R.id.textView);
                    String disp=plname + ": " + wphrase + ", " + temp;
                    tv.setText(disp);
                }
            }

        }
    }
}
