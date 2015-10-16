package com.eureka_main;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by Pratyush on 13-05-2015.
 */
public class Test1 extends PNotif{
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), clsc[position], Toast.LENGTH_LONG).show();


        MyApplication.attcurrent_section = section_list[position];
        MyApplication.attcurrent_class = class_list[position];
        Intent startIntent = new Intent(getActivity().getApplicationContext(), Test2.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().getApplicationContext().startActivity(startIntent);

    }
}
