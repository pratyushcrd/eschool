package com.eureka_main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.view.MaterialListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;


public class StClNotif extends Fragment {

    public StClNotif() {
        // Required empty public constructor
    }
    MaterialListView mListView;
    String[] msg, title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_st_cl_notif, container, false);
        mListView = (MaterialListView) rootView.findViewById(R.id.material_listview);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String cls = sp.getString(MyApplication.svclass, "") + sp.getString(MyApplication.svsection, "");
        double a = Math.random();
        new getNotif().execute(MyApplication.host_add + "class/" + cls + "/notif.json?q="+a);
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


    }


    class getNotif extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPostExecute(String s) {

            if(s.charAt(0)!='['&&getActivity()!=null){
                SmallImageCard card = new SmallImageCard(getActivity());
                card.setTitle("Sorry");
                card.setDescription("No Internet Connection!!");
                card.setBackgroundColor(Color.parseColor("#fefefe"));
                card.setTitleColor(getResources().getColor(R.color.accent));
                card.setDescriptionColor(getResources().getColor(R.color.secondary_text));
                mListView.add(card);

            }else {
                JSONArray ja;
                try {
                    ja = new JSONArray(s);
                    msg = new String[ja.length()];


                    for (int i = ja.length() - 1; i >= 0; --i) {

                        if (getActivity() != null) {
                            SmallImageCard card = new SmallImageCard(getActivity());
                            card.setTitle(ja.getJSONObject(i).getString("t"));
                            card.setDescription(ja.getJSONObject(i).getString("m"));
                            card.setBackgroundColor(Color.parseColor("#fefefe"));
                            card.setTitleColor(getResources().getColor(R.color.accent));
                            card.setDescriptionColor(getResources().getColor(R.color.secondary_text));
                            mListView.add(card);

                        }


                    }


                } catch (JSONException e) {
                    SmallImageCard card = new SmallImageCard(getActivity());
                    card.setTitle("Sorry");
                    card.setDescription("No Information Available!!");
                    card.setBackgroundColor(Color.parseColor("#fefefe"));
                    card.setTitleColor(getResources().getColor(R.color.accent));
                    card.setDescriptionColor(getResources().getColor(R.color.secondary_text));
                    mListView.add(card);
                }
            }
            pd.dismiss();
            super.onPostExecute(s);
        }

        @Override
        protected void onPreExecute() {
            if(getActivity()!=null) {
                pd = new ProgressDialog(getActivity());
                pd.setMessage("Working..");
                pd.show();
                pd.setCancelable(false);
            }
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
            return "["+response.substring(1)+"]";
        }
    }

}
