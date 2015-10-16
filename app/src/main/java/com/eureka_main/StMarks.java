package com.eureka_main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
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


public class StMarks extends Fragment {

    public StMarks() {
        // Required empty public constructor
    }

    MaterialListView mListView;
    String[] msg, title, marks, fmarks;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.frag_marks, container, false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        double a = Math.random();

        listView = (ListView) rootView.findViewById(R.id.stmarks_listView);
        mDrawableBuilder = TextDrawable.builder()
                .rect();
        new getNotif().execute(MyApplication.host_add + "students/" + sp.getString(MyApplication.svreg, "") + "/marks.json?q=" + a);
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
            super.onPostExecute(s);
            String tnm;
            JSONArray ja;
            try {
                ja = new JSONArray(s);
                msg = new String[ja.length()];
                title = new String[ja.length()];
                marks = new String[ja.length()];
                fmarks = new String[ja.length()];
                int i;
                for (int k = 0; k < ja.length(); ++k) {
                    if (getActivity() != null) {
                        i = ja.length() - 1 - k;
                        title[i] = ja.getJSONObject(k).getString("sc");
                        marks[i] = title[i].split("/")[0].trim();
                        fmarks[i] = title[i].split("/")[1].trim();
                        tnm = (ja.getJSONObject(k).getString("nm"));
                        tnm = tnm + " (" + ja.getJSONObject(k).getString("d") + "/" + ja.getJSONObject(k).getString("m") + "/" + ja.getJSONObject(k).getString("y") + ")";
                        tnm += "\n Marks Obtained : " + marks[i];
                        tnm += "\n Full Marks : " + fmarks[i];
                        msg[i] = tnm;
                    }
                }


            } catch (JSONException e) {
                msg = new String[1];
                marks = new String[1];
                msg[0] = "No Data Available";
                marks[0] = "0";
            }

            listView.setAdapter(new SampleAdapter());
            if (getActivity() != null)
                if (pd != null)
                    if (pd.isShowing())
                        pd.dismiss();
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            if (getActivity() != null) {
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

            return "[" + response.substring(1) + "]";
        }
    }

    private class SampleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return msg.length;
        }

        @Override
        public String getItem(int position) {
            return msg[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.stmarks_list_item_layout, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            TextDrawable drawable;
            try {
                int rt = (int) (Integer.parseInt(marks[position]) * 100) / Integer.parseInt(fmarks[position]);
                drawable = mDrawableBuilder.build(rt + "", mColorGenerator.getColor(marks[position]));
            } catch (Exception e) {
                drawable = mDrawableBuilder.build("Ab", mColorGenerator.getColor("Absent"));
            }
            holder.imageView.setImageDrawable(drawable);
            holder.textView.setText(msg[position]);
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
