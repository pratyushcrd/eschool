package com.eureka_main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class Upload extends Fragment implements AdapterView.OnItemClickListener {

    String[] class_list, section_list, clsc;
    private static int RESULT_LOAD_IMG = 1;
    String imgPath, fileName;
    String title;

    public Upload() {
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
        new CheckPass().execute(MyApplication.host_add + "teachers/" + sp.getString(MyApplication.svemail, "") + "/class.json");


        lv.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        MyApplication.attcurrent_class = class_list[position];
        MyApplication.attcurrent_section = section_list[position];
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        try {
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "No Photo Viewer Installed", Toast.LENGTH_SHORT).show();
        }

    }


    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        try {
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "No Photo Viewer Installed", Toast.LENGTH_SHORT).show();
        }
    }

    // When Image is selected from Gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                cursor.close();


                // Get the Image's file name
                String fileNameSegments[] = imgPath.split("/");
                fileName = fileNameSegments[fileNameSegments.length - 1];

                // Put file name in Async Http Post Param which will used in Php web app
                encodeImagetoString();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Something went wrong" + e.toString(), Toast.LENGTH_LONG)
                    .show();
        }

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

    Bitmap bitmap, bitmap2;
    ProgressDialog prgDialog;
    String encodedString, encodedString2;

    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            }


            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();


                if (imgPath.contains(".jpg") || imgPath.contains(".jpeg"))
                    options.inSampleSize = 5;
                else if (imgPath.contains(".png"))
                    options.inSampleSize = 1;
                else return "error";
                bitmap = BitmapFactory.decodeFile(imgPath,
                        options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy

                if (imgPath.contains(".png"))
                    bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
                else
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] byte_arr = stream.toByteArray();
                encodedString = Base64.encodeToString(byte_arr, 0);


                options = new BitmapFactory.Options();
                if (imgPath.contains(".png"))
                    options.inSampleSize = 4;
                else
                    options.inSampleSize = 15;
                bitmap2 = BitmapFactory.decodeFile(imgPath,
                        options);
                bitmap2 = Bitmap.createScaledBitmap(bitmap2, 100, 100, false);
                stream = new ByteArrayOutputStream();
                if (imgPath.contains(".png"))
                    bitmap2.compress(Bitmap.CompressFormat.PNG, 30, stream);
                else
                    bitmap2.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                byte_arr = stream.toByteArray();
                encodedString2 = Base64.encodeToString(byte_arr, 0);

                return "";
            }


            @Override
            protected void onPostExecute(String msg) {
                // Put converted Image string into Async Http Post param
                // Trigger Image upload
                if (msg.contains("error")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Invalid Image!!", Toast.LENGTH_SHORT).show();
                } else {

                    LayoutInflater li = LayoutInflater.from(getActivity());
                    ScrollView someLayout = (ScrollView) li.inflate(R.layout.upload_dialog, null);

                    ImageView iv = (ImageView) someLayout.findViewById(R.id.upload_dialog_iv1);
                    iv.setImageBitmap(bitmap);
                    final EditText input = (EditText) someLayout.findViewById(R.id.upload_dialog_et1);
                    input.setTextColor(getResources().getColor(android.R.color.black));
                    final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Enter a title for upload : ");
                    alert.setMessage("");
                    alert.setView(someLayout);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            title = input.getText().toString();
                            if (title.length() < 2) {
                                Toast.makeText(getActivity().getApplicationContext(), "Failed !! Not a proper title", Toast.LENGTH_SHORT).show();
                            } else
                                new PostData().execute("");
                        }


                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    alert.create().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                    alert.show();
                }
            }
        }
                .execute(null, null, null);

    }


    ProgressDialog pd;

    @Override
    public void onPause() {
        if (pd != null)
            if (pd.isShowing())
                pd.dismiss();

        super.onPause();
    }

    private class PostData extends AsyncTask<String, Integer, String> {

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

            pd.hide();
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Working...");
            pd.show();
        }

        public String postData(String val) {

            // Create a new HttpClient and Post Header
            HttpClient httpclient = null;
            httpclient = new DefaultHttpClient();
            HttpPost httppost = null;
            httppost = new HttpPost(MyApplication.host_add + "/img.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("image", encodedString));
                nameValuePairs.add(new BasicNameValuePair("thumb", encodedString2));
                nameValuePairs.add(new BasicNameValuePair("class", MyApplication.attcurrent_class + MyApplication.attcurrent_section));
                String name = sp.getString(MyApplication.svname, "");
                nameValuePairs.add(new BasicNameValuePair("teacher", name));
                nameValuePairs.add(new BasicNameValuePair("title", title));
                nameValuePairs.add(new BasicNameValuePair("filename", fileName));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpclient.execute(httppost);
                String response = EntityUtils.toString(httpResponse.getEntity());

                Log.e("response", response);
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