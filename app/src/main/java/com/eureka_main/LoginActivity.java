package com.eureka_main;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pushbots.push.Pushbots;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener, Runnable {

    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView leftDrawerList;
    private ArrayAdapter<String> navigationDrawerAdapter;
    private String[] leftSliderData = {"Home", "Calendar", "Notification", "Messages", "Gallery", "Pics", "Directory", "Test Series", "Logout"};
    public CaldroidFragment caldroidFragment;
    public Calendar cal;
    public Date date;
    static int frag_position = 0;
    Thread th;
    TextView cl1;
    int[][] atr;
    String[][] cal_events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Pushbots.sharedInstance().init(this);

        nitView();
        init();
        color_change();
        Pushbots.sharedInstance().setCustomHandler(CustomHandler.class);
        Pushbots.sharedInstance().setAlias(sp.getString(MyApplication.svreg, ""));
        Pushbots.sharedInstance().tag(sp.getString(MyApplication.svclass, "") + sp.getString(MyApplication.svsection, ""));
        Pushbots.sharedInstance().setNotifyStatus(true);

        th = new Thread(this);
        th.start();

        fragchange(frag_position);
        check_push();
    }


    public void check_push() {


    }

    private void nitView() {
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationDrawerAdapter = new ArrayAdapter<String>(LoginActivity.this, R.layout.cs_simple_list1, leftSliderData);
        leftDrawerList.setAdapter(navigationDrawerAdapter);
        leftDrawerList.setOnItemClickListener(this);
        if (toolbar != null) {
            toolbar.setTitle("School_Project");
            setSupportActionBar(toolbar);
        }
        initDrawer();
    }

    public void calendar() {
        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.content_frame, caldroidFragment);
        t.commit();

        Toast.makeText(this,"Click on the dates for the event name",Toast.LENGTH_SHORT).show();

        new GetCalInfo().execute(MyApplication.host_add + "/students/" + sp.getString(MyApplication.svreg, "") + "/att.json?q=" + Math.random());

        //Toast.makeText(this, MyApplication.host_add + "students/" + sp.getString(MyApplication.svreg, "") + "/att.json", Toast.LENGTH_SHORT).show();

        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                try {
                    Calendar cl1 = Calendar.getInstance(), cl2 = Calendar.getInstance();
                    cl1.setTime(date);
                    int m = cl1.get(Calendar.MONTH);
                    int y = cl1.get(Calendar.YEAR);
                    int d = cl1.get(Calendar.DATE);

                    if (cl2.get(Calendar.YEAR) == y)
                        if (cal_events[m][d].length() > 0)
                            Toast.makeText(LoginActivity.this, cal_events[m][d], Toast.LENGTH_SHORT).show();
                }catch (Exception e){}

            }

            @Override
            public void onChangeMonth(int month, int year) {
                cal = Calendar.getInstance();
                cl1.setText("");
                if (atr != null && cal.get(Calendar.YEAR)==year) {
                    try {

                        int present = 0, absent = 0;
                        int mon_cur = month-1;
                        for (int i = 0; i < 32; ++i)

                            if (atr[mon_cur][i] == 1)
                                ++absent;
                            else if (atr[mon_cur][i] == 2)
                                ++present;
                        //Toast.makeText(LoginActivity.this, "Present = " + present + " days / Absent = " + absent + " days", Toast.LENGTH_SHORT).show();
                        cl1.setText("Present = " + present + " days"+" / Absent = " + absent + " days");

                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onLongClickDate(Date date, View view) {
            }

            @Override
            public void onCaldroidViewCreated() {
                if (atr != null) {
                    try {
                        cal = Calendar.getInstance();
                        int present = 0, absent = 0;
                        int mon_cur = cal.get(Calendar.MONTH);
                        for (int i = 0; i < 32; ++i)
                            if (atr[mon_cur][i] == 1)
                                ++absent;
                            else if (atr[mon_cur][i] == 2)
                                ++present;
                        //Toast.makeText(LoginActivity.this, "Present = " + present + " days / Absent = " + absent + " days", Toast.LENGTH_SHORT).show();
                        cl1.setText("Present = " + present + " days"+" / Absent =" + absent + " days");
                    } catch (Exception e) {
                    }
                }
            }

        };

        caldroidFragment.setCaldroidListener(listener);
    }

    private void init() {
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ed = sp.edit();

        cl1 = (TextView) findViewById(R.id.cal_txt1);
        cl1.setVisibility(View.INVISIBLE);
        String str = sp.getString(MyApplication.svname, "");
        Toast.makeText(this, "Welcome " + str + "!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

    }

    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("About").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                final ImageView input = new ImageView(getApplicationContext());
                input.setImageDrawable(getResources().getDrawable(R.drawable.eureka));
                input.setAdjustViewBounds(true);

                final AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);

                alert.setTitle("Developed by");
                alert.setMessage("VMR TECH");

                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {


                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void fragchange(int position) {
        frag_position = position;
        Fragment fragment;


        cl1.setVisibility(View.INVISIBLE);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(leftSliderData[position]);
        }


        if (position == 0) {
            fragment = new Student_Intro();

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else if (position == 3) {
            fragment = new StPNotif();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else if (position == 4) {
            fragment = new GallerySt();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else if (position == 2) {
            fragment = new StClNotif();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else if (position == 5) {


            fragment = new Pics();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else if (position == 6) {


            fragment = new St_TchrsCards();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else if (position == 7) {


            fragment = new StMarks();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else if (position == 8) {


            frag_position = 0;
            new CheckNet().execute(MyApplication.host_add + "check.php");

        } else if (position != 1) {
            fragment = new Student_Intro();
            Bundle args = new Bundle();
            args.putInt(Student_Intro.ARG_PLANET_NUMBER, position);
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else {
            calendar();
            cl1.setVisibility(View.VISIBLE);
        }

    }


    public void logout() {
        ed.clear();
        ed.apply();


        Pushbots.sharedInstance().unRegister();
        Pushbots.sharedInstance().setNotificationEnabled(false);

        finish();
        Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(startIntent);
    }

    public void color_change() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
            window.setNavigationBarColor(getResources().getColor(android.R.color.black));

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        fragchange(position);
        // Highlight the selected item, update the title, and close the drawer
        leftDrawerList.setItemChecked(position, true);
        drawerLayout.closeDrawers();


    }

    @Override
    public void run() {
        try {
            Thread.sleep(2500);
            Pushbots.sharedInstance().setCustomHandler(CustomHandler.class);
            Pushbots.sharedInstance().setAlias(sp.getString(MyApplication.svreg, ""));
            Pushbots.sharedInstance().tag(sp.getString(MyApplication.svclass, "") + sp.getString(MyApplication.svsection, ""));
            Pushbots.sharedInstance().setNotifyStatus(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    String JSONResp = "nothing";

    public class GetCalInfo extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private int resCode;


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            int year = 2015, month = 0, day = 0;
            String col;
            int color;
            JSONArray arr = null;
            JSONObject jobj = null;
            Boolean aoh = false;
            try {
                arr = new JSONArray(s);
                resCode = 201;


                atr = new int[13][32];
                cal_events = new String[13][32];
                for (int i = 0; i < 12; ++i)
                    for (int j = 0; j < 32; ++j) {
                        atr[i][j] = 0;
                        cal_events[i][j] = "";
                    }

                for (int i = 0; i < arr.length(); i++) {
                    jobj = arr.getJSONObject(i);


                    year = Integer.parseInt(jobj.getString("y"));
                    month = Integer.parseInt(jobj.getString("m"));
                    --month;
                    day = Integer.parseInt(jobj.getString("d"));
                    if(jobj.has("a")) {
                        col = jobj.getString("a");

                        cal.set(year, month, day);
                        date = cal.getTime();

                        if (col.toLowerCase().equals("a")) {
                            color = android.R.color.holo_red_light;
                            cal_events[month][day] = "Absent";
                            if (cal.get(Calendar.YEAR) == year)
                                atr[month][day] = 1;

                        } else if (col.toLowerCase().equals("p")) {
                            color = android.R.color.holo_green_light;
                            cal_events[month][day] = "Present";
                            if (cal.get(Calendar.YEAR) == year)
                                atr[month][day] = 2;
                        }
                        else
                            color = android.R.color.white;


                        caldroidFragment.setBackgroundResourceForDate(color, date);
                    }
                    else if(jobj.has("h")){
                        col = jobj.getString("h");
                        cal_events[month][day] = col;
                        cal.set(year, month, day);
                        date = cal.getTime();
                        color = android.R.color.holo_blue_dark;
                        caldroidFragment.setBackgroundResourceForDate(color, date);
                        if (cal.get(Calendar.YEAR) == year)
                            atr[month][day] = 0;
                    }
                }


            } catch (Throwable t) {
                t.printStackTrace();
            }
            try {
                cal = Calendar.getInstance();
                int present = 0, absent = 0;
                int mon_cur = cal.get(Calendar.MONTH);
                for (int i = 0; i < 32; ++i)
                    if (atr[mon_cur][i] == 1)
                        ++absent;
                    else if (atr[mon_cur][i] == 2)
                        ++present;
                //Toast.makeText(LoginActivity.this, "Present = " + present + " days / Absent = " + absent + " days", Toast.LENGTH_SHORT).show();
                cl1.setText("Present = " + present + " days"+" / Absent = " + absent + " days");
            } catch (Exception e) {
            }
            caldroidFragment.refreshView();
            if (getApplication() != null)
                if (dialog != null)
                    if (dialog.isShowing())
                        dialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Downloading details..");
            dialog.setCancelable(false);
            if (getApplication() != null)
                if (dialog != null)
                    dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {


            byte[] b = null;
            try {

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(params[0]);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                JSONResp = EntityUtils.toString(httpResponse.getEntity());

                httpGet = new HttpGet(MyApplication.host_add+"school/holidays.json");
                httpResponse = httpClient.execute(httpGet);
                JSONResp += EntityUtils.toString(httpResponse.getEntity());


            } catch (MalformedURLException e) {
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }

            return "[" + JSONResp.substring(1) + "]";
        }
    }


    class CheckNet extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPostExecute(String s) {


            if (s.equals("true")) {

                logout();
            } else {
                Toast.makeText(LoginActivity.this, "No Internet !!", Toast.LENGTH_SHORT).show();
            }

            if (pd.isShowing())
                pd.dismiss();
            super.onPostExecute(s);
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Please Wait !");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String response = "false";

            Log.e(response, params[0]);
            try {


                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(params[0]);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                response = EntityUtils.toString(httpResponse.getEntity());
                response = "true";
            } catch (MalformedURLException e) {
                return "false";
            } catch (ClientProtocolException e) {
                return "false";
            } catch (IOException e) {
                return "false";
            }

            return response;
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


}