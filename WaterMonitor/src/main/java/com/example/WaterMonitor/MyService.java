package com.example.WaterMonitor;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

@Service
public class MyService {
    private final FirebaseDatabase firebaseDatabase;
    @Autowired
    private FirebaseApp firebaseApp;

    @Autowired
    public MyService(FirebaseApp firebaseApp) {
        firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
    }

    public void readData() {
        DatabaseReference ref = firebaseDatabase.getReference("Sensor");
        System.err.println("MyService Ref:" + ref);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object data = dataSnapshot.getValue();
                System.out.println("---- Data: " + data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }

    public boolean isWaterDrinkable(SensorData sensorData) {
        if (sensorData == null) {
            return false;
        }

        // Add your conditions for drinkable water using sensorData object
        // Example:
        boolean isTemperatureOk = sensorData.getTemperature() >= 0 && sensorData.getTemperature() <= 50;
        boolean isEcOk = sensorData.getEcValue() >= 0 && sensorData.getEcValue() <= 1;
        boolean isTurbidityOk = sensorData.getTurbidity() >= 0 && sensorData.getTurbidity() <= 5;

        return isTemperatureOk && isEcOk && isTurbidityOk;
    }

    public SensorData dataSnapshotToSensorData(DataSnapshot dataSnapshot) {
        Gson gson = new Gson();
        String jsonData = gson.toJson(dataSnapshot.getValue());
        SensorData sensorData = gson.fromJson(jsonData, SensorData.class);
        return sensorData;
    }
}
