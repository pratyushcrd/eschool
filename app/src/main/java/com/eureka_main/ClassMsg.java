package com.eureka_main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ClassMsg extends Fragment implements AdapterView.OnItemClickListener {

    String[] class_list, section_list, clsc;
    int clk_pos;

    public ClassMsg() {
    }

    Context cxt;
    SharedPreferences sp;
    ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tcatt, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        cxt = getActivity();
        lv = (ListView) rootView.findViewById(R.id.tcatt_lv1);
        double a = Math.random();
        new CheckPass().execute(MyApplication.host_add + "teachers/" + sp.getString(MyApplication.svemail, "") + "/class.json");


        lv.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        clk_pos = position;

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Posting to class " + clsc[position]);
        alert.setMessage("Message:");
        final EditText input = new EditText(getActivity().getApplicationContext());
        input.setTextColor(getResources().getColor(android.R.color.black));
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                String name = sp.getString(MyApplication.svname, "");
                JSONObject job = new JSONObject();
                try {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
                    job.put("m",value);
                    job.put("t", name+" ("+sdf.format(cal.getTime())+")");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new PostData().execute(job.toString());

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }


    class CheckPass extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPostExecute(String s) {

            JSONArray ja;
            try {
                ja = new JSONArray(s);
                class_list = new String[ja.length()];
                section_list = new String[ja.length()];
                clsc = new String[ja.length()];
                for (int i = 0; i < ja.length(); ++i) {

                    class_list[i] = ja.getJSONObject(i).getString("c");
                    section_list[i] = ja.getJSONObject(i).getString("s");
                    clsc[i] = class_list[i] + section_list[i];
                    if (getActivity() != null) {
                        ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(), R.layout.textview3, clsc);
                        lv.setAdapter(aa);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pd.dismiss();
            super.onPostExecute(s);
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(cxt);
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
                Toast.makeText(getActivity().getApplicationContext(), "Success!!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getActivity().getApplicationContext(), "Failure!!", Toast.LENGTH_LONG).show();

            if (pd.isShowing())
                pd.hide();
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Working...");
            if (getActivity() != null)
                pd.show();
        }

        public String postData(String val) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(MyApplication.host_add + "classNotif.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("data", val));
                nameValuePairs.add(new BasicNameValuePair("class", clsc[clk_pos]));

                Log.e("data", val);
                Log.e("class", clsc[clk_pos]);
                Log.e("address", MyApplication.host_add + "classNotif.php");


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


}