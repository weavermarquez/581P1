package com.cpsc581.colorunlock;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.widget.ImageView;

import com.devs.vectorchildfinder.VectorDrawableCompat;

import java.nio.file.Path;

public class ColorableSlice{

    public ImageView parent;
    public ImageView mask;
    public VectorDrawableCompat.VFullPath slice;
    public int maskColor;
    public int id;

    public ColorableSlice(int id, ImageView parent, ImageView mask, VectorDrawableCompat.VFullPath slice, int maskColor)
    {
        this.parent = parent;
        this.mask = mask;
        this.slice = slice;
        this.maskColor = maskColor;
        this.id = id;
    }

    public Animator animateFill(int toColor)
    {
        int from = slice.getFillColor();
        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), from, toColor);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                slice.setFillColor((int) animator.getAnimatedValue());
                parent.invalidate();
            }

        });
        animator.start();

        return animator;
    }

    public void animatePath()
    {
        ValueAnimator animator = ValueAnimator.ofObject(new FloatEvaluator(), 0f, 1f);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                slice.setTrimPathEnd((float) animator.getAnimatedValue());
                parent.invalidate();
            }
        });
        animator.start();
    }

    public void setFill(int color)
    {
        slice.setFillColor(color);
        parent.invalidate();
    }
}
