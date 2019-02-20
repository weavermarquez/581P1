package com.cpsc581.colorunlock;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
import android.widget.RelativeLayout;

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

    int selectedColor = Color.TRANSPARENT;

    ImageView pointerImage;

    LockPattern key;
    LockPattern attempt = new LockPattern();

    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Basic Window formatting
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Get View References and set source and mask
        sliceImage = findViewById(R.id.imageView);
        sliceMask = findViewById(R.id.imageMask);
        sliceImage.setImageResource(R.drawable.ic_complexslices);
        sliceMask.setImageResource(R.drawable.ic_complexslices_mask);

        //Get paths
        VectorChildFinder vectorFinder = new VectorChildFinder(this, R.drawable.ic_complexslices, sliceImage);
        slices = new ColorableSlice[3];
        slices[0] = new ColorableSlice(0, sliceImage, sliceMask, vectorFinder.findPathByName("slice_0"), Color.parseColor("#ff0000"));
        slices[1] = new ColorableSlice(1, sliceImage, sliceMask, vectorFinder.findPathByName("slice_1"), Color.parseColor("#00ff00"));
        slices[2] = new ColorableSlice(2, sliceImage, sliceMask, vectorFinder.findPathByName("slice_2"), Color.parseColor("#0000ff"));

        //Fix stroke which doesn't show by default for some reason?
        slices[0].slice.setStrokeWidth(1f);
        slices[1].slice.setStrokeWidth(1f);
        slices[2].slice.setStrokeWidth(1f);

        //Gross code to build the color palette
        colorPalette = new PaletteColor[6];
        colorPalette[0] = new PaletteColor((ImageView)findViewById(R.id.color0), availableColors[0]);
        colorPalette[1] = new PaletteColor((ImageView)findViewById(R.id.color1), availableColors[1]);
        colorPalette[2] = new PaletteColor((ImageView)findViewById(R.id.color2), availableColors[2]);
        colorPalette[3] = new PaletteColor((ImageView)findViewById(R.id.color3), availableColors[3]);
        colorPalette[4] = new PaletteColor((ImageView)findViewById(R.id.color4), availableColors[4]);
        colorPalette[5] = new PaletteColor((ImageView)findViewById(R.id.color5), availableColors[5]);

        //Generate Pattern which unlocks the device
        key = new LockPattern();
        key.addNode(new PatternNode(0, Color.GREEN));
        key.addNode(new PatternNode(2, Color.BLUE));
        key.addNode(new PatternNode(1, Color.CYAN));

        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
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

        reset();
    }

    private void reset() {
        if (slices != null) {

            for (ColorableSlice slice : slices) {
                slice.setFill(Color.WHITE);
            }
        }
        attempt.clearPattern();
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
                unselectAllColors();
                removePointer();
                break;
            case MotionEvent.ACTION_MOVE:
                moveColorIcon(evX, evY);
                break;
            case MotionEvent.ACTION_DOWN:
                doActionDown(evX, evY);
                break;
        }

        return true;
    }

    private void doActionDown(int evX, int evY)
    {
        unselectAllColors();
        if(selectColor(evX, evY))
        {
            createPointer(evX, evY);
        }
    }

    private void removePointer() {
        ((RelativeLayout)findViewById(R.id.mainLayout)).removeView(pointerImage);
    }

    private void createPointer(int x, int y)
    {
        pointerImage = new ImageView(this);
        pointerImage.setImageResource(R.drawable.ic_colorblob);
        pointerImage.setColorFilter(selectedColor);
        ((RelativeLayout)findViewById(R.id.mainLayout)).addView(pointerImage);
        pointerImage.setScaleType(ImageView.ScaleType.CENTER);
        pointerImage.setScaleX(2f);
        pointerImage.setScaleY(2f);
        pointerImage.setX(x - (pointerImage.getWidth()/2f));
        pointerImage.setY(y - (pointerImage.getHeight()/2f));
        
    }
    
    private boolean selectColor(int evX, int evY)
    {
        for (PaletteColor pc: colorPalette)
        {
            if(pc.getHitRect().contains(evX, evY))
            {
                pc.selectColor();
                selectedColor = pc.color;
                return true;
            }
        }

        return false;
    }

    private void unselectAllColors()
    {
        selectedColor = Color.TRANSPARENT;

        for (PaletteColor pc: colorPalette)
        {
            pc.unselectColor();
        }
    }

    private void moveColorIcon(int x, int y)
    {
        if(pointerImage != null) {
            pointerImage.setX(x - (pointerImage.getWidth() / 2f));
            pointerImage.setY(y - (pointerImage.getHeight() / 2f));
        }
    }

    public void fillSlice(int x, int y) {

        if(selectedColor != Color.TRANSPARENT) {

            for (ColorableSlice slice : slices) {

                if (closeMatch(getMaskColor(R.id.imageMask, x, y), slice.maskColor, 25)) {
                    Animator a = slice.animateFill(selectedColor);
                    attempt.addNode(new PatternNode(slice.id, selectedColor));
                    a.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            if(key.getPatternLength() == attempt.getPatternLength()) {
                                if (attempt.validateAgainst(key)) {
                                    //UNLOCKED!
                                    finish();
                                }else {
                                    //TODO Give Feedback so user knows they are wrong
                                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                                    reset();
                                }
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
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
