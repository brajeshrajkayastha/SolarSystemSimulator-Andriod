package com.brajesh.solarsystem.solarsystem;

public  class Calculations {
    float To=1.0f;
//    public static float[][] Pos = new float[10][3]; //sun,mercury,venus,earth,mars.jupiter,saturn,uranus,neptune,pluto //xyz
//    public static float[] Rot = new float[9];  //mercury,venus,earth,mars.jupiter,saturn,uranus,neptune,pluto

    public float[] calcPos(float T, float rad, float Tp){
        float[] pos = new float[2];

        pos[0] = rad*(float)Math.cos(((2*Math.PI)*(T-To))/Tp);
        pos[1] = rad*(float)Math.sin(((2*Math.PI)*(T-To))/Tp);

        return pos;
    }

    public void calcRot(){

    }

}
