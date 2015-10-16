package com.eureka_main;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.view.MaterialListView;


public class TcIntro extends Fragment {


    public TcIntro() {
        // Required empty public constructor
    }
    SmallImageCard card;
    MaterialListView mListView;
    TextView tv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tc_intro, container, false);

        tv = (TextView) rootView.findViewById(R.id.tcintro_txt);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String str  = sp.getString(MyApplication.svname,"");
        String str1 = sp.getString(MyApplication.svclass,"") + sp.getString(MyApplication.svsection,"");
        String str2 = sp.getString(MyApplication.svroll,"");
        String str3 = sp.getString(MyApplication.svfather,"");
        String str4 = sp.getString(MyApplication.svmother,"");
        String str5 = sp.getString(MyApplication.svreg,"");
        String str6 = sp.getString(MyApplication.svemail,"");

        tv.setText(str);
        mListView = (MaterialListView) rootView.findViewById(R.id.tcintro_list);

        if(getActivity()!=null) {



            card = new SmallImageCard(getActivity().getApplicationContext());
            card.setTitle("Email");
            card.setDescription(str6);
            card.setTitleColor(getResources().getColor(R.color.accent));
            card.setDescriptionColor(getResources().getColor(R.color.secondary_text));
            card.setDrawable(getResources().getDrawable(R.drawable.ic_action_name5));
            card.setBackgroundColor(Color.parseColor("#ffffff"));
            mListView.add(card);

        }
        return rootView;
    }


}
