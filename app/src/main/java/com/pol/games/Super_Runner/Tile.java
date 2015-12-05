package com.pol.games.Super_Runner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Pol on 27/10/15.
 *
 */
public class Tile {

    public Square square;

    private float x1, y1, x2, y2, x3, y3, x4, y4;

    public Tile(int numtile, Texture tex, int tile_w, int tile_h, int initalNumTile, int rightNumTile)
    {
        //Catch every tile of the tileMap
        int numTilesWidht = tex.getWidht()/tile_w;

        x1 = (float) tile_w*(numtile%numTilesWidht);
        y1 = (float) tile_h*(numtile/numTilesWidht);
        x2 = (float) tile_w*(numtile%numTilesWidht)+tile_w;
        y2 = (float) tile_h*(numtile/numTilesWidht);
        x3 = (float) tile_w*(numtile%numTilesWidht)+tile_w;
        y3 = (float) (tile_h*(numtile/numTilesWidht)+tile_h);
        x4 = (float) tile_w*(numtile%numTilesWidht);
        y4 = (float) (tile_h*(numtile/numTilesWidht)+tile_h);

        x1 = x1/ tex.getWidht();
        y1 = y1/ tex.getHeight();
        x2 = x2/ tex.getWidht();
        y2 = y2/ tex.getHeight();
        x3 = x3/ tex.getWidht();
        y3 = y3/ tex.getHeight();
        x4 = x4/ tex.getWidht();
        y4 = y4/ tex.getHeight();

        //Fix pixel aproximation
        if(numtile == initalNumTile) {
            x1 = x1 + 0.003f;
            x4 = x4 + 0.003f;
        }
        if(initalNumTile == rightNumTile){
            x2 = x2 - 0.003f;
            x3 = x3 - 0.003f;
        }
        y4 = y4 - 0.001f;
        y3 = y3 - 0.001f;

        y1 = y1 + 0.001f;
        y2 = y2 + 0.001f;


        float[] list= new float[]{x4, y4, x1, y1, x2, y2, x3, y3};

        FloatBuffer tileBuffer;

        ByteBuffer vbb = ByteBuffer.allocateDirect(list.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        tileBuffer = vbb.asFloatBuffer();
        tileBuffer.put(list);
        tileBuffer.position(0);

        //Put tile into square
        square = new Square();
        square.setTextureImage(tex);
        square.setTextureCoords(tileBuffer);

    }

    public void draw(GL10 gl) { square.draw(gl);}
}
