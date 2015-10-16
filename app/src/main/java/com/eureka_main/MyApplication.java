package com.eureka_main;

import android.app.Application;
import android.content.SharedPreferences;

public class MyApplication extends Application {

    //public static String host_add = "http://clubramble.com/admin/pratyush/school/";
    //public static String host_add = "http://192.168.0.3/school/";
    public static String host_add = "http://eurekaapp.in/sonam_tut/";
    public static String host_add_stud = host_add+"student.php?";
    public static String host_add_teacher = host_add+"teacher.php?";

    public static int push_pos = 0;
    public static Boolean push_rec = false;

    public static String session_name;
    public static Boolean session_active;
    public static String userLoggedIn_key = "login_boolean";
    public static String username_key = "login_username";
    public static String svroll = "svclass_roll";
    public static String svreg = "svregistration_num";
    public static String svfather = "sv_father";
    public static String svmother = "sv_mother";
    public static String svclass = "sv_class";
    public static String svsection = "sv_section";
    public static String svlocation = "sv_location";
    public static String svemail = "sv_email";
    public static String svphone = "sv_phone";
    public static String svdoa = "sv_doa";
    public static String svdob = "sv_dob";
    public static String svname = "sv_name";
    public static String svdes = "sv_des";
    public static String svstud = "sv_isstud";


    public static int sel_y, sel_m, sel_d;


    public static String attcurrent_class, attcurrent_section;

    SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();


    }
}
