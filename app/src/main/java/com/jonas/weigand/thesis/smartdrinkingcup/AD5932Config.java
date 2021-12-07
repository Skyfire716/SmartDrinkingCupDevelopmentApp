package com.jonas.weigand.thesis.smartdrinkingcup;

import android.util.Log;

import java.util.Objects;

public class AD5932Config {

    private boolean b24;
    private boolean dac_enable;
    private boolean sine;
    private boolean msbout;
    private boolean int_inc;
    private boolean sync_sel;
    private boolean sync_out;
    private boolean inc_on_cycles;
    private int number_of_increments;
    private int delta_frequency;
    private int increment_interval;
    private int start_frequency;
    private byte multiplier;

    private final short CONTROL_MASK = 0x00D3;
    private final short NUMBER_INC_MASK = 0x1000;
    private final short LOWER_DELTA_FREQUENCY_MASK = 0x2000;
    private final short UPPER_DELTA_FREQUENCY_MASK = 0x3000;
    private final short INCREMENT_INVERAL_MASK = 0x4000;
    private final short LOWER_START_FREQUENCY_MASK = 0x0C00;
    private final short UPPER_START_FREQUENCY_MASK = 0x0D00;

    public byte[] getControl() {
        byte[] control = new byte[2];
        short value = CONTROL_MASK;
        value |= ((sync_out ? 1 : 0) << 2);
        value |= ((sync_sel ? 1 : 0) << 3);
        value |= ((int_inc ? 1 : 0) << 5);
        value |= ((msbout ? 1 : 0) << 8);
        value |= ((sine ? 1 : 0) << 9);
        value |= ((dac_enable ? 1 : 0) << 10);
        value |= ((b24 ? 1 : 0) << 11);
        String regval = Integer.toBinaryString(0xFFFF & value);
        Log.d("REGISTER", regval);
        control[0] = (byte) (value & 0xFF);
        control[1] = (byte) ((value & 0xFF00) >> 8);
        if ((value & CONTROL_MASK) != CONTROL_MASK){
            Log.e("MASK NOT MATCHING", "");
        }
        String regval2 = Integer.toBinaryString(0xFF & control[0]);
        Log.d("REGISTER0", regval2);
        String regval3 = Integer.toBinaryString(0xFF & control[1]);
        Log.d("REGISTER1", regval3);
        return control;
    }

    public byte[] getNumberOnIncrements() {
        byte[] num_inc = new byte[2];
        short value = NUMBER_INC_MASK;
        value |= (0x0FFF & number_of_increments);
        if ((value & NUMBER_INC_MASK) != NUMBER_INC_MASK) {
            Log.e("MASK NOT MATCHING", "");
        }
        num_inc[0] = (byte) (value & 0xFF);
        num_inc[1] = (byte) ((value & 0xFF00) >> 8);
        return num_inc;
    }

    public byte[] getLowerDeltaFrequency(){
        byte[] delta_frequency = new byte[2];
        short value = LOWER_DELTA_FREQUENCY_MASK;
        value |= (0x0FFF & (this.delta_frequency < 0 ? (-this.delta_frequency) : this.delta_frequency));
        if ((value & LOWER_DELTA_FREQUENCY_MASK) != LOWER_DELTA_FREQUENCY_MASK) {
            Log.e("MASK NOT MATCHING", "");
        }
        delta_frequency[0] = (byte) (value & 0xFF);
        delta_frequency[1] = (byte) ((value & 0xFF00) >> 8);
        return delta_frequency;
    }

    public byte[] getUpperDeltaFrequency(){
        byte[] delta_frequency = new byte[2];
        short value = UPPER_DELTA_FREQUENCY_MASK;
        if (this.delta_frequency < 0){
            value |= 0x0800;
            value |= ((0x07FF000 & (-this.delta_frequency)) >> 12);
        }else {
            value |= ((0x07FF000 & this.delta_frequency) >> 12);
        }
        if ((value & UPPER_DELTA_FREQUENCY_MASK) != UPPER_DELTA_FREQUENCY_MASK) {
            Log.e("MASK NOT MATCHING", "");
        }
        delta_frequency[0] = (byte) (value & 0xFF);
        delta_frequency[1] = (byte) ((value & 0xFF00) >> 8);
        return delta_frequency;
    }

    public byte[] getIncrementIntervalReg(){
        byte[] inc_int = new byte[2];
        short value = INCREMENT_INVERAL_MASK;
        value |= (inc_on_cycles ? (0x1 << 13) : 0);
        value |= (this.multiplier << 11);
        value |= (0x3FF & this.increment_interval);
        if ((value & INCREMENT_INVERAL_MASK) != INCREMENT_INVERAL_MASK) {
            Log.e("MASK NOT MATCHING", "");
        }
        inc_int[0] = (byte) (value & 0xFF);
        inc_int[1] = (byte) ((value & 0xFF00) >> 8);
        return inc_int;
    }

    public byte[] getLowerStartFrequency(){
        byte[] lowerStart = new byte[2];
        short value = LOWER_DELTA_FREQUENCY_MASK;
        value |= (0x0FFF & start_frequency);
        if ((value & LOWER_DELTA_FREQUENCY_MASK) != LOWER_DELTA_FREQUENCY_MASK) {
            Log.e("MASK NOT MATCHING", "");
        }
        lowerStart[0] = (byte) (value & 0xFF);
        lowerStart[1] = (byte) ((value & 0xFF00) >> 8);
        return lowerStart;
    }

