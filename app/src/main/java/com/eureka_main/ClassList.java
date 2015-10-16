package com.eureka_main;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Pratyush on 18-03-2015.
 */
public class ClassList extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    final String color_green = "#5cda90";
    final String color_red = "#f1998f";

    Boolean[] attRec;
    Toolbar toolbar;
    String[] st_list, roll_list, cl_list, sec_list, st_comb, stid_list, st_reg;
    ListView lv;
    Button b1;

    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classlist);
        toolbar = (Toolbar) findViewById(R.id.clist_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Attendance of " + MyApplication.attcurrent_class + MyApplication.attcurrent_section);
            setSupportActionBar(toolbar);
        }
        lv = (ListView) findViewById(R.id.clist_lv1);
        lv.setOnItemClickListener(this);
        b1 = (Button) findViewById(R.id.clist_bt1);
        b1.setOnClickListener(this);


        new CheckPass().execute(MyApplication.host_add + "student.php?class=" + MyApplication.attcurrent_class + "&section=" + MyApplication.attcurrent_section);

        color_change();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        view.setBackgroundColor(Color.parseColor("#ee3344"));

        attRec[position] = !attRec[position];

        if (attRec[position]) {
            view.setBackgroundColor(getResources().getColor(R.color.accent));
        } else {
            view.setBackgroundColor(getResources().getColor(R.color.accent2));

        }

    }

    @Override
    public void onClick(View v) {


        showDialog(999);


    }


    class CheckPass extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPostExecute(String s) {

            JSONArray ja;
            try {
                ja = new JSONArray(s);
                cl_list = new String[ja.length()];
                sec_list = new String[ja.length()];
                st_list = new String[ja.length()];
                stid_list = new String[ja.length()];
                roll_list = new String[ja.length()];
                attRec = new Boolean[ja.length()];
                st_comb = new String[ja.length()];
                st_reg = new String[ja.length()];
                for (int i = 0; i < ja.length(); ++i) {
                    attRec[i] = false;
                    stid_list[i] = ja.getJSONObject(i).getString("stid");
                    st_list[i] = ja.getJSONObject(i).getString("name");
                    roll_list[i] = ja.getJSONObject(i).getString("roll");
                    cl_list[i] = ja.getJSONObject(i).getString("class");
                    sec_list[i] = ja.getJSONObject(i).getString("section");
                    st_reg[i] = ja.getJSONObject(i).getString("registration");
                    st_comb[i] = roll_list[i] + "   " + st_list[i] + " " + st_reg[i];
                }

                ArrayAdapter<String> aa = new ArrayAdapter<String>(getApplicationContext(), R.layout.textview, st_comb);
                lv.setAdapter(aa);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            pd.dismiss();
            super.onPostExecute(s);
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(ClassList.this);
            pd.setMessage("Logging in!");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String response = "false";
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(params[0]);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                response = EntityUtils.toString(httpResponse.getEntity());
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


    private class PostData extends AsyncTask<String, Integer, String> {
        ProgressDialog pd;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            return postData(params[0]);

        }

        protected void onPostExecute(String result) {

            if (result.equals("success"))
                Toast.makeText(getApplicationContext(), "Success!!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Failure!!", Toast.LENGTH_LONG).show();

            pd.hide();
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ClassList.this);
            pd.setMessage("Working...");
            if (getApplication() != null)
                if (pd != null)
                    pd.show();
        }

        public String postData(String val) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MyApplication.host_add + "receive_att.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("data", val));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpclient.execute(httppost);
                String response = EntityUtils.toString(httpResponse.getEntity());
                Log.e("PHP", response);

                return response;

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return val;
        }

    }


    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {

            Calendar calendar;
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener
            = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        //dateView.setText(new StringBuilder().append(day).append("/")
        //      .append(month).append("/").append(year));
        Toast.makeText(this, year + " " + month + " " + day, Toast.LENGTH_SHORT).show();

        MyApplication.sel_d = day;
        MyApplication.sel_m = month;
        MyApplication.sel_y = year;

        final AlertDialog.Builder alert = new AlertDialog.Builder(ClassList.this
        );

        alert.setTitle("Post Attendance ?");
        alert.setMessage("For Date : " + year + "/" + month + "/" + day);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                JSONArray jarr = new JSONArray();
                JSONObject jobj;
                int i;

                for (i = 0; i < st_list.length; ++i) {

                    jobj = new JSONObject();
                    try {
                        jobj.put("reg", st_reg[i]);
                        jobj.put("att", attRec[i] ? "p" : "a");
                        jobj.put("y", MyApplication.sel_y);
                        jobj.put("m", MyApplication.sel_m);
                        jobj.put("d", MyApplication.sel_d);
                        jobj.put("c", cl_list[i] + sec_list[i]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jarr.put(jobj);

                }

                dialog.dismiss();
                new PostData().execute(jarr.toString());
                Log.e("JSON_DATA", jarr.toString());

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();


    }


}
