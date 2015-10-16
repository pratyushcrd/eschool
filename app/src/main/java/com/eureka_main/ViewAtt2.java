package com.eureka_main;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Pratyush on 10-04-2015.
 */
public class ViewAtt2 extends ActionBarActivity{

    Calendar cal;
    CaldroidFragment caldroidFragment;
    Date date;
    String JSONResp;
    TextView cl1,cl2;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_att2);

        cl1 = (TextView) findViewById(R.id.cal2_txt1);
        cl2 = (TextView) findViewById(R.id.cal2_txt2);

        calendar();
        color_change();

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        if (toolbar != null) {
            toolbar.setTitle("Yo");
            setSupportActionBar(toolbar);
        }

        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Attendance of "+ClassList3.current_st_nm);
    }
    public void color_change() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
            window.setNavigationBarColor(getResources().getColor(R.color.primary_dark));

        }
    }
    public void calendar() {
        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.content_frame2, caldroidFragment);
        t.commit();

        new GetCalInfo().execute(MyApplication.host_add + "/students/" + ClassList3.current_st_reg + "/att.json?q=" + Math.random());


    }
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
            try {
                arr = new JSONArray(s);
                resCode = 201;


                int[][] atr = new int[13][32];
                for (int i = 0; i < 12; ++i)
                    for (int j = 0; j < 31; ++j) {
                        atr[i][j] = 0;
                    }

                for (int i = 0; i < arr.length(); i++) {
                    jobj = arr.getJSONObject(i);


                    year = Integer.parseInt(jobj.getString("y"));
                    month = Integer.parseInt(jobj.getString("m"));
                    --month;
                    day = Integer.parseInt(jobj.getString("d"));
                    col = jobj.getString("a");

                    cal.set(year, month, day);
                    date = cal.getTime();

                    if (col.toLowerCase().equals("a")) {
                        color = android.R.color.holo_red_light;
                        if(cal.get(Calendar.MONTH)==month)
                            atr[month][day] = 1;

                    } else if (col.toLowerCase().equals("p")) {
                        color = android.R.color.holo_green_light;
                        if(cal.get(Calendar.MONTH)==month)
                            atr[month][day] = 2;

                    } else {
                        color = android.R.color.white;
                    }



                    caldroidFragment.setBackgroundResourceForDate(color, date);
                }

                cal = Calendar.getInstance();
                int present = 0,absent =0;
                int mon_cur = cal.get(Calendar.MONTH);
                for(int i=0;i<32;++i)
                    if(atr[mon_cur][i]==1)
                        ++absent;
                    else if(atr[mon_cur][i]==2)
                        ++present;

                String month_current = new SimpleDateFormat("MMMM").format(cal.getTime());
                cl1.setText("Present : "+present + "\n(" + month_current + ")");
                cl2.setText("Absent : "+absent + "\n(" + month_current + ")");


                cal.add(Calendar.MONTH ,-1);
                present = 0;
                absent =0;
                mon_cur = cal.get(Calendar.MONTH);
                for(int i=0;i<32;++i)
                    if(atr[mon_cur][i]==1)
                        ++absent;
                    else if(atr[mon_cur][i]==2)
                        ++present;

                month_current = new SimpleDateFormat("MMMM").format(cal.getTime());
                cl1.append("\nPresent : "+present + "\n(" + month_current + ")");
                cl2.append("\nAbsent : "+absent + "\n(" + month_current + ")");


            } catch (Throwable t) {
            }

            caldroidFragment.refreshView();
            if (dialog != null)
                if (dialog.isShowing())
                    dialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ViewAtt2.this);
            dialog.setMessage("Downloading details..");
            if (dialog != null)
                dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {


            byte[] b = null;
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(params[0]);

                HttpResponse httpResponse = httpClient.execute(httpGet);


                JSONResp = EntityUtils.toString(httpResponse.getEntity());
            } catch (MalformedURLException e) {
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }

            return "["+JSONResp.substring(1)+"]";
        }
    }

}
