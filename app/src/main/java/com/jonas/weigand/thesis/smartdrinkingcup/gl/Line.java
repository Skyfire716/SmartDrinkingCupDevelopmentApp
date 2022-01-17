package com.jonas.weigand.thesis.smartdrinkingcup.gl;

import android.opengl.GLES20;
import android.util.Log;

import com.jonas.weigand.thesis.smartdrinkingcup.CupRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

public class Line {

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";


    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private int vPMatrixHandle;
    private int positionHandle;
    private int colorHandle;
    private int mProgram;
    private FloatBuffer vertexBuffer;
    static final int COORDS_PER_VERTEX = 3;
    private final int vertexCount = 12 / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4;
    private static final String TAG = "LINE";
    float vertexColor[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};
    float colorRed[] = {1.0f, 0.0f, 0.0f, 1.0f};
    float colorGreen[] = {0.0f, 1.0f, 0.0f, 1.0f};
    float colorBlue[] = {0.0f, 0.0f, 1.0f, 1.0f};
    float colorWhite[] = {1.0f, 1.0f, 1.0f, 1.0f};
    byte rgb = 0;
    private ByteBuffer bb;

    public Line(Vector<Float> v1, Vector<Float> v2, byte rgb) {
        bb = ByteBuffer.allocateDirect(4 * COORDS_PER_VERTEX * 2);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        this.rgb = rgb;
        for (Float f : v1) {
            vertexBuffer.put(f);
        }
        for (Float f : v2) {
            vertexBuffer.put(f);
        }

        vertexBuffer.position(0);
        int vertexShader = CupRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = CupRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        if (vertexShader == 0){
            Log.e(TAG, "Cannot load VertexShader");
        }
        if (fragmentShader == 0){
            Log.e(TAG, "Cannot load FragmentShader");
        }
        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

    }

    public void draw(float[] mvpMatrix) {
        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // Set color for drawing the triangle
        switch (rgb){
            case 0:
                GLES20.glUniform4fv(colorHandle, 1, vertexColor, 0);
                break;
            case 1:
                GLES20.glUniform4fv(colorHandle, 1, colorRed, 0);
                break;
            case 2:
                GLES20.glUniform4fv(colorHandle, 1, colorGreen, 0);
                break;
            case 3:
                GLES20.glUniform4fv(colorHandle, 1, colorBlue, 0);
                break;
            case 4:
                GLES20.glUniform4fv(colorHandle, 1, colorWhite, 0);
                break;
        }
        //GLES20.glUniform4fv(colorHandle, 1, vertexColor, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
