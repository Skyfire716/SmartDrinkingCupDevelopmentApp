package com.jonas.weigand.thesis.smartdrinkingcup;

public interface IIMUUpdate {

    void IMUAccelUpdate(float ax, float ay, float az);
    void IMUGyroUpdate(float gx, float gy, float gz);
}
