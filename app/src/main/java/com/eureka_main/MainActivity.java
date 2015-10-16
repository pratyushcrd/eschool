package com.eureka_main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pushbots.push.Pushbots;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends ActionBarActivity {

    Button b1;
    EditText e1, e2;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    Boolean boo_login;
    CheckBox cb1;

    static Boolean stSplash = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Pushbots.sharedInstance().init(this);
        setContentView(R.layout.activity_main);

        init();
        listeners();
        boo_login = false;
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ed = sp.edit();

        boo_login = sp.getBoolean(MyApplication.userLoggedIn_key, false);

        if (boo_login) {
            Intent i;
            if(sp.getBoolean(MyApplication.svstud,true))
            i = new Intent("com.eureka_main.LoginActivity");
            else
                i = new Intent("com.eureka_main.LoginActivity2");
            startActivity(i);
            Pushbots.sharedInstance().setCustomHandler(CustomHandler.class);

            if(stSplash){
                Intent x = new Intent(this,Splash.class);
                x.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(x);
                stSplash = false;
            }
            finish();
        }else{

            if(stSplash){
                Intent x = new Intent(this,Splash.class);
                x.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(x);
                stSplash = false;
            }
        }

        Pushbots.sharedInstance().setCustomHandler(CustomHandler.class);

    }
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String sha1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
    private void listeners() {
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new CheckPass().execute("http://phdemo.pe.hu/school/index.php");


                String pass = e2.getText().toString();
                pass = "QxLUF1bgIAdeQX" + pass;
                String saltpass = null;
                try {
                    saltpass = sha1(pass);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.e("salt",saltpass);

                if (!cb1.isChecked()) {
                    new CheckPass().execute(MyApplication.host_add_stud + "stid=" + e1.getText().toString() + "&pass=" + saltpass);

                }else
                    new CheckPass().execute(MyApplication.host_add_teacher + "email=" + e1.getText().toString() + "&pass=" + saltpass);
            }


        });
    }

    public void loginSuccess(String s) {


        try {
            JSONObject jso = new JSONArray(s).getJSONObject(0);
            if(!cb1.isChecked()) {
                ed.putString(MyApplication.svname, jso.getString("name"));
                ed.putString(MyApplication.svclass, jso.getString("class"));
                ed.putString(MyApplication.svsection, jso.getString("section"));
                ed.putString(MyApplication.svdoa, jso.getString("doa"));
                ed.putString(MyApplication.svdob, jso.getString("dob"));
                ed.putString(MyApplication.svemail, jso.getString("email"));
                ed.putString(MyApplication.svfather, jso.getString("father"));
                ed.putString(MyApplication.svlocation, jso.getString("location"));
                ed.putString(MyApplication.svmother, jso.getString("mother"));
                ed.putString(MyApplication.svphone, jso.getString("phone"));
                ed.putString(MyApplication.svreg, jso.getString("registration"));
                ed.putString(MyApplication.svroll, jso.getString("roll"));
                ed.putBoolean(MyApplication.svstud,true);




            }else{

                ed.putString(MyApplication.svname, jso.getString("name"));
                ed.putString(MyApplication.svemail, jso.getString("email"));
                ed.putString(MyApplication.svdes, jso.getString("des"));
                ed.putBoolean(MyApplication.svstud,false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Toast.makeText(this, "Login Successful! " + e1.getText().toString(), Toast.LENGTH_SHORT).show();
        ed.putBoolean(MyApplication.userLoggedIn_key, true);
        ed.putString(MyApplication.username_key, e1.getText().toString());


        ed.commit();
        finish();
        Intent i;
        if(cb1.isChecked())
        i = new Intent("com.eureka_main.LoginActivity2");
        else
        i = new Intent("com.eureka_main.LoginActivity");
        startActivity(i);
    }

    public void loginFailure() {
        Toast.makeText(this, "Login Failure!", Toast.LENGTH_SHORT).show();
    }

    public void init() {
        b1 = (Button) findViewById(R.id.main_bt1);
        e1 = (EditText) findViewById(R.id.main_et1);
        e2 = (EditText) findViewById(R.id.main_et2);
        cb1 = (CheckBox) findViewById(R.id.main_ck1);
    }

    class CheckPass extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPostExecute(String s) {


            JSONObject js = null;
            try {
                js = new JSONArray(s).getJSONObject(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (js != null) {
                    loginSuccess(s);
                } else {
                    loginFailure();
                }
            } catch (StringIndexOutOfBoundsException e) {
                loginFailure();
            }

            if(pd!=null)
                if(pd.isShowing())
            pd.dismiss();
            super.onPostExecute(s);
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Logging in!");
            if(pd!=null)
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Pushbots.sharedInstance().unRegister();
            Pushbots.sharedInstance().setNotificationEnabled(false);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String response = "false";
            try {


                DefaultHttpClient httpClient = new DefaultHttpClient();

                HttpGet httpGet = new HttpGet(params[0]);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                response = EntityUtils.toString(httpResponse.getEntity());
            } catch ( Exception e) {
                return "false";
            }

            Log.e("Login",response);
            return response;
        }
    }



}
