package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 10;
    private static final int ROW_COUNT = 10;
    private ArrayList<TextView> cell_tvs;
    private Random rand = new Random();
    // flags
    boolean lost = false;
    boolean win = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                tv.setTextColor(Color.GRAY);
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
            tv.setTextColor(Color.GRAY);

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

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int tag = (int) tv.getTag();
        // it's a mine
        if(tv.getText() == "M") {
            //BOOM YOU LOST
            tv.setTextColor(Color.GREEN);
            lost = true;
        }
        // it has >0 adj mines
        else if( tag > 0) {
            tv.setText(Integer.toString(tag));
            tv.setBackgroundColor(Color.LTGRAY);
        }
        // it has 0 adj mines, then keep clearing
        else{
            clear(tv.getId());
        }

    }

    public void onClickScreen(View v){
        if( win || lost){
            Intent intent = new Intent(this, Result.class);
            startActivity(intent);
        }
        return;
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

        if( id < 0 || id >=120)
            return;
        TextView tv = findViewById(id);
        if(tv.getDrawingCacheBackgroundColor() == Color.LTGRAY)
            return;
        int tag = (int) tv.getTag();
        tv.setBackgroundColor(Color.LTGRAY);

        if(tag > 0) {
            tv.setText(Integer.toString(tag));
            tv.setTextColor(Color.GRAY);
        }
        else{
            clear(id - 10);
            //clear(id + 10);
            if(id % 10 != 0)
                clear(id -1);
//            if(id % 10 != 9)
//                clear(id+1);
        }
        return;

    }

}