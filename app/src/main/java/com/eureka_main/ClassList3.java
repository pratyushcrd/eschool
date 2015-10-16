package com.eureka_main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Pratyush on 10-04-2015.
 */
public class ClassList3 extends ClassList2{


    public static String current_st_reg,current_st_nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("View Attendance");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        clk_pos = position;
          current_st_reg = st_reg[position];
        current_st_nm = st_list[position];
        Intent i = new Intent(this,ViewAtt2.class);
        startActivity(i);


    }

}
