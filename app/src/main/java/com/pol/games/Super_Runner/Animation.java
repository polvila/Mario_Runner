package com.pol.games.Super_Runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;

/**
 * Created by Pol on 27/10/15.
 *
 */
public class Animation
{
    private float posx, posy;
    private float sclx, scly;
    private float speed;
    private String currentAnimation;

    private int currentFrame;

    private long tempsActual;

    private HashMap <String, List<AnimationStep>> animations = new HashMap <>();

    public Animation(Texture tex, int filenameId, Context ctx, float speed) {
        // Load the data from the file
        String line,parts[];

        InputStream inputStream = ctx.getResources().openRawResource(filenameId);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);

        currentFrame = 0;
        tempsActual = System.currentTimeMillis();
        if(speed == 0)
            this.speed = 1;
        else
            this.speed = speed;

        try {
            //Read the animations from the file
            line = buffreader.readLine();
            while (line != null)
            {
                parts = line.split(" ");

                String nom = parts[0];
                List<AnimationStep> listAs;
                if(animations.get(nom)==null){
                    listAs = new LinkedList<>();
                }else{
                    listAs = animations.get(nom);
                }
                listAs.add(new AnimationStep(tex, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5])));

                animations.put(nom, listAs);

                line = buffreader.readLine();
            }
        } catch (IOException e) {
            System.out.println(Log.getStackTraceString(e));
        }

    }

    public void setCurrentAnimation(String name){
        currentAnimation = name;
        currentFrame = 0;

    }

    public String getCurrentAnimation() {
        return currentAnimation;
    }

    public void setPosition(float x, float y){
        posx = x;
        posy = y;
    }

    public void setSize(float x, float y){
        sclx = x;
        scly = y;
    }

    public float getPosx(){
        return posx;
    }

    public float getPosy(){
        return posy;
    }

    public void update(long currentTime) {
        if(currentTime-tempsActual >= 40/this.speed){
            currentFrame = (currentFrame+1)%animations.get(currentAnimation).size();
            tempsActual = currentTime;
        }
    }

    public void draw(GL10 gl) {
        gl.glTranslatef(posx, posy, 0.0f);
        gl.glScalef(sclx, scly, 1.0f);
        animations.get(currentAnimation).get(currentFrame).draw(gl);
    }
}
