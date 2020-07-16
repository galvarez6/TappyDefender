package edu.utep.cs.cs4381.tappydefender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(view -> {
            //this is an intent
            startActivity(new Intent(this, GameActivity.class));
            //im done this acticity will be destoryed (when you go back the main was killed so the app closes

            finish();
        });

    }
}