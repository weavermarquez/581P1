package com.cpsc581.colorunlock;

public class PatternNode{
    public Fruit fruit;
    public int slice;
    public int color;

    public PatternNode(int slice, Fruit fruit){
        this.fruit = fruit;
        this.slice = slice;
    }
    /*
    public PatternNode(int slice, int color)
    {
        this.color = color;
        this.slice = slice;
    }
     */

    @Override
    public String toString()
    {
        return "{fruit:" + fruit.toString() + "color:"+ fruit.toInt() + " slice: " + slice + "}";
    }
}
