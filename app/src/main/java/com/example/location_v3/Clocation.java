package com.example.location_v3;
import android.location.Location;

public class Clocation extends Location{
    public Clocation(Location location){
        super(location);
    }

    public Clocation(String provider) {
        super(provider);
    }

    @Override
    public double getLatitude() {
        return super.getLatitude();
    }

    @Override
    public double getLongitude() {
        return super.getLongitude();
    }

    @Override
    public float getSpeed() {
        return super.getSpeed();
    }

    @Override
    public float getSpeedAccuracyMetersPerSecond() {
        return super.getSpeedAccuracyMetersPerSecond();
    }
}
