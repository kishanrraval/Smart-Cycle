package android.ahduni.seas.myiot;

import java.util.ArrayList;

/**
 * Created by kisha on 29/09/2017.
 */

public class Data
{
    public Double heart, speed;
    ArrayList<Double> lat, lng;

    public Data()
    {
        heart = 0.0;
        speed = 0.0;
        lat = new ArrayList<>();
        lng = new ArrayList<>();
    }
}
