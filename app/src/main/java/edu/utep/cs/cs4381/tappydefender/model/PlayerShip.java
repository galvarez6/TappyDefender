package edu.utep.cs.cs4381.tappydefender.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import edu.utep.cs.cs4381.tappydefender.R;

public class PlayerShip {

    private int y;
    private int x;
    private int speed;
    private Bitmap bitmap;

    private static final int GRAVITY = -12;
    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 20;

    private Rect hitbox;

    private boolean boosting;

    private int maxY;
    private int minY;

    private int shieldStrength;


    public PlayerShip(Context context, int width, int height) {
        x = 50;
        y = 50;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ship);
        maxY = height - bitmap.getHeight(); // Q: why?
        minY = 0;

        shieldStrength = 2;

        hitbox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }



    public void setBoosting(boolean flag) {
        boosting = flag;
    }

    public void update() {
        if (boosting) {
            speed += 2;
        } else {
            speed -= 5;
        }
        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }

        y -= speed + GRAVITY;
        if (y < minY) {
            y = minY;
        }
        if (y > maxY) {
            y = maxY;
        }

        hitbox.set(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
    }


    public float getX() {
        return x;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public float getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public Rect getHitbox() { return hitbox; }

    public int getShieldStrength() { return shieldStrength; }
}