    public byte[] getUpperStartFrequency(){
        byte[] upperStart = new byte[2];
        short value = UPPER_DELTA_FREQUENCY_MASK;
        value |= ((0x0FFF000 & start_frequency) >> 12);
        if ((value & UPPER_DELTA_FREQUENCY_MASK) != UPPER_DELTA_FREQUENCY_MASK) {
            Log.e("MASK NOT MATCHING", "");
        }
        upperStart[0] = (byte) (value & 0xFF);
        upperStart[1] = (byte) ((value & 0xFF00) >> 8);
        return upperStart;
    }

    public byte[] packConfigForTransfer(){
        byte[] transferData = new byte[14];
        byte[] controlReg = getControl();
        byte[] number_of_incReg = getNumberOnIncrements();
        byte[] lower_deltaReg = getLowerDeltaFrequency();
        byte[] upper_deltaReg = getUpperDeltaFrequency();
        byte[] inc_intReg = getIncrementIntervalReg();
        byte[] lower_startReg = getLowerStartFrequency();
        byte[] upper_startReg = getUpperStartFrequency();
        transferData[0] = controlReg[0];
        transferData[1] = controlReg[1];
        transferData[2] = number_of_incReg[0];
        transferData[3] = number_of_incReg[1];
        transferData[4] = lower_deltaReg[0];
        transferData[5] = lower_deltaReg[1];
        transferData[6] = upper_deltaReg[0];
        transferData[7] = upper_deltaReg[1];
        transferData[8] = inc_intReg[0];
        transferData[9] = inc_intReg[1];
        transferData[10] = lower_startReg[0];
        transferData[11] = lower_startReg[1];
        transferData[12] = upper_startReg[0];
        transferData[13] = upper_startReg[1];
        return transferData;
    }

    public void printRegister(byte[] register){
        short value = (short) (((((short)register[1]) << 8) & 0xFF00) | (((short)register[0]) & 0xFF));
        String regval = Integer.toBinaryString(0xFFFF & value);
        Log.d("REGISTER", regval);
    }

    public boolean isDac_enable() {
        return dac_enable;
    }

    public void setDac_enable(boolean dac_enable) {
        this.dac_enable = dac_enable;
    }

    public boolean isSine() {
        return sine;
    }

    public void setSine(boolean sine) {
        this.sine = sine;
    }

    public boolean isMsbout() {
        return msbout;
    }

    public void setMsbout(boolean msbout) {
        this.msbout = msbout;
    }

    public boolean isInt_inc() {
        return int_inc;
    }

    public void setInt_inc(boolean int_inc) {
        this.int_inc = int_inc;
    }

    public boolean isSync_sel() {
        return sync_sel;
    }

    public void setSync_sel(boolean sync_sel) {
        this.sync_sel = sync_sel;
    }

    public boolean isSync_out() {
        return sync_out;
    }

    public void setSync_out(boolean sync_out) {
        this.sync_out = sync_out;
    }

    public boolean isInc_on_cycles() {
        return inc_on_cycles;
    }

    public void setInc_on_cycles(boolean inc_on_cycles) {
        this.inc_on_cycles = inc_on_cycles;
    }

    public int getNumber_of_increments() {
        return number_of_increments;
    }

    public void setNumber_of_increments(int number_of_increments) {
        this.number_of_increments = number_of_increments;
    }

    public int getDelta_frequency() {
        return delta_frequency;
    }

    public void setDelta_frequency(int delta_frequency) {
        this.delta_frequency = delta_frequency;
    }

    public int getIncrement_interval() {
        return increment_interval;
    }

    public void setIncrement_interval(int increment_interval) {
        this.increment_interval = increment_interval;
    }

    public int getStart_frequency() {
        return start_frequency;
    }

    public void setStart_frequency(int start_frequency) {
        this.start_frequency = start_frequency;
    }

    public byte getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(byte multiplier) {
        this.multiplier = multiplier;
    }

    public boolean isB24() {
        return b24;
    }

    @Override
    public String toString() {
        return "AD5932Config{" +
                "b24=" + b24 +
                ", dac_enable=" + dac_enable +
                ", sine=" + sine +
                ", msbout=" + msbout +
                ", int_inc=" + int_inc +
                ", sync_sel=" + sync_sel +
                ", sync_out=" + sync_out +
                ", inc_on_cycles=" + inc_on_cycles +
                ", number_of_increments=" + number_of_increments +
                ", delta_frequency=" + delta_frequency +
                ", increment_interval=" + increment_interval +
                ", start_frequency=" + start_frequency +
                ", multiplier=" + multiplier +
                '}';
    }

    public void setB24(boolean b24) {
        this.b24 = b24;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AD5932Config that = (AD5932Config) o;
        return b24 == that.b24 && dac_enable == that.dac_enable && sine == that.sine && msbout == that.msbout && int_inc == that.int_inc && sync_sel == that.sync_sel && sync_out == that.sync_out && inc_on_cycles == that.inc_on_cycles && number_of_increments == that.number_of_increments && delta_frequency == that.delta_frequency && increment_interval == that.increment_interval && start_frequency == that.start_frequency && multiplier == that.multiplier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(b24, dac_enable, sine, msbout, int_inc, sync_sel, sync_out, inc_on_cycles, number_of_increments, delta_frequency, increment_interval, start_frequency, multiplier);
    }

    public AD5932Config(){

    }

}
