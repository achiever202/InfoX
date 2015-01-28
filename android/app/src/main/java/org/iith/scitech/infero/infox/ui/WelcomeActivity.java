package org.iith.scitech.infero.infox.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.util.HttpServerRequest;
import org.iith.scitech.infero.infox.util.PrefUtils;

import java.security.Permission;


public class WelcomeActivity extends ActionBarActivity {


    String phone, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Log.v("DEBUG: ", Environment.getExternalStorageDirectory().toString());
        //Log.v("DEBUG: ", Environment.getExternalStoragePublicDirectory("InfoX/").getAbsolutePath());
        //Log.v("DEBUG: ", PrefUtils.getDownloadDirectory(WelcomeActivity.this));

        if(PrefUtils.getLoginStatus(WelcomeActivity.this)==true)
        {
            Intent intent = new Intent(WelcomeActivity.this, BrowseActivity.class);
            startActivity(intent);
            finish();
        }

        findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println(findViewById(R.id.loginPhone).toString());

                EditText editText = (EditText) findViewById(R.id.loginPhone);
                phone = editText.getText().toString();

                editText = (EditText) findViewById(R.id.loginPass);
                password = editText.getText().toString();

                if(phone.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please fill in the details to Log In.", Toast.LENGTH_LONG).show();
                    return;
                }

                new ValidateDataTask().execute();
            }
        });

        findViewById(R.id.button_signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


        private class ValidateDataTask extends AsyncTask<Void, Void, String> {
            ProgressDialog mProgressDialog = new ProgressDialog(WelcomeActivity.this);
            @Override
            protected void onPreExecute() {
                mProgressDialog.setTitle("Login Task");
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                // Perform validation here
                String reply = new HttpServerRequest(WelcomeActivity.this).getReply("login.php", "phone", phone, "password", password);
                return reply;
            }

            @Override
            protected void onPostExecute(String  reply) {
                Toast.makeText(getApplicationContext(), reply, Toast.LENGTH_LONG).show();

                if(reply.equals("Success!")) {
                    PrefUtils.setLoginStatus(getApplicationContext(), true);
                    PrefUtils.setPhoneNumber(getApplicationContext(), phone);

                    if (PrefUtils.getPhoneNumber(WelcomeActivity.this).equals(phone) && PrefUtils.getLoginPassword(WelcomeActivity.this).equals(password)) {
                        Intent intent = new Intent(WelcomeActivity.this, BrowseActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(WelcomeActivity.this, "Authentication Error", Toast.LENGTH_SHORT).show();
                    }
                }
                // Do some UI related stuff here
                mProgressDialog.dismiss();
                super.onPostExecute(reply);
            }
        }

}
