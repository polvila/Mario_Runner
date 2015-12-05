package com.pol.games.Super_Runner;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.os.Build;
import android.view.MotionEvent;


/**
 * Created by Pol on 27/10/15.
 *
 */
public class MyOpenGLRenderer implements Renderer {

	private Context context;

	private Animation runnerAnimation;
	private Animation skeleton1animation;
	private Animation skeleton2animation;

    private TileMap tileFont;

    private HudText hudText;

	private Paralax paralax;

	private int width;

    private float p = 0.0f;
    private float x;
    private int jumpFrame = 0;
    private int enemyFrame = 0;
    private boolean skeleton1Turn = true;
    private boolean falling = false;
    private int lives = 3;
    private int score = 0;
    private int bestScore = 0;
    private boolean hurted = false;
    private boolean gameOver = false;
    private float runnerPos;

    private boolean touched = false;
    private boolean replay;
    private boolean onPause = false;

    private MediaPlayer runnerSong;
    private MediaPlayer loseSound;
    private SoundPool soundPool;
    private int jumpSound;
    private int hurtSound;
    private int enemyDieSound;

    private SharedPreferences.Editor editor;

	TextureManager tm;

	public MyOpenGLRenderer(Context context){
		this.context = context;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Image Background color
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		tm = new TextureManager(context, gl);
		tm.addTexture("RUFFY", R.drawable.ruffy);
		tm.addTexture("SKELETON1", R.drawable.skeleton1);
		tm.addTexture("SUPER_RUNNER", R.drawable.super_runner);
		tm.addTexture("SKELETON2", R.drawable.skeleton2);
		tm.addTexture("BACKGROUND", R.drawable.background_tiles);
		tm.addTexture("FOREGROUND", R.drawable.foreground_tiles);
        tm.addTexture("TILEFONT", R.drawable.tilefont);

		runnerAnimation = new Animation(tm.getTexture("SUPER_RUNNER"), R.raw.super_runner, context, 10);
		runnerAnimation.setPosition(0.0f, 0.0f);
		runnerAnimation.setCurrentAnimation("walk");

		skeleton1animation = new Animation(tm.getTexture("SKELETON1"), R.raw.skeleton1, context, 0);
		skeleton1animation.setPosition(0.0f, 0.0f);
		skeleton1animation.setCurrentAnimation("walk");

		skeleton2animation = new Animation(tm.getTexture("SKELETON2"), R.raw.skeleton2, context, 0);
		skeleton2animation.setPosition(0.0f, 0.0f);
		skeleton2animation.setCurrentAnimation("walk");

        TileMap background1 = new TileMap(tm.getTexture("BACKGROUND"), R.raw.tilemap1, context);
        TileMap background2 = new TileMap(tm.getTexture("BACKGROUND"), R.raw.tilemap2, context);
        TileMap background3 = new TileMap(tm.getTexture("BACKGROUND"), R.raw.tilemap3, context);
        TileMap background4 = new TileMap(tm.getTexture("BACKGROUND"), R.raw.tilemap4, context);
        TileMap foreground = new TileMap(tm.getTexture("FOREGROUND"), R.raw.tilemap5, context);

		paralax = new Paralax(width);
		paralax.addTileMap(background1, -2.0f, 0.4f);
		paralax.addTileMap(background2, -2.0f, 0.5f);
		paralax.addTileMap(background3, -2.0f, 0.7f);
		paralax.addTileMap(background4, -1.0f, 0.9f);
		paralax.addTileMap(foreground, -1.0f, 1.2f);

        tileFont = new TileMap(tm.getTexture("TILEFONT"), R.raw.tilefont, context);

        SharedPreferences sp = context.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        editor = sp.edit();
        editor.apply();
        bestScore = sp.getInt("bestscore", 0);

        hudText = new HudText(tileFont);

        runnerSong = MediaPlayer.create(context, R.raw.runner_song);
        runnerSong.start();
        loseSound = MediaPlayer.create(context, R.raw.lose);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        }else{
            createOldSoundPool();
        }
        jumpSound = soundPool.load(context, R.raw.jump, 1);
        hurtSound = soundPool.load(context, R.raw.hurt, 1);
        enemyDieSound = soundPool.load(context, R.raw.enemy_die, 1);

        replay = false;

