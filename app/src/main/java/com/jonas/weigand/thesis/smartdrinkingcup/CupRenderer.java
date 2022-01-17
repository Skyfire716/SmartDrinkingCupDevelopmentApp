package com.jonas.weigand.thesis.smartdrinkingcup;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.view.inputmethod.BaseInputConnection;

import com.jonas.weigand.thesis.smartdrinkingcup.gl.BinarySTLParser;
import com.jonas.weigand.thesis.smartdrinkingcup.gl.STLMesh;
import com.jonas.weigand.thesis.smartdrinkingcup.gl.STLTriangle;
import com.jonas.weigand.thesis.smartdrinkingcup.gl.STLVector3D;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class CupRenderer implements GLSurfaceView.Renderer {

    private final String vertexShaderCode =

            // This matrix member variable provides a hook to manipulate
            // the coordinates of objects that use this vertex shader.
            "uniform mat4 uMVPMatrix;   \n" +

                    "attribute vec4 vPosition;  \n" +
                    "void main(){               \n" +
                    // The matrix must be included as part of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    " gl_Position = uMVPMatrix * vPosition; \n" +

                    "}  \n";

    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];


    private Context context;

    public ArrayList<STLTriangle> triangles;

    private STLMesh mesh;

    private float[] lightPos = new float[]{0.8f, 0.8f, 0.0f};
    private float[] ambientColor = new float[]{0.3f, 0.3f, 0.3f, 1.0f};
    private float[] diffuseColor = new float[]{0.8f, 0.8f, 0.8f, 1.0f};

    private float[] rotationMatrix = new float[16];


    public CupRenderer(Context context){
        this.context = context;
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }


    private STLTriangle getTriangle1(){
        STLVector3D normal =new STLVector3D(0, 0, 0);
        STLVector3D v1 = new STLVector3D(1.0f,  1.0f, -1.0f);
        STLVector3D v2 = new STLVector3D(1.0f, -1.0f, -1.0f);
        STLVector3D v3 = new STLVector3D( -1.0f, -1.0f, -1.0f);
        return new STLTriangle(normal, v1, v3, v2, new byte[]{0,0});
    }

    private STLTriangle getTriangle2(){
        STLVector3D normal =new STLVector3D(0, 0, 0);
        STLVector3D v1 = new STLVector3D(1.0f, -1.0f, -1.0f);
        STLVector3D v2 = new STLVector3D(0.33333334f, -0.33333334f, 1.0f);
        STLVector3D v3 = new STLVector3D( -1.0f, -1.0f, -1.0f);
        return new STLTriangle(normal, v1, v3, v2, new byte[]{0,0});
    }

    private STLTriangle getTriangle3(){
        STLVector3D normal =new STLVector3D(0, 0, 0);
        STLVector3D v1 = new STLVector3D(-1.0f, -1.0f, -1.0f);
        STLVector3D v2 = new STLVector3D(0.33333334f, -0.33333334f, 1.0f);
        STLVector3D v3 = new STLVector3D( 1.0f, 1.0f, -1.0f);
        return new STLTriangle(normal, v1, v3, v2, new byte[]{0,0});
    }

    private STLTriangle getTriangle4(){
        STLVector3D normal =new STLVector3D(0, 0, 0);
        STLVector3D v1 = new STLVector3D(1.0f, 1.0f, -1.0f);
        STLVector3D v2 = new STLVector3D(0.33333334f, -0.33333334f, 1.0f);
        STLVector3D v3 = new STLVector3D( 1.0f, -1.0f, -1.0f);
        return new STLTriangle(normal, v1, v3, v2, new byte[]{0,0});
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //GLES20.glEnable(GLES20.GL_CULL_FACE);
        //GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //triangles = BinarySTLParser.readFile(this.context);
        triangles = new ArrayList<>();
        STLVector3D normal =new STLVector3D(0, 0, 0);
        STLVector3D v1 = new STLVector3D(0.0f,  0.622008459f, 0.0f);
        STLVector3D v2 = new STLVector3D(-0.5f, -0.311004243f, 0.0f);
        STLVector3D v3 = new STLVector3D( 0.5f, -0.311004243f, 0.0f);
        //triangles.add(new STLTriangle(normal, v1, v3, v2, new byte[]{0,0}));
        //triangles.add(getTriangle1());
        //triangles.add(getTriangle2());
        //triangles.add(getTriangle3());
        //triangles.add(getTriangle4());
        mesh = BinarySTLParser.readMesh(this.context);
        //triangles.addAll(BinarySTLParser.readFile(this.context));
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 100);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(viewMatrix, 0, 0, -2, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        Matrix.translateM(viewMatrix, 0, 0, 0, -1);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        for (STLTriangle stlTriangle : triangles){
            float[] scratch = new float[16];
            // Create a rotation transformation for the triangle
            long time = SystemClock.uptimeMillis() % 4000L;
            float angle = 0.090f * ((int) time);
            Matrix.setRotateM(rotationMatrix, 0, angle, 0, 1.0f, -1.0f);

            // Combine the rotation matrix with the projection and camera view
            // Note that the vPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);
            Matrix.translateM(scratch, 0, 0, 5, -2);
            //stlTriangle.draw(scratch, viewMatrix, lightPos);
            stlTriangle.draw(vPMatrix);
        }
        if (mesh != null){
            float[] scratch = new float[16];
            // Create a rotation transformation for the triangle
            long time = SystemClock.uptimeMillis() % 4000L;
            float angle = 0.090f * ((int) time);
            Matrix.setRotateM(rotationMatrix, 0, angle, 0, 1.0f, -1.0f);

            // Combine the rotation matrix with the projection and camera view
            // Note that the vPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);
            Matrix.translateM(scratch, 0, 0, 5, -2);
            mesh.draw(scratch);
        }
    }
}
