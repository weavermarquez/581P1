package com.cpsc581.colorunlock;

import android.graphics.Color;
import android.graphics.Rect;
import android.widget.ImageView;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;

public class PaletteColor {

    public ImageView paletteView;
    public int color;
    public VectorDrawableCompat.VFullPath path;


    public PaletteColor(ImageView view, int color)
    {
        this.color = color;
        this.paletteView = view;
        this.path = new VectorChildFinder(paletteView.getContext(), R.drawable.ic_colorblob, paletteView).findPathByName("blob");
        path.setFillColor(color);
        paletteView.invalidate();
    }

    public Rect getHitRect()
    {
        Rect r = new Rect();
        this.paletteView.getGlobalVisibleRect(r);
        return r;
    }

    public void selectColor()
    {
        path.setFillAlpha(0.25f);
        paletteView.invalidate();
    }

    public void unselectColor()
    {
        path.setFillAlpha(1f);
        paletteView.invalidate();
    }
}
