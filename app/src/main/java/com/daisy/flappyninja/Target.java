package com.daisy.flappyninja;

public class Target {
    private float positionX;
    private float height;
    private int stuff;

    public Target(float positionX, float height, int stuff) {
        this.positionX = positionX;
        this.height = height;
        this.stuff = stuff;
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getHeight() {
        return height;
    }

    public int getStuff(){return stuff;}

    public boolean isBomb(){
        if(stuff>7)
            return true;
        else
            return false;
    }


}
