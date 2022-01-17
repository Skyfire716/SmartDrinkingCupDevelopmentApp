package com.jonas.weigand.thesis.smartdrinkingcup;

import android.util.Log;

import java.util.UUID;

public class ApplicationUUIDS {

    public static final UUID UUID_MEASURING_TYPE = UUID.fromString("072585df-390b-434e-ba25-116533274d3e");
    public static final UUID UUID_CONFIGURATION = UUID.fromString("adef44b0-3308-42f7-9bba-65c648d7bf66");
    public static final UUID UUID_IMU_ACCEL =UUID.fromString("cb68774e-5438-4c5a-8937-814be0a249bb");
    public static final UUID UUID_IMU_GYRO = UUID.fromString("cd98f4b7-df60-4d9c-9c87-6a64e19a7c4f");
    public static final UUID UUID_HCSR04_CONTROL = UUID.fromString("9988258d-5663-4e1f-a4bc-a8e211f98e39");
    public static final UUID UUID_WOOODO_BLUETOOTHLE_SUBSCRIPTION_THING = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_DEFAULT_MASK = UUID.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");
    public static final UUID UUID_WAVEFORM =UUID.fromString("cb68774e-5438-4c5a-8937-814be0a249bb");
    public static final UUID UUID_MULTIPLIER =UUID.fromString("cd98f4b7-df60-4d9c-9c87-6a64e19a7c4f");
    public static final UUID UUID_INCREMENT_BY =UUID.fromString("f2207ee2-1b29-419f-8f7d-b079db007255");
    public static final UUID UUID_SYNC_MODE =UUID.fromString("3aaf6709-7f59-4e9c-8041-ef31f8b20791");
    public static final UUID UUID_TRIGGER_MODE =UUID.fromString("1c54116d-d6aa-4d1f-96a4-5827842c3e4f");
    public static final UUID UUID_START_FREQ =UUID.fromString("cbd047db-ed1e-4523-bf97-f29f5b4f4c8d");
    public static final UUID UUID_INC_FREQ =UUID.fromString("e146d40a-6fdf-4887-86e1-0c4875b349e6");
    public static final UUID UUID_INCN =UUID.fromString("6ba8446b-5711-43f6-98e6-2bd314fa56a3");
    public static final UUID UUID_MSBOUT =UUID.fromString("072585df-390b-434e-ba25-116533274d3e");
    public static final UUID UUID_SYNCOUT =UUID.fromString("6a2676fa-20e0-48a7-9cd6-560a48013bfe");
    public static final UUID UUID_POWERSAVING =UUID.fromString("9988258d-5663-4e1f-a4bc-a8e211f98e39");
    public static final UUID UUID_TRIGGER =UUID.fromString("cfe2a28b-3e8a-4023-b2b9-70c773a27fb5");

    public static String lookup(String uuid, String unknownServiceString) {
        Log.d("ApplicationUUIDS", "uuid " + uuid + " ServiceString " + unknownServiceString);
        if (uuid == UUID_CONFIGURATION.toString()){
            if (unknownServiceString == "UnknownCharacteristic"){

            }else if (unknownServiceString == "Unknown_Service"){

            }
            return "Control";
        }
        return "";
    }
}
