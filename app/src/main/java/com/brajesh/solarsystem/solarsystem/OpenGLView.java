package com.brajesh.solarsystem.solarsystem;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class OpenGLView extends GLSurfaceView {

    Context context;
    OpenGLRenderer renderer;

    public OpenGLView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public OpenGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    void init() {
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
//        setBackgroundResource(R.drawable.stars_milky_way);
        setZOrderOnTop(false);
        renderer = new OpenGLRenderer(this.context);
        setRenderer(renderer);
    }

    public void onResume(){
        super.onResume();
    }
    public void onPause(){
        super.onPause();
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float ViewPreviousX;
    private float ViewPreviousY;
    private float currX, currY ;

    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currX = x;
                currY = y;

            case MotionEvent.ACTION_MOVE:

                float dviewx = 0;
                float dviewy = 0;

//                dviewy = y - ViewPreviousY;
////                dviewy = dviewy * -2 ;
//                OpenGLRenderer.xsetViewAngle(OpenGLRenderer.xgetViewAngle()-dviewy*TOUCH_SCALE_FACTOR);
//                ViewPreviousY = y;

                dviewx = x - ViewPreviousX;
                dviewx = dviewx * -0.2f;
                OpenGLRenderer.ysetViewAngle(OpenGLRenderer.ygetViewAngle() - dviewx * TOUCH_SCALE_FACTOR);
                ViewPreviousX = x;

                requestRender();
                break;

        }
        return true;
    }
}
