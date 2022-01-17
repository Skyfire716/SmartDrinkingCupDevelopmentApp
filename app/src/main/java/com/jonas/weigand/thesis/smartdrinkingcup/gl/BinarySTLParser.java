package com.jonas.weigand.thesis.smartdrinkingcup.gl;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class BinarySTLParser {

    private static final String TAG = "STLParser";

    public static STLMesh readMesh(Context context){
        AssetManager am = context.getAssets();

        AssetFileDescriptor assetFileDescriptor;
        try {
            //assetFileDescriptor = am.openFd("stl/Nano-RP2040-Connect.stl");
            assetFileDescriptor = am.openFd("stl/tetraeder.stl");
            int size = (int) assetFileDescriptor.getLength();
            Log.d(TAG, "AssetLength: " + size);
            byte[] header = new byte[80];
            byte[] triangle_count = new byte[4];
            byte[] triangle = new byte[50];
            BufferedInputStream buf = new BufferedInputStream(assetFileDescriptor.createInputStream());

            Log.d(TAG, "Read Header Result: " + buf.read(header, 0, header.length));
            Log.d(TAG, "Read Triangle Count Result: " + buf.read(triangle_count, 0, triangle_count.length));
            int triangleCount = ByteBuffer.wrap(triangle_count).order(ByteOrder.LITTLE_ENDIAN).getInt();
            Log.d(TAG, "Parsed Triangle Count to: " + triangleCount);
            STLMesh mesh = new STLMesh(triangleCount);
            for (int i = 0; i < triangleCount; i++) {
                int result = buf.read(triangle, 0, triangle.length);
                if (result != 50){
                    Log.e(TAG, "Cannot read enough bytes to fill triangle");
                }
                mesh.addTriangle(triangle);
            }
            buf.close();
            return mesh;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<STLTriangle> readFile(Context context) {
        AssetManager am = context.getAssets();

        AssetFileDescriptor assetFileDescriptor;
        try {
            assetFileDescriptor = am.openFd("stl/Nano-RP2040-Connect.stl");
            int size = (int) assetFileDescriptor.getLength();
            Log.d(TAG, "AssetLength: " + size);
            byte[] header = new byte[80];
            byte[] triangle_count = new byte[4];
            byte[] triangle = new byte[50];
            BufferedInputStream buf = new BufferedInputStream(assetFileDescriptor.createInputStream());

            Log.d(TAG, "Read Header Result: " + buf.read(header, 0, header.length));
            Log.d(TAG, "Read Triangle Count Result: " + buf.read(triangle_count, 0, triangle_count.length));
            int triangleCount = ByteBuffer.wrap(triangle_count).order(ByteOrder.LITTLE_ENDIAN).getInt();
            Log.d(TAG, "Parsed Triangle Count to: " + triangleCount);
            ArrayList<STLTriangle> stlTriangles = new ArrayList<>();
            for (int i = 0; i < triangleCount; i++) {
                int result = buf.read(triangle, 0, triangle.length);
                if (result != 50){
                    Log.e(TAG, "Cannot read enough bytes to fill triangle");
                }
                stlTriangles.add(new STLTriangle(triangle));
            }
            Log.d(TAG, "Parsed " + stlTriangles.size() + " triangles");
            buf.close();
            return stlTriangles;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
