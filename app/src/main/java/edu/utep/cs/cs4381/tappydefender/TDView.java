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
import java.util.concurrent.CopyOnWriteArrayList;

import edu.utep.cs.cs4381.tappydefender.model.EnemyShip;
import edu.utep.cs.cs4381.tappydefender.model.PlayerShip;
import edu.utep.cs.cs4381.tappydefender.model.SpaceDust;


public class TDView extends SurfaceView
        implements Runnable {

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


    private List<EnemyShip> enemyShips = new CopyOnWriteArrayList<>();
    private List<SpaceDust> dustParticles = new CopyOnWriteArrayList<>();


    public TDView(Context context, int width, int height) {
        super(context);
        this.context = context;
        screenX = width;
        screenY = height;
        player = new PlayerShip(context,width,height);
        holder = getHolder();
        paint = new Paint();
        dustColor = new Paint();

        enemyShips.add(new EnemyShip(context, width, height));
        enemyShips.add(new EnemyShip(context, width, height));
        enemyShips.add(new EnemyShip(context, width, height));

        for(int i = 0; i<=100; i++){
            dustParticles.add(new SpaceDust(width,height));
        }

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
        for (EnemyShip enemy: enemyShips) {
            if (Rect.intersects(player.getHitbox(), enemy.getHitbox())) {
                enemy.setX(-enemy.getBitmap().getWidth());
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
    }



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

