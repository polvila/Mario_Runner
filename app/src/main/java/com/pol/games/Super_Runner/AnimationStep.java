package com.pol.games.Super_Runner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Pol on 27/10/15.
 *
 */
public class AnimationStep {

    private Texture texture;
    private float x1, y1, x2, y2, x3, y3, x4, y4;
    private float list[];
    private FloatBuffer texturaBuffer;

    public Square square;

    public AnimationStep(Texture texture, int x, int y, int w, int h){
        //Catch every step of the animation
        this.texture = texture;
        x4 = (float) x;
        y4 = (float) -y;
        x3 = (float) x+w;
        y3 = (float) -y;
        x2 = (float) x+w;
        y2 = (float) -(y+h);
        x1 = (float) x;
        y1 = (float) -(y+h);

        x1 = x1/ texture.getWidht();
        y1 = y1/ texture.getHeight();
        x2 = x2/ texture.getWidht();
        y2 = y2/ texture.getHeight();
        x3 = x3/ texture.getWidht();
        y3 = y3/ texture.getHeight();
        x4 = x4/ texture.getWidht();
        y4 = y4/ texture.getHeight();

        x1 = x1 + 0.002f;
        x4 = x4 + 0.002f;
        x2 = x2 - 0.001f;
        x3 = x3 - 0.001f;

        list = new float[]{x4, y4, x1, y1, x2, y2, x3, y3};

        ByteBuffer vbb = ByteBuffer.allocateDirect(list.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        texturaBuffer = vbb.asFloatBuffer();
        texturaBuffer.put(list);
        texturaBuffer.position(0);

        square = new Square();
        square.setTextureImage(texture);
        square.setTextureCoords(this.getFloatBuffer());
    }

    public Texture getTexture(){
        return texture;
    }

    public FloatBuffer getFloatBuffer(){
        return texturaBuffer;
    }

    public void draw(GL10 gl) {

        square.draw(gl);
    }

}
