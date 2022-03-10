package com.jonas.weigand.thesis.smartdrinkingcup;

import android.util.Log;

import java.util.UUID;

public class ApplicationUUIDS {

    public static final UUID UUID_MEASURING_TYPE = UUID.fromString("072585df-390b-434e-ba25-116533274d3e");
    public static final UUID UUID_CONFIGURATION = UUID.fromString("adef44b0-3308-42f7-9bba-65c648d7bf66");
    public static final UUID UUID_IMU_ACCEL = UUID.fromString("cb68774e-5438-4c5a-8937-814be0a249bb");
    public static final UUID UUID_IMU_GYRO = UUID.fromString("cd98f4b7-df60-4d9c-9c87-6a64e19a7c4f");
    public static final UUID UUID_HCSR04_CONTROL = UUID.fromString("9988258d-5663-4e1f-a4bc-a8e211f98e39");
    public static final UUID UUID_HCSR04_DISTANCE = UUID.fromString("6a2676fa-20e0-48a7-9cd6-560a48013bfe");
    public static final UUID UUID_WOOODO_BLUETOOTHLE_SUBSCRIPTION_THING = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_AD5932_MEASURE_RESULT = UUID.fromString("cbd047db-ed1e-4523-bf97-f29f5b4f4c8d");
    public static final UUID UUID_DEFAULT_MASK = UUID.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");
    public static final UUID UUID_LASER_CONTROL = UUID.fromString("849831dc-427b-4784-8848-1d88ef7e4d44");
    public static final UUID UUID_LASER_DISTANCE = UUID.fromString("670b40c4-df34-4f4a-a59d-242c51e7774a");
    public static final UUID UUID_TRIGGER = UUID.fromString("cfe2a28b-3e8a-4023-b2b9-70c773a27fb5");

    public static String lookup(String uuid, String unknownServiceString) {
        Log.d("ApplicationUUIDS", "uuid " + uuid + " ServiceString " + unknownServiceString);
        if (uuid == UUID_CONFIGURATION.toString()) {
            if (unknownServiceString == "UnknownCharacteristic") {

            } else if (unknownServiceString == "Unknown_Service") {

            }
            return "Control";
        }
        return "";
    }
}
