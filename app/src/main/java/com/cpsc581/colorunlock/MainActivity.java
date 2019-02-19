package com.cpsc581.colorunlock;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;

public class MainActivity extends AppCompatActivity {

    static String TAG = "TouchUnlock";

    boolean firstLoad = true;

    ImageView sliceImage;
    ImageView sliceMask;
    ColorableSlice[] slices;
    PaletteColor[] colorPalette;
    int[] availableColors = {Color.MAGENTA,Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE};

    int selectedColor = Color.parseColor("#00ff99");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        sliceImage = findViewById(R.id.imageView);
        sliceMask = findViewById(R.id.imageMask);
        sliceImage.setImageResource(R.drawable.ic_complexslices);
        sliceMask.setImageResource(R.drawable.ic_complexslices_mask);

        VectorChildFinder vectorFinder = new VectorChildFinder(this, R.drawable.ic_complexslices, sliceImage);
        slices = new ColorableSlice[3];
        slices[0] = new ColorableSlice(sliceImage, sliceMask, vectorFinder.findPathByName("slice_0"), Color.parseColor("#ff0000"));
        slices[1] = new ColorableSlice(sliceImage, sliceMask, vectorFinder.findPathByName("slice_1"), Color.parseColor("#00ff00"));
        slices[2] = new ColorableSlice(sliceImage, sliceMask, vectorFinder.findPathByName("slice_2"), Color.parseColor("#0000ff"));

        slices[0].slice.setStrokeWidth(1f);
        slices[1].slice.setStrokeWidth(1f);
        slices[2].slice.setStrokeWidth(1f);

        colorPalette = new PaletteColor[6];
        colorPalette[0] = new PaletteColor((ImageView)findViewById(R.id.color0), availableColors[0]);
        colorPalette[1] = new PaletteColor((ImageView)findViewById(R.id.color1), availableColors[1]);
        colorPalette[2] = new PaletteColor((ImageView)findViewById(R.id.color2), availableColors[2]);
        colorPalette[3] = new PaletteColor((ImageView)findViewById(R.id.color3), availableColors[3]);
        colorPalette[4] = new PaletteColor((ImageView)findViewById(R.id.color4), availableColors[4]);
        colorPalette[5] = new PaletteColor((ImageView)findViewById(R.id.color5), availableColors[5]);

    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(firstLoad)
        {
            for (ColorableSlice slice : slices) {
                slice.animatePath();
            }
            firstLoad = false;
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        if (slices != null) {

            for (ColorableSlice slice : slices) {
                slice.setFill(Color.parseColor("#ffffff"));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int action = event.getActionMasked();

        int evX = (int) event.getX();
        int evY = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_UP:
                fillSlice(evX, evY);
                selectedColor = Color.TRANSPARENT;
                break;
            case MotionEvent.ACTION_MOVE:
                moveColorIcon(evX, evY);
                break;
            case MotionEvent.ACTION_DOWN:
                selectColor(evX, evY);
                break;
        }

        return true;
    }

    private void selectColor(int evX, int evY)
    {
        for (PaletteColor pc: colorPalette) {

            if(pc.getHitRect().contains(evX, evY))
            {
                pc.selectColor();
                selectedColor = pc.color;
            }
            else
            {
                pc.unselectColor();
            }
        }
    }

    private void moveColorIcon(int evX, int evY)
    {
        
    }

    public void fillSlice(int x, int y) {

        if(selectedColor != Color.TRANSPARENT) {

            for (ColorableSlice slice : slices) {

                if (closeMatch(getMaskColor(R.id.imageMask, x, y), slice.maskColor, 25)) {
                    slice.animateFill(selectedColor);
                    break;
                }

            }
        }
    }

    public int getMaskColor(int hotspot, int x, int y) {
        sliceMask.setDrawingCacheEnabled(true);
        Bitmap hotspots = Bitmap.createBitmap(sliceMask.getDrawingCache());
        sliceMask.setDrawingCacheEnabled(false);

        if (y > hotspots.getHeight() || x > hotspots.getWidth()) {
            return -1;
        }

        return hotspots.getPixel(x, y);
    }

    public boolean closeMatch(int color1, int color2, int tolerance) {
        if ((int) Math.abs(Color.red(color1) - Color.red(color2)) > tolerance)
            return false;
        if ((int) Math.abs(Color.green(color1) - Color.green(color2)) > tolerance)
            return false;
        if ((int) Math.abs(Color.blue(color1) - Color.blue(color2)) > tolerance)
            return false;
        return true;
    } // end match

}
