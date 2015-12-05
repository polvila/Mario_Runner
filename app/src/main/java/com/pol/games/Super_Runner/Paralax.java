package com.pol.games.Super_Runner;

import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Pol on 3/11/15.
 *
 */
public class Paralax {

    private LinkedList<TileMap> lltm;
    private LinkedList<Float> llzOrder;
    private LinkedList<Float> llspeed;
    private LinkedList<Long> lltempsActual;
    private LinkedList<Float> llx;

    private int speed;

    private int widthWindow;

    public Paralax(int widthWindow){
        lltm = new LinkedList<>();
        llzOrder = new LinkedList<>();
        llspeed = new LinkedList<>();
        lltempsActual = new LinkedList<>();
        llx = new LinkedList<>();
        this.widthWindow = widthWindow;
        speed = 1;
    }

    public void addTileMap(TileMap tm, float zOrder, float speed){
        lltm.add(tm);
        llzOrder.add(zOrder);
        llspeed.add(speed);
        llx.add(0.0f);
        lltempsActual.add(System.currentTimeMillis());
    }

    public void setSpeed(int s){
        speed = s;
    }

    public void update(long currentTime){
        for(TileMap tm : lltm){
            if(currentTime-lltempsActual.get(lltm.indexOf(tm)) >= 40/speed){
                llx.add(lltm.indexOf(tm), (llx.get(lltm.indexOf(tm)) - 0.1f * llspeed.get(lltm.indexOf(tm))) % tm.getNumtiles_width());
                llx.remove(lltm.indexOf(tm) + 1);
                lltempsActual.add(lltm.indexOf(tm), currentTime);
                lltempsActual.remove(lltm.indexOf(tm) + 1);
            }
        }
    }

    public void draw(GL10 gl){
        for(TileMap tm : lltm){
            gl.glPushMatrix();
            gl.glTranslatef(llx.get(lltm.indexOf(tm)), 0.0f, 0.0f);
            if(lltm.indexOf(tm) == lltm.size()-1) gl.glTranslatef(0, -7, 0);
            if(lltm.indexOf(tm) == lltm.size()-2) gl.glTranslatef(0, -3, 0);
            if(lltm.indexOf(tm) == lltm.size()-3) gl.glTranslatef(0, -3, 0);
            if(lltm.indexOf(tm) == lltm.size()-4) gl.glTranslatef(0, -1, 0);
            tm.draw(gl);
            gl.glPopMatrix();

            if(tm.getNumtiles_width() + llx.get(lltm.indexOf(tm)) < widthWindow) {
                gl.glPushMatrix();
                gl.glTranslatef(tm.getNumtiles_width() + llx.get(lltm.indexOf(tm)), 0.0f, 0.0f);
                if(lltm.indexOf(tm) == lltm.size()-1) gl.glTranslatef(0, -7, 0);
                if(lltm.indexOf(tm) == lltm.size()-2) gl.glTranslatef(0, -3, 0);
                if(lltm.indexOf(tm) == lltm.size()-3) gl.glTranslatef(0, -3, 0);
                if(lltm.indexOf(tm) == lltm.size()-4) gl.glTranslatef(0, -1, 0);
                tm.draw(gl);
                gl.glPopMatrix();
            }
        }
    }

    public void setWidthWindow(int widthWindow){
        this.widthWindow = widthWindow;
    }
}
