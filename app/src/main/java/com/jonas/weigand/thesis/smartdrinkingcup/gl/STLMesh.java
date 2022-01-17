package com.jonas.weigand.thesis.smartdrinkingcup.gl;

import android.opengl.GLES20;
import android.util.Log;

import com.jonas.weigand.thesis.smartdrinkingcup.CupRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Vector;

public class STLMesh {

    //http://www.learnopengles.com/android-lesson-two-ambient-and-diffuse-lighting/
    private final String DiffuseVertexShader =
            "uniform mat4 u_MVPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.
                    + "uniform mat4 u_MVMatrix;       \n"     // A constant representing the combined model/view matrix.
                    + "uniform vec3 u_LightPos;       \n"     // The position of the light in eye space.

                    + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
                    + "attribute vec4 a_Color;        \n"     // Per-vertex color information we will pass in.
                    + "attribute vec3 a_Normal;       \n"     // Per-vertex normal information we will pass in.

                    + "varying vec4 v_Color;          \n"     // This will be passed into the fragment shader.

                    + "void main()                    \n"     // The entry point for our vertex shader.
                    + "{                              \n"
// Transform the vertex into eye space.
                    + "   vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);              \n"
// Transform the normal's orientation into eye space.
                    + "   vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));     \n"
// Will be used for attenuation.
                    + "   float distance = length(u_LightPos - modelViewVertex);             \n"
// Get a lighting direction vector from the light to the vertex.
                    + "   vec3 lightVector = normalize(u_LightPos - modelViewVertex);        \n"
// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
// pointing in the same direction then it will get max illumination.
                    + "   float diffuse = max(dot(modelViewNormal, lightVector), 0.1);       \n"
// Attenuate the light based on distance.
                    + "   diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));  \n"
// Multiply the color by the illumination level. It will be interpolated across the triangle.
                    + "   v_Color = a_Color * diffuse;                                       \n"
// gl_Position is a special variable used to store the final position.
// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
                    + "   gl_Position = u_MVPMatrix * a_Position;                            \n"
                    + "}                                                                     \n";

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
    private static final String TAG = "STLMesh";
    float vertexColor[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    private ArrayList<Line> lines = new ArrayList<Line>();
    private ByteBuffer bb;

    public STLMesh(int triangle_count) {
        bb = ByteBuffer.allocateDirect(4 * 3 * triangle_count * COORDS_PER_VERTEX);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();

        int vertexShader = CupRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = CupRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

    }

    public void addTriangle(byte[] bytes){
        if (bytes.length != 50){
            Log.e(TAG,"Byte Count has invalid Length " + bytes.length + " should be 50");
            return;
        }

        // add the coordinates to the FloatBuffer


        float x = ByteBuffer.wrap(bytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float y = ByteBuffer.wrap(bytes, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float z = ByteBuffer.wrap(bytes, 8, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();

        Vector<Float> normal = new Vector<>();
        normal.add(x);
        normal.add(y);
        normal.add(z);
        //Log.d("STLMesh", "Normal " + x + ", " + y + ", " + z);
        //TODO Write Line Class draw lines on Corners

        //Normal Vector
        //vertexBuffer.put(ByteBuffer.wrap(bytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        //vertexBuffer.put(ByteBuffer.wrap(bytes, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        //vertexBuffer.put(ByteBuffer.wrap(bytes, 8, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        float nx = ByteBuffer.wrap(bytes, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float ny = ByteBuffer.wrap(bytes, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float nz = ByteBuffer.wrap(bytes, 8, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        for (int i = 12; i < 48; i += 12) {
            Vector<Float> v1 = new Vector<>();
            Vector<Float> v2 = new Vector<>();
            float v1x = ByteBuffer.wrap(bytes, i, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            float v1y = ByteBuffer.wrap(bytes, i + 4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            float v1z = ByteBuffer.wrap(bytes, i + 8, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            v1.add(v1x);
            v1.add(v1y);
            v1.add(v1z);
            v2.add(v1x + 2 * nx);
            v2.add(v1y + 2 * ny);
            v2.add(v1z + 2 * nz);
            Line l = new Line(v1, v2, (byte) (i / 12));
            lines.add(l);
        }
        Log.d(TAG, "Lines " + lines.size());

        //V1
        vertexBuffer.put(ByteBuffer.wrap(bytes, 12, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        vertexBuffer.put(ByteBuffer.wrap(bytes, 16, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        vertexBuffer.put(ByteBuffer.wrap(bytes, 20, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());

        Vector<Float> v1 = new Vector<>();
        v1.add(ByteBuffer.wrap(bytes, 12, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        v1.add(ByteBuffer.wrap(bytes, 16, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        v1.add(ByteBuffer.wrap(bytes, 20, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());

        //V2
        vertexBuffer.put(ByteBuffer.wrap(bytes, 24, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        vertexBuffer.put(ByteBuffer.wrap(bytes, 28, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        vertexBuffer.put(ByteBuffer.wrap(bytes, 32, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());

        Vector<Float> v2 = new Vector<>();
        v2.add(ByteBuffer.wrap(bytes, 24, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        v2.add(ByteBuffer.wrap(bytes, 28, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        v2.add(ByteBuffer.wrap(bytes, 32, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());

        //V3
        vertexBuffer.put(ByteBuffer.wrap(bytes, 36, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        vertexBuffer.put(ByteBuffer.wrap(bytes, 40, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        vertexBuffer.put(ByteBuffer.wrap(bytes, 44, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());



        Vector<Float> v3 = new Vector<>();
        v3.add(ByteBuffer.wrap(bytes, 36, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        v3.add(ByteBuffer.wrap(bytes, 40, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());
        v3.add(ByteBuffer.wrap(bytes, 44, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat());


        Log.d(TAG, "V1:");
        Log.d(TAG, v1.get(0) + ", " + v1.get(1) + ", " + v1.get(2));
        Log.d(TAG, "V2:");
        Log.d(TAG, v2.get(0) + ", " + v2.get(1) + ", " + v2.get(2));
        Log.d(TAG, "V3:");
        Log.d(TAG, v3.get(0) + ", " + v3.get(1) + ", " + v3.get(2));


        Vector<Float> calculatedNormal = normal(crossProduct(sub(v1, v3), sub(v1, v2)));
        Log.d(TAG, "Normal Check1:");
        Log.d(TAG, normal.get(0) + " = " + calculatedNormal.get(0));
        Log.d(TAG, normal.get(1) + " = " + calculatedNormal.get(1));
        Log.d(TAG, normal.get(2) + " = " + calculatedNormal.get(2));
        Line l2 = new Line(v1, add(v1, calculatedNormal), (byte) 4);
        //lines.add(l2);
        l2 = new Line(v2, add(v2, calculatedNormal), (byte) 4);
        //lines.add(l2);
        l2 = new Line(v3, add(v3, calculatedNormal), (byte) 4);
        //lines.add(l2);


        calculatedNormal = normal(crossProduct(sub(v1, v2), sub(v1, v3)));
        Log.d(TAG, "Normal Check2:");
        Log.d(TAG, normal.get(0) + " = " + calculatedNormal.get(0));
        Log.d(TAG, normal.get(1) + " = " + calculatedNormal.get(1));
        Log.d(TAG, normal.get(2) + " = " + calculatedNormal.get(2));
        l2 = new Line(v1, add(v1, calculatedNormal), (byte) 4);
        //lines.add(l2);
        l2 = new Line(v2, add(v2, calculatedNormal), (byte) 4);
        //lines.add(l2);
        l2 = new Line(v3, add(v3, calculatedNormal), (byte) 4);
        //lines.add(l2);

        calculatedNormal = normal(crossProduct(sub(v2, v1), sub(v2, v3)));
        Log.d(TAG, "Normal Check3:");
        Log.d(TAG, normal.get(0) + " = " + calculatedNormal.get(0));
        Log.d(TAG, normal.get(1) + " = " + calculatedNormal.get(1));
        Log.d(TAG, normal.get(2) + " = " + calculatedNormal.get(2));
        l2 = new Line(v1, add(v1, calculatedNormal), (byte) 4);
        //lines.add(l2);
        l2 = new Line(v2, add(v2, calculatedNormal), (byte) 4);
        //lines.add(l2);
        l2 = new Line(v3, add(v3, calculatedNormal), (byte) 4);
        //lines.add(l2);


        calculatedNormal = normal(crossProduct(sub(v2, v3), sub(v2, v1)));
        Log.d(TAG, "Normal Check4:");
        Log.d(TAG, normal.get(0) + " = " + calculatedNormal.get(0));
        Log.d(TAG, normal.get(1) + " = " + calculatedNormal.get(1));
        Log.d(TAG, normal.get(2) + " = " + calculatedNormal.get(2));
        l2 = new Line(v1, add(v1, calculatedNormal), (byte) 4);
        lines.add(l2);
        l2 = new Line(v2, add(v2, calculatedNormal), (byte) 4);
        lines.add(l2);
        l2 = new Line(v3, add(v3, calculatedNormal), (byte) 4);
        lines.add(l2);

        calculatedNormal = normal(crossProduct(sub(v3, v1), sub(v3, v2)));
        Log.d(TAG, "Normal Check5:");
        Log.d(TAG, normal.get(0) + " = " + calculatedNormal.get(0));
        Log.d(TAG, normal.get(1) + " = " + calculatedNormal.get(1));
        Log.d(TAG, normal.get(2) + " = " + calculatedNormal.get(2));
        l2 = new Line(v1, add(v1, calculatedNormal), (byte) 4);
        //lines.add(l2);
        l2 = new Line(v2, add(v2, calculatedNormal), (byte) 4);
        //lines.add(l2);
        l2 = new Line(v3, add(v3, calculatedNormal), (byte) 4);
        //lines.add(l2);

        calculatedNormal = normal(crossProduct(sub(v3, v2), sub(v3, v1)));
        Log.d(TAG, "Normal Check6:");
        Log.d(TAG, normal.get(0) + " = " + calculatedNormal.get(0));
        Log.d(TAG, normal.get(1) + " = " + calculatedNormal.get(1));
        Log.d(TAG, normal.get(2) + " = " + calculatedNormal.get(2));
        l2 = new Line(v1, add(v1, calculatedNormal), (byte) 4);
        //lines.add(l2);
        l2 = new Line(v2, add(v2, calculatedNormal), (byte) 4);
        //lines.add(l2);
        l2 = new Line(v3, add(v3, calculatedNormal), (byte) 4);
        //lines.add(l2);
    }

    private Vector<Float> add(Vector<Float> a, Vector<Float> b){
        if (a.size() != 3 || b.size() != 3){
            return null;
        }
        Vector<Float> add = new Vector<>();
        add.add(a.get(0) + b.get(0));
        add.add(a.get(1) + b.get(1));
        add.add(a.get(2) + b.get(2));
        return add;
    }

    private Vector<Float> sub(Vector<Float> a, Vector<Float> b){
        if (a.size() != 3 || b.size() != 3){
            return null;
        }
        Vector<Float> sub = new Vector<>();
        sub.add(a.get(0) - b.get(0));
        sub.add(a.get(1) - b.get(1));
        sub.add(a.get(2) - b.get(2));
        return sub;
    }

    private Vector<Float> crossProduct(Vector<Float> a, Vector<Float> b){
        if (a.size() != 3 || b.size() != 3){
            return null;
        }
        Vector<Float> ab = new Vector<>();
        ab.add(a.get(1) * b.get(2) - a.get(2) * b.get(1));
        ab.add(a.get(2) * b.get(0) - a.get(0) * b.get(2));
        ab.add(a.get(0) * b.get(1) - a.get(1) * b.get(0));
        return ab;
    }

    private Vector<Float> normal(Vector<Float> v){
        if (v.size() != 3){
            return null;
        }
        Vector<Float> n = new Vector<>();
        float size = 0;
        for (Float f: v) {
            size += Math.pow(f, 2);
        }
        size = (float) Math.sqrt(size);
        n.add(v.get(0) / size);
        n.add(v.get(1) / size);
        n.add(v.get(2) / size);
        return n;
    }

    public void draw(float[] mvpMatrix) { // pass in the calculated transformation matrix

        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

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
        for (Line l:lines) {
            l.draw(mvpMatrix);
        }
    }
}
