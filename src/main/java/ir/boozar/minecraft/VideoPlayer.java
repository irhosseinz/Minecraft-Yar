package ir.boozar.minecraft;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by hossein on 11/16/14.
 */
public class VideoPlayer extends SurfaceView implements SurfaceHolder.Callback {

    public boolean loaded = false;

    public VideoPlayer(Context c) {
        super(c);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        loaded = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}