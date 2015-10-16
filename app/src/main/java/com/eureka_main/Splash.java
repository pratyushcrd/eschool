package com.eureka_main;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class Splash extends Activity {
    ImageView fv;
    Thread tf = new Thread() {
        public void run() {
            try {
                sleep(2500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (d1)
                finish();
            overridePendingTransition(android.R.anim.fade_in,
                    android.R.anim.fade_out);

        }
    };
    Boolean d1 = true;
    Runnable r = new Runnable() {
        public void run() {
        }
    };

    public void color_change() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(getResources().getColor(android.R.color.transparent));

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spl);
        color_change();
        if (getActionBar() != null)
            getActionBar().hide();
        fv = (ImageView) findViewById(R.id.sp_logo);
        tf.start();
        Handler mh = new Handler();
        mh.postDelayed(r, 500);
        fv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                d1 = false;
                finish();
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
            }
        });

       // YoYo.with(Techniques.FadeInUp).duration(2500).playOn(fv);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(

                Glider.glide(Skill.CubicEaseInOut, 2000, ObjectAnimator.ofFloat(fv, "translationY", -60, 0)),
                Glider.glide(Skill.CubicEaseInOut, 2000, ObjectAnimator.ofFloat(fv, "alpha", 0, 1))

        );

        set.setDuration(2000);
        set.start();



    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        try {
            tf.join(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

}
