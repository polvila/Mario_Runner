package com.pol.games.Super_Runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;

/**
 * Created by Pol on 27/10/15.
 *
 */
public class TileMap {

    public HashMap<Integer, Tile> tiles = new HashMap<>();

    int tile_width;
    int tile_height;

    int numtiles_width;
    int numtiles_height;

    int[][] tilelist;

    public TileMap(Texture tex, int filenameId, Context ctx) {
        // Load the data from the file
        String temp[];

        InputStream inputStream = ctx.getResources().openRawResource(filenameId);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);

        try {
            //Read the tile size
            temp = buffreader.readLine().split(" ");
            tile_width = Integer.parseInt(temp[0]);
            tile_height = Integer.parseInt(temp[1]);

            //Read the number of tiles
            temp = buffreader.readLine().split(" ");
            numtiles_width = Integer.parseInt(temp[0]);
            numtiles_height = Integer.parseInt(temp[1]);

            //Create the tiles
            tilelist = new int[numtiles_height][numtiles_width];

            for(int i=0; i < numtiles_height ; i++) {
                temp = buffreader.readLine().split(" ");
                for(int j=0; j < numtiles_width ; j++) {
                    tilelist[i][j] = Integer.parseInt(temp[j]);
                }
            }
        } catch (IOException e) {
            System.out.println(Log.getStackTraceString(e));
        }

        // Create all the tiles that we will need later
        for(int i=0; i < numtiles_height ; i++) {
            for(int j=0; j < numtiles_width ; j++) {
                if(tiles.get(tilelist[i][j]) == null){
                    Tile t;
                    if(j != numtiles_width-1) {
                        t = new Tile(tilelist[i][j], tex, tile_width, tile_height, tilelist[i][0], tilelist[i][j + 1]);
                    }else{
                        t = new Tile(tilelist[i][j], tex, tile_width, tile_height, tilelist[i][0], tilelist[i][j]);
                    }

                    tiles.put(tilelist[i][j], t);
                }
            }
        }

    }

    public int getTile_width() {
        return tile_width;
    }

    public int getTile_height() {
        return tile_height;
    }

    public int getNumtiles_width() {
        return numtiles_width;
    }

    public int getNumtiles_height() {
        return numtiles_height;
    }

    public void draw(GL10 gl) {
        //Draw all tileMap
        gl.glPushMatrix();
        for(int i=0; i < numtiles_height ; i++) {
            for(int j=0; j < numtiles_width ; j++) {
                tiles.get(tilelist[i][j]).draw(gl);
                gl.glTranslatef(1.0f, 0.0f, 0.0f);

            }
            gl.glTranslatef(-numtiles_width, -1.0f, 0.0f);
        }
        gl.glPopMatrix();

    }
}
