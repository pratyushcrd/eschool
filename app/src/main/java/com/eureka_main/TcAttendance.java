package com.eureka_main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;


public class TcAttendance extends Fragment implements AdapterView.OnItemClickListener {

    String[] class_list,section_list,clsc;
    public TcAttendance() {
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
        new CheckPass().execute(MyApplication.host_add+"teachers/"+sp.getString(MyApplication.svemail,"")+"/class.json");


        lv.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(),clsc[position],Toast.LENGTH_LONG).show();


        MyApplication.attcurrent_section = section_list[position];
        MyApplication.attcurrent_class = class_list[position];
        Intent startIntent = new Intent(getActivity().getApplicationContext(), ClassList.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().getApplicationContext().startActivity(startIntent);

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
                for(int i = 0;i<ja.length();++i){

                    class_list[i] = ja.getJSONObject(i).getString("c");
                    section_list[i] = ja.getJSONObject(i).getString("s");
                    clsc[i] = class_list[i]+section_list[i];
                    if(getActivity()!=null) {
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

}