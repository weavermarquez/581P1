package com.cpsc581.colorunlock;

import android.graphics.Color;
import android.graphics.Rect;
import android.widget.ImageView;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;

public class PaletteFruit {

    public ImageView paletteView;
    public Fruit fruit;
    public VectorDrawableCompat.VFullPath path;

    public PaletteFruit(ImageView view, Fruit fruit)
    {
        this.fruit = fruit;
        this.paletteView = view;
        this.path = new VectorChildFinder(paletteView.getContext(), R.drawable.ic_colorblob, paletteView).findPathByName("blob");
        path.setFillColor(fruit.toInt());
        paletteView.invalidate();
    }

    public Rect getHitRect()
    {
        Rect r = new Rect();
        this.paletteView.getGlobalVisibleRect(r);
        return r;
    }

    public void selectFruit()
    {
        path.setFillAlpha(0.25f);
        paletteView.invalidate();
    }

    public void unselectFruit()
    {
        path.setFillAlpha(1f);
        paletteView.invalidate();
    }
}
