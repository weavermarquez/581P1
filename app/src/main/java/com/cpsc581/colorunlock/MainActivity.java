package com.cpsc581.colorunlock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.Image;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    static String TAG = "TouchUnlock";

    TextView date;

    boolean firstLoad = true;
    boolean settingPattern = false;

    FloatingActionButton fab;
    int patternLength;

    ImageView sliceImage;
    ImageView sliceMask;
    ColorableSlice[] slices;
    PaletteColor[] colorPalette;
    int[] availableColors = {Color.MAGENTA,Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE};

    int selectedColor = Color.TRANSPARENT;

    ImageView pointerImage;
    ImageView confirmed;
    ImageView denied;

    LockPattern key;
    LockPattern attempt = new LockPattern();

    Vibrator vibrator;

    VectorDrawableCompat.VFullPath checkmarkPath;
    VectorDrawableCompat.VFullPath checkOutline;
    VectorDrawableCompat.VFullPath deniedPath1;
    VectorDrawableCompat.VFullPath deniedPath2;
    VectorDrawableCompat.VFullPath deniedOutline1;
    VectorDrawableCompat.VFullPath deniedOutline2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Basic Window formatting
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);

//        date = findViewById(R.id.dateView);


        //Get View References and set source and mask
        sliceImage = findViewById(R.id.imageView);
        sliceMask = findViewById(R.id.imageMask);
        sliceImage.setImageResource(R.drawable.ic_complexslices);
        sliceMask.setImageResource(R.drawable.ic_complexslices_mask);
        fab = findViewById(R.id.fab);
        confirmed = findViewById(R.id.confirmation);
        confirmed.setVisibility(View.INVISIBLE);
        denied = findViewById(R.id.denied);
        denied.setVisibility(View.INVISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPattern(view);
            }
        });
        fab.hide();

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

        //Load key data if available, otherwise, set key data
        ArrayList<PatternNode> temp =  loadData("pass");
        if(temp == null){
            key = new LockPattern();
            setPatternAlert();
        }
        else{
            key = new LockPattern();
            key.setPattern(temp);
        }

        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        VectorChildFinder vfc = new VectorChildFinder(this, R.drawable.ic_check, confirmed);
        checkmarkPath = vfc.findPathByName("checkmark");
        checkOutline = vfc.findPathByName("outline");
        checkmarkPath.setTrimPathEnd(0f);
        checkOutline.setTrimPathEnd(0f);

        VectorChildFinder vcf = new VectorChildFinder(this, R.drawable.ic_x, denied);
        deniedPath1 = vcf.findPathByName("part1");
        deniedPath2 = vcf.findPathByName("part2");
        deniedOutline1 = vcf.findPathByName("outline1");
        deniedOutline2 = vcf.findPathByName("outline2");
        deniedPath1.setTrimPathEnd(0f);
        deniedPath1.setStrokeAlpha(0f);
        deniedPath2.setTrimPathEnd(0f);
        deniedPath2.setStrokeAlpha(0f);
        deniedOutline1.setTrimPathEnd(0f);
        deniedOutline1.setStrokeAlpha(0f);
        deniedOutline2.setTrimPathEnd(0f);
        deniedOutline2.setStrokeAlpha(0f);
    }

    public void generatePattern(){
        settingPattern = true;
        fab.show();
    }

    public void setPattern(View view) {
        reset();
        settingPattern = false;
        patternLength = key.getPatternLength();
        saveData(key.getPattern(), "pass");
        fab.hide();
    }

    private void saveData(ArrayList<PatternNode> pn, String key1){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pn);
        editor.putString(key1, json);
        editor.apply();
    }

    private ArrayList<PatternNode> loadData(String key1){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key1,null);
        Type type = new TypeToken<ArrayList<PatternNode>>(){}.getType();
        return (ArrayList<PatternNode>) gson.fromJson(json, type);
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
        denied.setVisibility(View.INVISIBLE);
        deniedPath1.setStrokeAlpha(0f);
        deniedPath2.setStrokeAlpha(0f);
        deniedOutline1.setStrokeAlpha(0f);
        deniedOutline2.setStrokeAlpha(0f);
        attempt.clearPattern();
    }

    @Override
    public void onResume() {
        super.onResume();

//        String d = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
//
//        date.setText(d);
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
        pointerImage.setImageResource(R.drawable.ic_dragblob);
        pointerImage.setColorFilter(selectedColor);
        ((RelativeLayout)findViewById(R.id.mainLayout)).addView(pointerImage);
        pointerImage.setScaleType(ImageView.ScaleType.CENTER);
        pointerImage.setScaleX(1f);
        pointerImage.setScaleY(1f);
        pointerImage.setX(x - (pointerImage.getWidth()/2f));
        pointerImage.setY(y - (pointerImage.getHeight()));
        
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
            pointerImage.setY(y - (pointerImage.getHeight()));
        }
    }

    public void fillSlice(int x, int y) {

        if(selectedColor != Color.TRANSPARENT) {

            for (ColorableSlice slice : slices) {

                if (closeMatch(getMaskColor(R.id.imageMask, x, y), slice.maskColor, 25)) {
                    Animator a = slice.animateFill(selectedColor);
                    if (!settingPattern) {
                        attempt.addNode(new PatternNode(slice.id, selectedColor));
                        a.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (key.getPatternLength() == attempt.getPatternLength()) {
                                    if (attempt.validateAgainst(key)) {
                                        //UNLOCKED!
                                        unlockPhone();
                                    } else {
                                        invalidPass();
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
                    else{
                        key.addNode(new PatternNode(slice.id, selectedColor));
                    }
                }
            }
        }
    }

    private void invalidPass()
    {
        ValueAnimator va1 = ValueAnimator.ofObject(new FloatEvaluator(), 0.1f, 1f);
        final ValueAnimator va2 = ValueAnimator.ofObject(new FloatEvaluator(), 0.1f, 1f);
        va1.setDuration(125);
        va2.setDuration(125);
        va1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                deniedPath1.setTrimPathEnd((float)animation.getAnimatedValue());
                deniedOutline1.setTrimPathEnd((float) animation.getAnimatedValue());
                denied.invalidate();
            }
        });
        va2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                deniedPath2.setTrimPathEnd((float)animation.getAnimatedValue());
                deniedOutline2.setTrimPathEnd((float) animation.getAnimatedValue());
                denied.invalidate();
            }
        });
        va1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                deniedPath1.setStrokeAlpha(1f);
                deniedOutline1.setStrokeAlpha(1f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                va2.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                deniedPath2.setStrokeAlpha(1f);
                deniedOutline2.setStrokeAlpha(1f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        reset();
                    }
                }, 250);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        denied.setVisibility(View.VISIBLE);
        va1.start();

    }

    private void unlockPhone() {
        ValueAnimator va = ValueAnimator.ofObject(new FloatEvaluator(), 0.1f, 1f);
        va.setDuration(250);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                checkmarkPath.setTrimPathEnd((float)animation.getAnimatedValue());
                checkOutline.setTrimPathEnd((float)animation.getAnimatedValue());
                confirmed.invalidate();
            }
        });
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                //show checkmark for .25 seconds before closing app

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 250);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        confirmed.setVisibility(View.VISIBLE);
        va.start();
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

    public void setPatternAlert(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = "Please set an unlock pattern";
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        generatePattern();
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
