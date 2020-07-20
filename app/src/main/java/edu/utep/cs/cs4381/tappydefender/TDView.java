package edu.utep.cs.cs4381.tappydefender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.utep.cs.cs4381.tappydefender.model.EnemyShip;
import edu.utep.cs.cs4381.tappydefender.model.PlayerShip;
import edu.utep.cs.cs4381.tappydefender.model.Shield;
import edu.utep.cs.cs4381.tappydefender.model.SpaceDust;


public class TDView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private boolean playing;

    private PlayerShip player;
    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint paint;
    private Paint dustColor;


    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;
    private int screenX;
    private int screenY;

    private Context context;

    private boolean gameEnded;


    private List<EnemyShip> enemyShips = new CopyOnWriteArrayList<>();
    private List<SpaceDust> dustParticles = new CopyOnWriteArrayList<>();
    private List<Shield> extraShield = new CopyOnWriteArrayList<>();


    public TDView(Context context, int width, int height) {
        super(context);
        this.context = context;
        screenX = width;
        screenY = height;
        player = new PlayerShip(context,width,height);
        holder = getHolder();
        paint = new Paint();
        dustColor = new Paint();

        startGame();

    }

    private void startGame() {
        Random random = new Random();
        int enemyNum = random.nextInt(7);
        player = new PlayerShip(context, screenX, screenY);
        //draw dust when game starts
        dustParticles.clear();
        for (int i = 0; i < 100; i++) {
            dustParticles.add(new SpaceDust(screenX, screenY));
        }
        //draw enemies
        enemyShips.clear();
        for(int i = 0; i < enemyNum; i++){
            enemyShips.add(new EnemyShip(context,screenX,screenY));
        }
        //draw shields
        extraShield.clear();
        extraShield.add(new Shield(context,screenX,screenY));

        distanceRemaining = 10000; // 10 km
        timeTaken = 0;
        timeStarted = System.currentTimeMillis();
        gameEnded = false;
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }



    private void update() {
        boolean hitDetected = false;
        boolean shieldGet = false;
        for (EnemyShip enemy: enemyShips) {
            if (Rect.intersects(player.getHitbox(), enemy.getHitbox())) {
                hitDetected = true;
                enemy.setX(-enemy.getBitmap().getWidth());
            }
        }
        for (Shield e: extraShield) {
            if (Rect.intersects(player.getHitbox(), e.getHitbox())) {
                shieldGet = true;
                e.setX(-e.getBitmap().getWidth());
            }
        }
        player.update();
        //update the speed of enemy ships when player increases speed
        for (EnemyShip enemy: enemyShips) {
            enemy.update(player.getSpeed());
        }
        //update dust speed when player goes faster
        for( SpaceDust d: dustParticles){
            d.update(player.getSpeed());
        }
        for (Shield e: extraShield) {
            e.update(player.getSpeed());
        }
        //reduce shield levels of player
        if (hitDetected) {
            if (player.reduceShieldStrength() <= 0) {
                gameEnded = true;
            }
        }
        if (shieldGet) {
            player.increaseShieldStrength();
        }
        if (!gameEnded) {
            distanceRemaining -= player.getSpeed();
            timeTaken = System.currentTimeMillis() - timeStarted;
        }
        if (distanceRemaining < 0) {
            if (timeTaken < fastestTime) {
                fastestTime = timeTaken;
            }
            distanceRemaining = 0;
            gameEnded = true;
        }

    }//end of update



    private void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.argb(255, 0, 0, 0));
            Paint hitBoxs = new Paint();
            hitBoxs.setColor(Color.WHITE);

            canvas.drawRect(player.getHitbox().left,player.getHitbox().top,player.getHitbox().right,player.getHitbox().bottom, hitBoxs);
            canvas.drawBitmap(player.getBitmap(), player.getX(),  player.getY(), paint);

            for (EnemyShip enemy: enemyShips) {
                canvas.drawRect(enemy.getHitbox().left,enemy.getHitbox().top, enemy.getHitbox().right,enemy.getHitbox().bottom, hitBoxs);
                canvas.drawBitmap(enemy.getBitmap(), enemy.getX(), enemy.getY(), paint);
            }
            //draw the dust
            dustColor.setColor(Color.WHITE);
            for (SpaceDust dust: dustParticles) {
                canvas.drawCircle(dust.getX(),dust.getY(), 2, dustColor);
            }

            for (Shield e: extraShield) {
                canvas.drawRect(e.getHitbox().left,e.getHitbox().top, e.getHitbox().right,e.getHitbox().bottom, hitBoxs);
                canvas.drawBitmap(e.getBitmap(), e.getX(), e.getY(), paint);
            }
            if (!gameEnded) {
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setStrokeWidth(4);
                paint.setTextSize(48);
                int yy = 50;
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(formatTime("Fastest", fastestTime), 10, yy, paint);
                canvas.drawText("Shield: " + player.getShieldStrength(), 10, screenY - yy, paint);

                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(formatTime("Time", timeTaken), screenX / 2, yy, paint);
                canvas.drawText("Distance: " + distanceRemaining / 1000 + " KM", screenX / 2, screenY - yy, paint);

                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("Speed: " + player.getSpeed() * 60 + " MPS", screenX - 10, screenY - yy, paint);
            }
            else{
                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX/2,100,paint);
                paint.setTextSize(25);
                canvas.drawText("Fastest:" + fastestTime + "s", screenX/2,160,paint);
                canvas.drawText("Time:"+ timeTaken + "s", screenX/2, 200, paint);
                canvas.drawText("Distance Remaining:"+ distanceRemaining + "KM", screenX/2, 240, paint);
                paint.setTextSize(80);
                canvas.drawText("Tap to replay, Gilbert!", screenX/2,350,paint);
            }

            holder.unlockCanvasAndPost(canvas);
        }

    }//end of draw


    private void control() {
        try {
            gameThread.sleep(17); // in milliseconds
        } catch (InterruptedException e) {
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                player.setBoosting(true);
                if (gameEnded) {
                    startGame();
                }
                break;
            case MotionEvent.ACTION_UP:
                player.setBoosting(false);
                break;
        }
        return true;
    }//end of on touch

    private String formatTime(String label, long time) { // time in milliseconds
        return String.format("%s: %d.%03ds", label, time / 1000, time % 1000);
    }//end of format time




}

