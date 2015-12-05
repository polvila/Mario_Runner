package com.pol.games.Super_Runner;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * Created by Pol on 27/10/15.
 *
 */
public class MainActivity extends Activity {
    private MyOpenGLRenderer mRenderer;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView view = new GLSurfaceView(this);
        mRenderer = new MyOpenGLRenderer(this);
        view.setRenderer(mRenderer);
        setContentView(view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRenderer.pauseMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRenderer.pauseMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRenderer.replayMusic();
    }


    public boolean onTouchEvent(MotionEvent event) {
        return mRenderer.onTouchEvent(event);
    }

}