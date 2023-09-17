package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Result extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        Intent intent = getIntent();
        String clock = intent.getStringExtra("com.example.project1.CLOCK");
        TextView tv = (TextView) findViewById(R.id.resultText);
        tv.setText("Used " + clock + " seconds.");

    }

    public void play(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
