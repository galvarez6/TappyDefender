package edu.utep.cs.cs4381.tappydefender.model;

import java.util.Random;

public class SpaceDust {

    private int x, y;
    private int speed;


    //set screen bounds for the dust
    private int maxX, maxY, minX, minY;



    //constructor for the dust
    public SpaceDust(int screenX, int screenY){
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        Random randomDust = new Random();
        //dust moves in random speed
        speed = randomDust.nextInt(10);

        //the spots where the dust will fall
        maxX = randomDust.nextInt(maxX);
        maxY = randomDust.nextInt(maxY);

        //when dust goes past far left respawn the dust

    }//end of dust constructor

    public void update(int playerSpeed) {
        x -= playerSpeed;
        x -= speed;
        Random randomDust = new Random();

        if(x < 0){
            x = maxX;
            y = randomDust.nextInt(maxY);
            speed = randomDust.nextInt(15);
        }
    }//end of dust speed update

    public int getX(){
        return x;
    }//end of x

    public int getY(){
        return y;
    }//end of y


}//end of SpaceDust
