package com.eureka_main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class ImageViewer extends Activity {

    ScaleImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        setTitle("");
        getActionBar().hide();
        iv = (ScaleImageView) findViewById(R.id.img_viewer);
        new LoadImg().execute();
    }

    class LoadImg extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ImageViewer.this);
            pd.setMessage("Downloading..");
            pd.setCancelable(false);
            if (getApplication() != null)
                if (pd != null)
                    pd.show();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iv.setImageBitmap(bitmap);
            super.onPostExecute(bitmap);
            if (getApplication() != null)
                if (pd != null)
                    if (pd.isShowing())
                        pd.dismiss();
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bmp = null;
            try {
                InputStream is = (InputStream) new URL(GallerySt.xxx).getContent();
                bmp = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bmp;
        }
    }
}
