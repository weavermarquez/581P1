package com.cpsc581.colorunlock;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class LockPattern {

    private ArrayList<PatternNode> pattern;

    public LockPattern()
    {
        this.pattern = new ArrayList<>();
    }

    public LockPattern(PatternNode[] pattern)
    {
        this.pattern = new ArrayList<>();
        this.pattern.addAll(Arrays.asList(pattern));
    }

    public int getPatternLength()
    {
        return pattern.size();
    }

    public void addNode(PatternNode node)
    {
        pattern.add(node);
    }

    public void clearPattern()
    {
        pattern.clear();
    }

    public boolean validateAgainst(LockPattern a){

        Log.v(MainActivity.TAG, a.pattern.toString());
        Log.v(MainActivity.TAG, this.pattern.toString());

        if(a.pattern.size() != pattern.size())
        {
            return false;
        }

        for (int i = 0; i < pattern.size(); i++) {
            if(a.pattern.get(i).color != pattern.get(i).color)
            {
                return false;
            }
            if(a.pattern.get(i).slice != pattern.get(i).slice)
            {
                return false;
            }
        }

        return true;
    }

}
