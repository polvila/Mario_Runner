package com.pol.games.Super_Runner;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Pol on 24/11/15.
 *
 */
public class HudText {


    private String text;
    private TileMap tileFont;


    public HudText(TileMap tileFont){
        this.tileFont = tileFont;
    }

    public void setText(String text){
        this.text = text;
    }

    public void draw(GL10 gl){

        char[] charArray = text.toCharArray();
        int ascii;
        for (char character : charArray) {
            ascii = (int) character;
            if(65 <= ascii && ascii <= 90){ //Uppercase letters
                ascii = ascii % 65;
            }else if(97 <= ascii && ascii <= 122) { //Lowercase letters
                ascii = ascii % 97;
            }else if(49 <= ascii && ascii <= 57) {  //1-9 numbers
                ascii = (ascii % 48) + 26;
            }else if(ascii == 48){      // 0 number
                ascii = (ascii % 48) + 36;
            }else if(ascii == 32){  //space
                ascii = -1;
            }
            if(ascii != -1){
                tileFont.tiles.get(ascii).draw(gl);
            }
            gl.glTranslatef(1.0f, 0.0f, 0.0f);
        }
    }


}
