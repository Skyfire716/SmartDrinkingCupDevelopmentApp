package com.jonas.weigand.thesis.smartdrinkingcup.gl;

import android.opengl.GLES20;
import android.util.Log;

import com.jonas.weigand.thesis.smartdrinkingcup.CupRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

public class STLTriangle {

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


    private STLVector3D normal;
    private STLVector3D v1;
    private STLVector3D v2;
    private STLVector3D v3;
    private byte[] attributeByteCount;

    private int vPMatrixHandle;
    private int positionHandle;
    private int colorHandle;
    private int mProgram;
    private FloatBuffer vertexBuffer;
    static final int COORDS_PER_VERTEX = 3;
    private final int vertexCount = 12 / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4;
    private static final String TAG = "STLTriangle";
    float vertexColor[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    //float vertexColor[] = {0,5411764705882353f, 0,5411764705882353f, 0,5411764705882353f, 1.0f};

    public STLTriangle(byte[] bytes){
        this(new STLVector3D(Arrays.copyOfRange(bytes, 0, 12)), new STLVector3D(Arrays.copyOfRange(bytes, 12, 24)), new STLVector3D(Arrays.copyOfRange(bytes, 24, 36)), new STLVector3D(Arrays.copyOfRange(bytes, 36, 48)), Arrays.copyOfRange(bytes, 49, 51));
    }

    public STLTriangle(STLVector3D normal, STLVector3D v1, STLVector3D v2, STLVector3D v3, byte[] attributeByteCount) {
        this.normal = normal;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        if (attributeByteCount.length != 2){
            Log.e(TAG,"Attribute Byte Count has invalid Length " + attributeByteCount.length + " should be 2");
            return;
        }
        this.attributeByteCount = attributeByteCount;
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * 3 * COORDS_PER_VERTEX);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(v1.getFloats());
        vertexBuffer.put(v2.getFloats());
        vertexBuffer.put(v3.getFloats());
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        int vertexShader = CupRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = CupRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

    }

    public void draw(float[] mvpMatrix) { // pass in the calculated transformation matrix

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
        GLES20.glUniform4fv(colorHandle, 1, vertexColor, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    public STLVector3D getNormal() {
        return normal;
    }

    public void setNormal(STLVector3D normal) {
        this.normal = normal;
    }

    public STLVector3D getV1() {
        return v1;
    }

    public void setV1(STLVector3D v1) {
        this.v1 = v1;
    }

    public STLVector3D getV2() {
        return v2;
    }

    public void setV2(STLVector3D v2) {
        this.v2 = v2;
    }

    public STLVector3D getV3() {
        return v3;
    }

    public void setV3(STLVector3D v3) {
        this.v3 = v3;
    }

    public byte[] getAttributeByteCount() {
        return attributeByteCount;
    }

    public void setAttributeByteCount(byte[] attributeByteCount) {
        this.attributeByteCount = attributeByteCount;
    }

    @Override
    public String toString() {
        return "STLTriangle{" +
                "normal=" + normal +
                ", v1=" + v1 +
                ", v2=" + v2 +
                ", v3=" + v3 +
                ", attributeByteCount=" + Arrays.toString(attributeByteCount) +
                '}';
    }
}
