package com.pol.games.Super_Runner;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Pol on 21/10/15.
 *
 */
public class TextureManager {


    HashMap<String, Texture> textureData;
    HashMap<String, FloatBuffer> textureCoords;

    private GL10 gl;
    private Context ctx;

    public TextureManager(Context ctx, GL10 gl){
        this.gl = gl;
        this.ctx = ctx;
        textureData = new HashMap<>();
        textureCoords = new HashMap<>();
    }

    public void addTexture(String nom, int resource_id) {
        addTexture(nom, resource_id, new float[] {0, 0, 1, 0, 1, 1, 0, 1});

    }

    public void addTexture(String nom, int resource_id, float[] texcoords){
        textureData.put(nom, new Texture(gl, ctx, resource_id));
        ByteBuffer vbb = ByteBuffer.allocateDirect(texcoords.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer texcoordsBuffer = vbb.asFloatBuffer();
        texcoordsBuffer.put(texcoords);
        texcoordsBuffer.position(0);
        textureCoords.put(nom, texcoordsBuffer);
    }

    public Texture getTexture(String s){
        return textureData.get(s);
    }

    public FloatBuffer getTextureCoords(String s){
        return textureCoords.get(s);
    }

}
