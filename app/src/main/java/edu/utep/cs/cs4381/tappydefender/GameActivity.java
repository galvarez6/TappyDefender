package edu.utep.cs.cs4381.tappydefender;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



public class GameActivity extends AppCompatActivity {

    private TDView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        gameView = new TDView(this, size.x, size.y);
        setContentView(gameView);

        /*gameView.setPauseListener(i -> {gameView.invalidate();
            Toast.makeText(this, "clicked"+i,Toast.LENGTH_SHORT).show();});*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }


}

