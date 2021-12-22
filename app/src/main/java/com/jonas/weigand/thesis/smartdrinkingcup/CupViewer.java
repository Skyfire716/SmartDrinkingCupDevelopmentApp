package com.jonas.weigand.thesis.smartdrinkingcup;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.jonas.weigand.thesis.smartdrinkingcup.gl.BinarySTLParser;
import com.jonas.weigand.thesis.smartdrinkingcup.gl.STLTriangle;
import com.jonas.weigand.thesis.smartdrinkingcup.gl.STLVector3D;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class CupViewer extends GLSurfaceView {

    private final CupRenderer renderer;

    public CupViewer(Context context) {
        super(context);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        renderer = new CupRenderer(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
    }

    public CupViewer(Context context, AttributeSet attrs){
        super(context, attrs);
        renderer = new CupRenderer(context);
        setRenderer(renderer);
    }

}