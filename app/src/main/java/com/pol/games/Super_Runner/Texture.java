package com.pol.games.Super_Runner;

/**
 * Created by Pol on 21/10/15.
 *
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

public class Texture {

    private int[] textureID;
    private int widht, height;

    public Texture(GL10 gl, Context context, int resource_id) {
        //Get the texture from the Android resource directory

        InputStream is = context.getResources().openRawResource(resource_id);
        Bitmap bitmap = null;

        try {
            //BitmapFactory is an Android graphics utility for images
            bitmap = BitmapFactory.decodeStream(is);

        } finally {
            //Always clear and close
            try {
                is.close();
                is = null;
            } catch (IOException e) {
            }
        }

        //Generate and fill the texture with the image

        textureID = new int[1];
        gl.glGenTextures(1, textureID, 0);

        gl.glBindTexture(gl.GL_TEXTURE_2D, textureID[0]);

        gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_NEAREST);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

        widht = bitmap.getWidth();
        height = bitmap.getHeight();

        //Clean up
        bitmap.recycle();

    }

    public int[] getTexture_id() {
        return this.textureID;
    }

    public int getWidht(){
        return this.widht;
    }

    public int getHeight(){
        return this.height;
    }
}