        skeleton1animation.setSize(0.7f, 0.7f);
        skeleton2animation.setSize(0.7f, 1.0f);
        runnerAnimation.setSize(1.0f, 1.3f);

	}

	@Override
	public void onDrawFrame(GL10 gl) {
		
		// Clears the screen and depth buffer.

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glLoadIdentity();

		gl.glPushMatrix();
        if(!gameOver) {
            paralax.update(System.currentTimeMillis());
        }
		gl.glTranslatef(0.0f, 7.0f, 0.0f);
        paralax.setSpeed(4 + (score / 1000));
		paralax.draw(gl);
		gl.glPopMatrix();

        if(!gameOver) {
            enemyFrame++; score++;
            x = ((4 + ((float)score/1000.0f)) * enemyFrame / 25.0f);   //Enemy position calculation
        }
        if(skeleton1Turn) {  //skeleton1 appears
            gl.glPushMatrix();
            gl.glTranslatef(width, 0.9f, 0.0f);
            skeleton1animation.setPosition(-x, 0.0f);    //skeleton1 movement
            if (x > (width + 1) ) {              //Reset skeleton1 position
                x = 0;
                enemyFrame = 0;
                skeleton1Turn = false;
                skeleton1animation.setCurrentAnimation("walk");
            }
            if(!gameOver) {
                skeleton1animation.update(System.currentTimeMillis());
                skeleton1animation.draw(gl);
            }
            gl.glPopMatrix();
        }else {            //skeleton2 appears
            gl.glPushMatrix();
            gl.glTranslatef(width, 0.9f, 0.0f);
            skeleton2animation.setPosition(-x, 0.0f);      //skeleton2 movement
            if (x > (width + 1)) {              //Reset skeleton2 position
                x = 0;
                enemyFrame = 0;
                skeleton1Turn = true;
                skeleton2animation.setCurrentAnimation("walk");
            }
            if(!gameOver) {
                skeleton2animation.update(System.currentTimeMillis());
                skeleton2animation.draw(gl);
            }

            gl.glPopMatrix();
        }

        //SUPER RUNNER
        gl.glPushMatrix();
        gl.glTranslatef(runnerPos, 0.9f, 0.0f);
        gl.glTranslatef(0.5f, 0.0f, 0.0f);
        gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        gl.glTranslatef(-0.5f, 0.0f, 0.0f);
        if(touched && !runnerAnimation.getCurrentAnimation().equals("jump")) { //When you touch, Runner jumps

            soundPool.play(jumpSound, 0.1f, 0.1f, 0, 0, 1.0f);
            runnerAnimation.setCurrentAnimation("jump");

        }
        if(runnerAnimation.getCurrentAnimation().equals("jump")){
            jumpFrame++;
            //Runner jump calculation
            p = 8.0f*(jumpFrame /25.0f)-(0.5f*16.0f*(float)Math.pow((jumpFrame /25.0f),2));
            runnerAnimation.setPosition(0.0f, p);
            if(p == 1.9968f){       //Max height of Runner's jump
                falling = true;
            }
            if (p == 0.0f){         //When Runner arrives to the floor
                runnerAnimation.setCurrentAnimation("walk");
                jumpFrame = 0;
                falling = false;
                touched = false;
            }
        }
        if(!gameOver) {
            runnerAnimation.update(System.currentTimeMillis());
        }
        if(!hurted) {
            runnerAnimation.draw(gl);
        }else{                          //if Runner is hurted, it starts flickering
           if(enemyFrame % 2 == 0){
               runnerAnimation.draw(gl);
           }

        }
        gl.glPopMatrix();
        //Runner END

        //COLLISION (Runner-Enemy)
        if(runnerAnimation.getCurrentAnimation().equals("jump") && runnerAnimation.getPosy() < 0.5f && falling &&
                skeleton1animation.getPosx() < runnerPos +1-width && skeleton1animation.getPosx() > runnerPos -width){

            soundPool.play(enemyDieSound, 1, 1, 0, 0, 1);
            skeleton1animation.setCurrentAnimation("die");   //skeleton1 die

        }else if(runnerAnimation.getCurrentAnimation().equals("jump") && runnerAnimation.getPosy() < 0.5f && falling &&
                skeleton2animation.getPosx() < runnerPos +1-width && skeleton2animation.getPosx() > runnerPos -width){

            soundPool.play(enemyDieSound, 1, 1, 0, 0, 1);
            skeleton2animation.setCurrentAnimation("skeleton");    //skeleton2 die

        }else if((skeleton1animation.getPosx() < runnerPos +0.7f-width && skeleton1animation.getPosx() > runnerPos +0.3f-width
                && !falling && !hurted && runnerAnimation.getPosy() <= 0.5f && !skeleton1animation.getCurrentAnimation().equals("die")) ||
                (skeleton2animation.getPosx() < runnerPos +0.7f-width && skeleton2animation.getPosx() > runnerPos +0.3f-width
                        && !falling && !hurted && runnerAnimation.getPosy() <= 0.5f && !skeleton2animation.getCurrentAnimation().equals("skeleton"))) {
            hurted = true;           //Runner hurted
            lives--;                //Runner loses 1 live
            if(lives == -1){      //-> GAME OVER
                lives = 0;
                enemyFrame = 0;
                x = 0;
                hurted = false;
                gameOver = true;
                runnerSong.pause();
                runnerSong.seekTo(0);
                loseSound.start();
                if(score>bestScore) {
                    bestScore = score;
                    editor.putInt("bestscore", bestScore);
                    editor.commit();
                }
            }else{
                soundPool.play(hurtSound, 1, 1, 0, 0, 1);
            }
        }else if(skeleton1animation.getPosx() < runnerPos -width-2 && skeleton1animation.getPosx() > runnerPos -width-3
                || skeleton2animation.getPosx() < runnerPos -width-2 && skeleton2animation.getPosx() > runnerPos -width-3){
            //When the enemies are away, super_runner stop flickering
            hurted = false;
        }

        //GAME OVER letters
        if(gameOver){
            jumpFrame++;
            p = 8.0f*(jumpFrame /25.0f)-(0.5f*16.0f*(float)Math.pow((jumpFrame /25.0f),2));
            runnerAnimation.setPosition(0.0f, p);

            gl.glPushMatrix();
            gl.glTranslatef(5.0f, 4.0f, 0.0f);
            gl.glScalef(0.5f, 0.5f, 0.0f);
            hudText.setText("GAME OVER");
            hudText.draw(gl);
            gl.glTranslatef(-8.0f, -1.0f, 0.0f);
            gl.glScalef(0.5f, 0.5f, 0.0f);
            hudText.setText("best score");
            hudText.draw(gl);
            gl.glTranslatef(-4.0f, -1.0f, 0.0f);
            hudText.setText(getScore(bestScore));
            hudText.draw(gl);
            gl.glPopMatrix();

            if(!loseSound.isPlaying() && !onPause){
                gl.glPushMatrix();
                gl.glTranslatef(6.0f, 6.0f, 0.0f);
                gl.glScalef(0.5f, 0.5f, 0.0f);
                hudText.setText("retry");
                hudText.draw(gl);
                replay = true;
                gl.glPopMatrix();
            }
        }

        //HUB
        gl.glPushMatrix();
            gl.glTranslatef(2.0f, 7.75f, 0.0f);
            gl.glScalef(0.25f, 0.25f, 0.0f);
        hudText.setText("super runner");
            hudText.draw(gl);

        gl.glTranslatef(-4.0f, -1.0f, 0.0f);
        gl.glPushMatrix();
            gl.glScalef(0.75f, 0.75f, 0.0f);
            tileFont.tiles.get(23).draw(gl);           //X
        gl.glPopMatrix();
        gl.glTranslatef(1.0f, 0.0f, 0.0f);
        hudText.setText(" 0" + lives);
        hudText.draw(gl);
        gl.glPopMatrix();

        gl.glPushMatrix();
            gl.glTranslatef(10.0f, 7.75f, 0.0f);
        gl.glScalef(0.25f, 0.25f, 0.0f);
            hudText.setText("score");
            hudText.draw(gl);
            gl.glTranslatef(-5.0f, -1.0f, 0.0f);


        hudText.setText(getScore(score));
        hudText.draw(gl);

        gl.glPopMatrix();


	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Define the Viewport
		gl.glViewport(0, 0, width, height);
        // Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Reset the projection matrix
		gl.glLoadIdentity();

		//Squares in vertical
		int vsquares = 8;
        // Calculate the aspect ratio of the window
		GLU.gluOrtho2D(gl, 0, vsquares*width/height, 0, vsquares);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		this.width = vsquares*width/height;
		paralax.setWidthWindow(this.width);
        runnerPos = (float)this.width * (4.0f/14.0f);
	}

	public boolean onTouchEvent(MotionEvent motionEvent){
        int eventAction = motionEvent.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN)
		{   //When you touch the screen
            if(!gameOver) {
                touched = true;
            }else if(replay){  //restart the game
                loseSound.pause();
                loseSound.seekTo(0);
                runnerSong.start();
                gameOver = false;
                lives = 3;
                skeleton1Turn = true;
                runnerAnimation.setPosition(0.0f, 0.0f);
                p = 0.0f;
                jumpFrame = 0;
                score = 0;
                replay = false;

            }
		}
		return true;
	}

    public void pauseMusic(){
        runnerSong.pause();
        if(loseSound.isPlaying()){
            loseSound.pause();
        }
        onPause = true;

    }

    public void replayMusic() {
        if (runnerSong != null){
            if (gameOver) {
                if(!replay){
                    loseSound.start();
                }
            }else{
                runnerSong.start();
            }
        }
        onPause = false;
    }

    private String getScore(int score){
        StringBuilder sb = new StringBuilder("");
        int lengthScore = (int)(Math.log10(score) + 1);
        int zeros = 8 - lengthScore;
        for(int i = 0; i<zeros; i++){
            sb.append("0");
        }
        sb.append(score);
        return sb.toString();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(5)
                .build();
    }
    @SuppressWarnings("deprecation")
    protected void createOldSoundPool(){
        soundPool = new SoundPool(5,AudioManager.STREAM_MUSIC,0);
    }


}
