package com.cpsc581.colorunlock;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import java.util.EmptyStackException;

public enum Fruit {
    DRAGONFRUIT ("Dragonfruit", 0xFFCC0040, R.drawable.dragonfruit),
    STRAWBERRY  ("Strawberry",  0xFFDE002A, R.drawable.strawberry),
    BANANA      ("Banana",      0xFFECBB76, R.drawable.banana),
    APPLE       ("Apple",       0xFF5C9920, R.drawable.apple),
    BLUEBERRY   ("Blueberry",   0xFF195093, R.drawable.blueberry),
    ACAI        ("Acai",        0xFF64374E, R.drawable.acai),
    EMPTY       ("Empty",     0x00000000, R.drawable.bowl);


    private String stringValue;
    private int colorValue;
    private int imageValue;

    private Fruit(String toString, int color, int image){
        stringValue = toString;
        colorValue = color;
        imageValue = image;
    }

    @Override
    public String toString(){
        return stringValue;
    }


    public int toInt(){
        return colorValue;
    }

    /*
    public int toImage(){
        return imageValue;
    }
     */

    public Drawable toImage() {return (Drawable)imageValue;}
}
