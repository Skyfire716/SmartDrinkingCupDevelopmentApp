package com.jonas.weigand.thesis.smartdrinkingcup;

public interface IAD5932ConfigChanged {

    void changeConfig(AD5932Config conf);
    void returnToScanDevice();
    void trigger();
    void reset();
}
