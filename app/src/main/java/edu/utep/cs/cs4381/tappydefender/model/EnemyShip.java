package edu.utep.cs.cs4381.tappydefender.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

import edu.utep.cs.cs4381.tappydefender.R;

public class EnemyShip {

    private static final Random random = new Random();

    private Bitmap bitmap;
    private int x, y;
    private int speed = 1;
    private int maxX, minX; // move horizontally from right to left
    private int maxY, minY;

    private Rect hitbox;

    public EnemyShip(Context ctx, int screenX, int screenY){
        bitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.enemy);
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;
        speed = random.nextInt(6) + 10;
        x = screenX;
        y = random.nextInt(maxY) - bitmap.getHeight();

        hitbox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }


    public Bitmap getBitmap() {  return bitmap; }

    public int getX() { return x; }

    public int getY() { return y; }


    public void update(int playerSpeed) {
        x -= playerSpeed;
        x -= speed;
        if (x < minX - bitmap.getWidth()) {
            speed = random.nextInt(10)+10;
            x = maxX;
            y = random.nextInt(maxY) - bitmap.getHeight();
        }
        hitbox.set(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
    }

    public void setX(int x) {
        this.x = x;
    }

    public Rect getHitbox() { return hitbox; }

}

