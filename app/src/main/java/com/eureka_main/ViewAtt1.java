package com.eureka_main;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by Pratyush on 10-04-2015.
 */
public class ViewAtt1 extends PNotif {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), clsc[position], Toast.LENGTH_LONG).show();


        MyApplication.attcurrent_section = section_list[position];
        MyApplication.attcurrent_class = class_list[position];
        Intent startIntent = new Intent(getActivity().getApplicationContext(), ClassList3.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().getApplicationContext().startActivity(startIntent);

    }
}
