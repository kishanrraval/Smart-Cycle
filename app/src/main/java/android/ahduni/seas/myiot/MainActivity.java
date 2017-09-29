package android.ahduni.seas.myiot;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.ahduni.seas.myiot.R.id.map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onResume() {
        super.onResume();

        FetchData fetchData = new FetchData();
        fetchData.execute();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);


    }

    Data d = new Data();
    GoogleMap gMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.

        gMap = googleMap;
    }

    public void updateMap()
    {
        TextView t1 = (TextView)findViewById(R.id.heart);
        TextView t2 = (TextView)findViewById(R.id.speed);
        t1.setText("Heart Beat: " + d.heart+"");
        t2.setText("Speed: "+ d.speed+"");
        LatLng myLatlng[] = new LatLng[d.lng.size()];

        for (int i = 1 ; i < myLatlng.length ; i++)
        {
            LatLng temp = new LatLng(d.lat.get(i), d.lng.get(i));
            gMap.addMarker(new MarkerOptions().position(temp).title("Reading" + i));
        }
        //gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(d.lat.get(0), d.lng.get(0))), 12.0f));
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(d.lat.get(1), d.lng.get(1)), 12.0f));


    }


    class FetchData extends AsyncTask<Void, Void, Data>
    {

        public Data getMyObject(JSONObject in) throws JSONException {
            Data out = new Data();
            JSONArray feeds = in.getJSONArray("feeds");

            for(int i = 0 ; i < feeds.length() ; i++)
            {
                JSONObject rec = feeds.getJSONObject(i);
                double a;
                try
                {
                    a = rec.getDouble("field1");
                    out.heart = a;
                }
                catch (JSONException ex)
                {

                }
                try
                {
                    a = rec.getDouble("field4");
                    out.speed = a;
                }
                catch (JSONException ex)
                {

                }
                try
                {
                    a = rec.getDouble("field2");
                    out.lat.add(a);
                }
                catch (JSONException ex)
                {

                }
                try
                {
                    a = rec.getDouble("field3");
                    out.lng.add(a);
                }
                catch (JSONException ex)
                {

                }

            }



            return out;
        }

        @Override
        protected void onPostExecute(Data data)
        {
            super.onPostExecute(data);

            d = data;
            updateMap();
        }



        @Override
        protected Data doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            String dataIn = null;

            try
            {
                final String BASE_URL = "https://api.thingspeak.com/channels/336856/feeds.json";


                URL url = new URL(BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while((line=bufferedReader.readLine()) != null)
                {
                    buffer.append(line + "\n");
                }
                if(buffer.length() == 0)
                {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                dataIn = buffer.toString(); //Storing Data coming into String

                Log.e("LOG TAG","API Data : "+dataIn);

                JSONObject rec_JSON = new JSONObject(dataIn);
                Data dt = getMyObject(rec_JSON);
                return dt;
            }
            catch (MalformedURLException e)
            {
                Log.e("URL Connection error", "Error Closing Stream", e);
            }
            catch (IOException e)
            {
                Log.e("IOException", "Error", e);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e("PlaceHolderFragment","Error Closing Stream", e);
                    }
                }

            }


            return null;
        }
    }

}
