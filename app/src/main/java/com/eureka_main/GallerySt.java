package com.eureka_main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

public class GallerySt extends Fragment {
    private static final String TAG = "CardListActivity";


    int MAX_IMAGE_NUMBER = 15;
    static String xxx;
    final Intent startIntent = new Intent("com.eureka_main.ImageViewer");
    String[] title, text, urls, big_urls;
    Bitmap[] bmps;
    int limit = 0;

    public GallerySt() {

    }

    public String JSONResp = null;
    private Context cxt;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_marks, container, false);

        cxt = getActivity().getApplicationContext();

        listView = (ListView) rootView.findViewById(R.id.stmarks_listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (title != null)
                    if (!title[0].equals("Oops!")) {
                        xxx = big_urls[position];
                        startActivity(startIntent);
                    }

            }
        });
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String cls = sp.getString(MyApplication.svclass, "") + sp.getString(MyApplication.svsection, "");

        new AsyncListViewLoader().execute(MyApplication.host_add + "class/" + cls + "/gallery.json");

        return rootView;
    }


    private class AsyncListViewLoader extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog;
        private int resCode;

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            if (getActivity() != null && title == null) {

                title = new String[1];
                text = new String[1];
                bmps = new Bitmap[1];
                title[0] = "Oops!";
                text[0] = "No information here";
            }
            listView.setAdapter(new SampleAdapter());

            if (getActivity() != null)
                if (dialog != null)
                    if (dialog.isShowing())
                        dialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Downloading details..");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {


            byte[] b = null;
            if (true)
                try {
                    // defaultHttpClient
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    double a = Math.random();
                    HttpGet httpGet = new HttpGet(params[0] + "?qw=" + a);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    JSONResp = EntityUtils.toString(httpResponse.getEntity());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            JSONArray arr;
            JSONObject jobj;
            try {
                arr = new JSONArray("[" + JSONResp.substring(1) + "]");

                bmps = new Bitmap[arr.length()];
                title = new String[arr.length()];
                text = new String[arr.length()];
                urls = new String[arr.length()];
                big_urls = new String[arr.length()];
                int comp = arr.length() - 1;
                if (arr.length() > MAX_IMAGE_NUMBER)
                    comp = MAX_IMAGE_NUMBER - 1;
                for (int i = arr.length() - 1; i >= arr.length() - comp - 1; i--) {
                    jobj = arr.getJSONObject(i);
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    String cls = sp.getString(MyApplication.svclass, "") + sp.getString(MyApplication.svsection, "");
                    String url = MyApplication.host_add + "class/" + cls + "/" + jobj.getString("url");
                    big_urls[comp - i] = MyApplication.host_add + "class/" + cls + "/img" + jobj.getString("url").substring(5);
                    urls[comp - i] = url;
                    text[limit] = jobj.getString("text");
                    title[limit] = jobj.getString("title");
                    ++limit;
                    // new BitmapDrawable(getResources(),bitmap);

                }


            } catch (Throwable t) {


            }
            return JSONResp;
        }
    }

    private class SampleAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public String getItem(int position) {
            return title[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getActivity().getApplicationContext(), R.layout.stmarks_list_item_layout, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(title[position] + "\n" + text[position]);

            try {
                Picasso.with(getActivity().getApplication()).load(urls[position]).into(holder.imageView);
            } catch (Exception e) {
                holder.imageView.setImageResource(R.drawable.ic_launcher);
            }
            holder.view.setBackgroundColor(Color.TRANSPARENT);
            return convertView;
        }

    }

    private static class ViewHolder {

        private View view;

        private ImageView imageView;
        private TextView textView;

        private ViewHolder(View view) {
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.stmarks_imageView);
            textView = (TextView) view.findViewById(R.id.stmarks_textView);
        }
    }

}
