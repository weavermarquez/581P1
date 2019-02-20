package com.cpsc581.colorunlock;

public class PatternNode{
    public int slice;
    public int color;

    public PatternNode(int slice, int color)
    {
        this.color = color;
        this.slice = slice;
    }

    @Override
    public String toString()
    {
        return "{color:"+ color + " slice: " + slice + "}";
    }
}
