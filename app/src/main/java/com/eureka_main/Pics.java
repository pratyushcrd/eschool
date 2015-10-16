package com.eureka_main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jpardogo.listbuddies.lib.views.ListBuddiesLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Pics extends Fragment {


    public Pics() {
        // Required empty public constructor
    }


    List<String> imageUrls_left = new ArrayList<String>(Arrays.asList(
            MyApplication.host_add+"pics/1.jpg",MyApplication.host_add+"pics/2.jpg",MyApplication.host_add+"pics/3.jpg",
            MyApplication.host_add+"pics/4.jpg",MyApplication.host_add+"pics/5.jpg",MyApplication.host_add+"pics/6.jpg"
    ));;
    List<String> imageUrls_right = new ArrayList<String>(Arrays.asList(
            MyApplication.host_add+"pics/6.jpg",MyApplication.host_add+"pics/5.jpg",MyApplication.host_add+"pics/4.jpg",
            MyApplication.host_add+"pics/3.jpg",MyApplication.host_add+"pics/1.jpg",MyApplication.host_add+"pics/2.jpg"
    ));;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pics, container, false);
        ListBuddiesLayout listBuddies = (ListBuddiesLayout) rootView.findViewById(R.id.listbuddies);


        CircularAdapter adapter = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.image_size1), imageUrls_left);
        CircularAdapter adapter2 = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.image_size2), imageUrls_right);

        listBuddies.setAdapters(adapter, adapter2);
        return rootView;
    }


}
