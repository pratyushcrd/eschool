package com.eureka_main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.view.MaterialListView;

/**
 * Created by Pratyush on 10-04-2015.
 */
public class St_TchrsCards extends Student_Intro {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tc_intro, container, false);


        rootView.findViewById(R.id.tcintro_rl).setVisibility(View.GONE);
        mListView = (MaterialListView) rootView.findViewById(R.id.tcintro_list);


        if (getActivity() != null) {

            adddCard("Website", "www.sonamtuitions.com");
            adddCard("Ulhasnagar-3", "2565999 / 2565766");
            adddCard("Ulhasnagar-5", "2532484");
            adddCard("Ambernath", "2606979 / 2610040");
            adddCard("Kalyan East", "2365401 / 2363982");
            adddCard("Kalyan West", "2311588 / 2312471");
            adddCard("Dombivili West", "2483841");

        }
        return rootView;
    }

    private void adddCard(String title, String text) {

        card = new SmallImageCard(getActivity().getApplicationContext());
        card.setTitle(title);
        card.setDescription(text);
        card.setTitleColor(getResources().getColor(R.color.accent));
        card.setDescriptionColor(getResources().getColor(R.color.secondary_text));
        if ((title + text).contains("www"))
            card.setDrawable(getResources().getDrawable(R.drawable.ic_web));
        else
            card.setDrawable(getResources().getDrawable(R.drawable.ic_action_name5));
        card.setBackgroundColor(Color.parseColor("#ffffff"));
        mListView.add(card);


    }
}
