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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
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
    Button btn_map;
    LocationManager locationManager;
    boolean Start_status = false;
    List<String[]> Result = new ArrayList<>();
    List<Position> positions = new ArrayList<>();
    CircularProgressBar circularProgressBar;
    private long originalTime = new Date().getTime();


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
        btn_map = findViewById(R.id.btn_map);
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
        btn_save.setOnClickListener(view -> {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    toSaveCSV();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        btn_map.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("positions", (Serializable) positions);
            startActivity(intent);
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
        }
    }

    private void Start() {
        if (!Start_status) {
            Start_status = true;
            btn_start.setText("Stop");
            btn_start.setTextColor(Color.RED);
        } else {
            Start_status = false;
            btn_start.setText("Start");
            btn_start.setTextColor(Color.rgb(128, 203, 196));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Thread thread = new Thread(() -> {
            while (true) {
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
        long timeNowInSecond = new Date().getTime();

        runOnUiThread(() -> {
            double strLatitude;
            double strLongitude;
            String CurrentDateTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            strLatitude = location.getLatitude();
            strLongitude = location.getLongitude();
            float strCurrentSpeed = (float) (location.getSpeed() * 3.6);
            circularProgressBar.setProgress(strCurrentSpeed);
            if (timeNowInSecond >= originalTime + 1000) {
                originalTime = timeNowInSecond;
                positions.add(new Position(location.getLongitude(), location.getLatitude()));
                Result.add(new String[]{CurrentDateTime, strCurrentSpeed + "", strLatitude + "", strLongitude + ""});
            }
            tv_speed.setText(String.valueOf(strCurrentSpeed));
            tv_time.setText(CurrentDateTime);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void toSaveCSV() throws IOException {
        String currentTime = new SimpleDateFormat("HH_mm_ss", Locale.getDefault()).format(new Date());
        String fileName = currentTime + ".csv";
        CSVWriter writer = new CSVWriter(new FileWriter(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/" + fileName, false));
        Toast.makeText(MainActivity.this, "save!", Toast.LENGTH_LONG).show();
        writer.writeAll(Result);
        writer.close();
    }

    private static final String[] permissions = new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.MANAGE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    private static final int REQUEST_PERMISSION_CODE = 123986;
}
