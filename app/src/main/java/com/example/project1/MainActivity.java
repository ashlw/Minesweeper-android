package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TextView> cell_tvs;
    private Random rand = new Random();
    private int clock = 0;
    private int flag_count = 0;
    private int[] mines = new int[4];
    private int cells_revealed = 0;
    // flags
    boolean lost = false;
    boolean win = false;
    boolean running = true;
    boolean dig = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            clock = savedInstanceState.getInt("clock");
            running = savedInstanceState.getBoolean("running");
        }

        runTimer();

        cell_tvs = new ArrayList<TextView>();
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);

        LayoutInflater li = LayoutInflater.from(this);
        int id = 0; // id = 0 - 119
        for (int i = 0; i<12; i++) {
            for (int j=0; j<10; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                tv.setTag(0);
                tv.setId(id);
                tv.setText("");
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
                id++;
            }
        }

        // set up 4 random mines and set numbers in adjacent tiles
        for(int i = 0; i<4; i++){
            int mine = rand.nextInt(120);
            TextView tv = (TextView) findViewById(mine);
            while(tv.getText() == "M"){
                mine = rand.nextInt(120);
                tv = (TextView) findViewById(mine);
            }
            tv.setText("M");
            tv.setTag(-2);
            mines[i] = mine;
            // check for leftmost
            if( mine%10 != 0){
                increment(mine-1);
                // check if at top
                if(mine > 9){
                    increment(mine-10);
                    increment(mine-11);
                }
                // check if at bottom
                if(mine < 110){
                    increment(mine+10);
                    increment(mine+9);
                }
            }else{
                if(mine > 9)
                    increment(mine-10);
                if(mine < 110)
                    increment(mine+10);
            }
            //check for rightmost
            if( mine %10 != 9){
                increment(mine+1);
                if(mine > 9)
                    increment(mine-9);
                if(mine < 110)
                    increment(mine+11);
            }
        } //endFor

    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int tag = (int) tv.getTag();

        if(win || lost){
            endGame();
        }
        //dig mode
        if(dig) {

            if(tv.getText() == getString(R.string.mine) ||
            tv.getText() == getString(R.string.flag)){}
            else if ((int)tv.getTag() == -2) {
                String bomb = getString(R.string.mine);
                tv.setText(bomb);
                lost = true;
                running = false;
                revealMines();
            }
            //has a number
            else if (tag > 0) {
                tv.setText(Integer.toString(tag));
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.LTGRAY);
                cells_revealed++;
                if(cells_revealed == 116){
                    running = false;
                    win = true;
                    revealMines();
                }
            }
            else {
                clear(tv.getId());
            }
        }
        else{ // flag mode
            if(tv.getText() == getString(R.string.flag)){
                tv.setText("");
                flag_count--;
            }
            else {
                tv.setText(R.string.flag);
                flag_count++;
            }
            TextView count = (TextView) findViewById(R.id.flagcount);
            count.setText(Integer.toString(flag_count));
        }
    }

    public void onClickScreen(View v){
        endGame();
    }

    public void switchMode(View v){
        TextView tv = (TextView) v;
        dig = !dig;
        if(dig)
            tv.setText(R.string.pick);
        else
            tv.setText(R.string.flag);
    }

    public void increment(int id){
        TextView tv = findViewById(id);
        if(tv.getText()!="M") {
            int num = (int) tv.getTag();
            num++;
            tv.setTag(num);
        }
    }

    public void clear(int id){

        //out of bounds
        if( id < 0 || id >=120)
            return;
        TextView tv = findViewById(id);
        int tag = (int) tv.getTag();
        //visited
        if(tag == -1)
            return;
        tv.setBackgroundColor(Color.LTGRAY);
        //mark as visited and increment counter
        tv.setTag(-1);
        cells_revealed++;
        if(cells_revealed == 116){
            running = false;
            win = true;
        }
        if(tag > 0) {
            tv.setText(Integer.toString(tag));
            tv.setTextColor(Color.GRAY);
            tv.setTag(-1);
        }
        else{
            clear(id - 10);
            clear(id + 10);
            if(id % 10 != 0)
                clear(id -1);
            if(id % 10 != 9)
                clear(id+1);
        }
        return;

    }

    private void runTimer() {
        final TextView timeView = (TextView) findViewById(R.id.timer);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                String time = String.format("%d", clock);
                timeView.setText(time);

                if (running) {
                    clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void revealMines(){
        for(int i = 0; i<4; i++){
            TextView tv = (TextView) findViewById(mines[i]);
            tv.setText(R.string.mine);
        }
    }

    private void endGame(){
        if( win || lost){
            Intent intent = new Intent(this, Result.class);
            intent.putExtra("com.example.project1.CLOCK", Integer.toString(clock));
            intent.putExtra("win", win);
            startActivity(intent);
        }
        return;
    }

}