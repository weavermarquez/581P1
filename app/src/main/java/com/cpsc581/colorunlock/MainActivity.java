package com.cpsc581.colorunlock;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DrawableUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;

public class MainActivity extends AppCompatActivity {

    static String TAG = "TouchUnlock";
    ImageView sliceImage;
    ImageView sliceMask;
    VectorDrawableCompat.VFullPath slice0;
    VectorDrawableCompat.VFullPath slice1;
    VectorDrawableCompat.VFullPath slice2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        sliceImage = findViewById(R.id.imageView);
        sliceMask = findViewById(R.id.imageMask);
        VectorChildFinder vectorFinder = new VectorChildFinder(this, R.drawable.ic_imageslices, sliceImage);
        slice0 = vectorFinder.findPathByName("slice_0");
        slice1 = vectorFinder.findPathByName("slice_1");
        slice2 = vectorFinder.findPathByName("slice_2");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int action = event.getActionMasked();

        int evX = (int)event.getX();
        int evY = (int)event.getY();

        if(action == MotionEvent.ACTION_DOWN) {

            int touchColor = getMaskColor(R.id.imageMask, evX, evY);

            if (closeMatch(touchColor, Color.parseColor("#ff0000"), 25)) {
                slice0.setFillColor(Color.RED);

            } else if (closeMatch(touchColor, Color.parseColor("#00ff00"), 25)) {
                slice1.setFillColor(Color.GREEN);
            }else if (closeMatch(touchColor, Color.parseColor("#0000ff"), 25)) {
                slice2.setFillColor(Color.BLUE);
            }
            sliceImage.invalidate();
        }

        return true;
    }

    public int getMaskColor(int hotspot, int x, int y)
    {
        sliceMask.setDrawingCacheEnabled(true);
        Bitmap hotspots = Bitmap.createBitmap(sliceMask.getDrawingCache());
        sliceMask.setDrawingCacheEnabled(false);

        return hotspots.getPixel(x, y);
    }

    public boolean closeMatch (int color1, int color2, int tolerance) {
        if ((int) Math.abs (Color.red (color1) - Color.red (color2)) > tolerance )
            return false;
        if ((int) Math.abs (Color.green (color1) - Color.green (color2)) > tolerance )
            return false;
        if ((int) Math.abs (Color.blue (color1) - Color.blue (color2)) > tolerance )
            return false;
        return true;
    } // end match

}
