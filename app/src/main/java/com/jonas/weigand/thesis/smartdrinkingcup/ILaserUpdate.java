package com.jonas.weigand.thesis.smartdrinkingcup;

public interface ILaserUpdate {
    void updateDistance(float distance, float voltage);
    void gotInterval(byte interval);
}
