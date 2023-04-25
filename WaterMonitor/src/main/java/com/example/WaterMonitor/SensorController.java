package com.example.WaterMonitor;

import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RestController
@CrossOrigin
@RequestMapping("/sensors")
public class SensorController {

    @Autowired
    private MyService myService;

    @GetMapping("/data")
    public SensorData getSensorData() throws InterruptedException {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sensor");
        System.err.println(ref);
        CountDownLatch latch = new CountDownLatch(1);
        SensorData[] sensorData = new SensorData[1];
        System.err.println(sensorData[0]);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sensorData[0] = myService.dataSnapshotToSensorData(dataSnapshot);
                System.err.println("Data:  " + sensorData[0]);
                SensorData sensorData = dataSnapshot.getValue(SensorData.class);
                System.out.println("Temperature: " + sensorData.getTemperature());
                System.out.println("EC Value: " + sensorData.getEcValue());
                System.out.println("Turbidity: " + sensorData.getTurbidity());
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error fetching sensor data: " + databaseError.getMessage());
                latch.countDown();
            }
        });

        latch.await();
        return sensorData[0];
    }

    @GetMapping("/check-water-drinkability")
    public String checkWaterDrinkability() throws InterruptedException {
        SensorData sensorData = getSensorData();

        if (myService.isWaterDrinkable(sensorData)) {
            return "Water is drinkable";
        } else {
            return "Water is not drinkable";
        }
    }

    @GetMapping("/drinkability-forecast")
    public double getDrinkabilityForecast() {
        System.out.println("Received request for drinkability forecast");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sensor");
        CountDownLatch latch = new CountDownLatch(1);
        List<SensorData> sensorDataList = new ArrayList<>();

        ref.limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SensorData sensorData = myService.dataSnapshotToSensorData(snapshot);
                    sensorDataList.add(sensorData);
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error fetching sensor data: " + databaseError.getMessage());
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }

        double avgDrinkabilityScore = calculateAvgDrinkabilityScore(sensorDataList);
        double expectedChange = calculateExpectedChange(sensorDataList);
        String drinkabilityStatus = determineDrinkabilityStatus(avgDrinkabilityScore + expectedChange);

        return avgDrinkabilityScore + expectedChange;
    }

    private double calculateAvgDrinkabilityScore(List<SensorData> sensorDataList) {
        double sum = 0;
        for (SensorData sensorData : sensorDataList) {
            if (myService.isWaterDrinkable(sensorData)) {
                sum += 1;
            }
        }
        return sum / sensorDataList.size();
    }

    private double calculateExpectedChange(List<SensorData> sensorDataList) {
        if (sensorDataList.size() < 2) {
            return 0;
        }

        double currentScore = calculateAvgDrinkabilityScore(sensorDataList);
        double previousScore = calculateAvgDrinkabilityScore(sensorDataList.subList(0, sensorDataList.size() - 1));

        return currentScore - previousScore;
    }

    private String determineDrinkabilityStatus(double score) {
        if (score > 0.8) {
            return "Very Good";
        } else if (score > 0.6) {
            return "Good";
        } else if (score > 0.4) {
            return "Fair";
        } else if (score > 0.2) {
            return "Poor";
        } else {
            return "Very Poor";
        }
    }

}
