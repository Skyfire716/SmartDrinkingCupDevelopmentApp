package com.jonas.weigand.thesis.smartdrinkingcup.gl;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class STLVector3D {

    private static final String TAG = "STLVector3d";
    private float x;
    private float y;
    private float z;

    public STLVector3D(byte[] bytes){
        if (bytes.length != 12){
            Log.d(TAG, "bytes hat invalid lenght " + bytes.length + " should be 12");
            return;
        }
        this.x = ByteBuffer.wrap(bytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        this.y = ByteBuffer.wrap(bytes, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        this.z = ByteBuffer.wrap(bytes, 8, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    public STLVector3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float[] getFloats(){
        return new float[]{x, y, z};
    }

    @Override
    public String toString() {
        return "STLVector3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
