package com.example.location_v3;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.opencsv.CSVWriter;

import static android.os.SystemClock.sleep;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.*;


public class MainActivity extends AppCompatActivity implements LocationListener {
    TextView tv_speed;
    TextView tv_time;
    Button btn_start;
    Clocation myLocation = null;
    Button btn_save;
    Button btn_reset;
    LocationManager locationManager;
    boolean Start_status = false;
    List<String[]> Result = new ArrayList<>();
    CircularProgressBar circularProgressBar;
//    List<Location> GPXresult = new ArrayList<>();
    private final String TAG = "AppLog";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestMultiPermission();
        tv_speed = findViewById(R.id.tv_speed);
        tv_time = findViewById(R.id.tv_time);
        btn_save = findViewById(R.id.btn_save);
        btn_start = findViewById(R.id.btn_start);
        btn_reset = findViewById(R.id.btn_reset);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestMultiPermission();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        btn_start.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Start();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        toSaveCSV();
                    } else {
                        // TODO
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        circularProgressBar = findViewById(R.id.circularProgressBar);
    }


    private void requestMultiPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(permissions[2]) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(permissions[3]) == PackageManager.PERMISSION_GRANTED
            ) {
                // TODO do when permission accessed
            } else {
                requestPermissions(permissions, REQUEST_PERMISSION_CODE);
            }
        } else {

        }
    }

    private void Start() {
        if (Start_status == false) {
            Start_status = true;
            btn_start.setText("Stop");
            btn_start.setTextColor(Color.RED);
        } else {
            Start_status = false;
            btn_start.setText("Start");
            btn_start.setTextColor(Color.rgb(128, 203, 196));

        }
        Log.d("main109", String.valueOf(Start_status));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Thread thread = new Thread(() -> {
            while (true) {
                Log.d(TAG+"130", String.valueOf(myLocation == null));
                if ((myLocation != null) && (Start_status)) {
                    updateSpeed(myLocation);
                }
                sleep(1000);
            }
        });
        thread.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                // TODO do when permission accessed
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if ((location != null) && (Start_status)) {
            myLocation = new Clocation(location);
        }
    }

    private void updateSpeed(Clocation location) {
        Log.d(TAG+"157",location.toString());
        if (location != null) {
            runOnUiThread(() -> {
                double strLatitude = 0;
                double strLongitude = 0;
                String CurrentDateTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                strLatitude = location.getLatitude();
                strLongitude = location.getLongitude();
                float strCurrentSpeed = (float) (location.getSpeed() * 3.6);
                circularProgressBar.setProgress(strCurrentSpeed);
                Result.add(new String[]{CurrentDateTime, strCurrentSpeed + "", strLatitude + "", strLongitude + ""});
                tv_speed.setText(String.valueOf(strCurrentSpeed));
                tv_time.setText((CharSequence) CurrentDateTime);
                Log.d("Main156", String.valueOf(Result));

            });
        } else {
            // TODO when location is null
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void toSaveCSV() throws IOException {
//        File file = new File("/storage/sdcard0");
        String currentTime = new SimpleDateFormat("HH_mm_ss", Locale.getDefault()).format(new Date());
        String fileName = currentTime + ".csv";
        CSVWriter writer = new CSVWriter(new FileWriter(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/" + fileName, false));
        int d = Log.d("main183", getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString());
        Toast.makeText(MainActivity.this, "save!", Toast.LENGTH_LONG).show();
        Log.d("Main159", Result.toString());
        writer.writeAll(Result);
        writer.close();

    }

    private static final String[] permissions = new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.MANAGE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    private static final int REQUEST_PERMISSION_CODE = 123986;
}
