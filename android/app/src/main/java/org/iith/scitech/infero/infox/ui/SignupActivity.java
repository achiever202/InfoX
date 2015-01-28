package org.iith.scitech.infero.infox.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.util.HttpServerRequest;
import org.iith.scitech.infero.infox.util.PrefUtils;


public class SignupActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findViewById(R.id.button_signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name, phone, password;
                EditText editText = (EditText) findViewById(R.id.loginPhone);
                phone = editText.getText().toString();

                editText = (EditText) findViewById(R.id.loginPass);
                password = editText.getText().toString();

                editText = (EditText) findViewById(R.id.loginName);
                name = editText.getText().toString();

                if(phone.isEmpty() || password.isEmpty() || name.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please fill in the details to Sign up.", Toast.LENGTH_LONG).show();
                    return;
                }

                String reply = new HttpServerRequest(getApplicationContext).getReply("InfoX/register.php", "name", name, "phone", phone, "password", password);
                Toast.makeText(getApplicationContext(), reply, Toast.LENGTH_LONG).show();

                if(reply.equals("Success!"))
                {
                    PrefUtils.setLoginStatus(getApplicationContext(), true);
                    PrefUtils.setPhoneNumber(getApplicationContext(), phone);
                    PrefUtils.setName(getApplicationContext(), name);
                    Intent intent = new Intent(SignupActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone, password;
                EditText editText = (EditText) findViewById(R.id.loginPhone);
                phone = editText.getText().toString();

                editText = (EditText) findViewById(R.id.loginPass);
                password = editText.getText().toString();

                if(phone.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please fill in the details to Log In.", Toast.LENGTH_LONG).show();
                    return;
                }

                String reply = new HttpServerRequest(SignupActivity.this).getReply("InfoX/login.php", "phone", phone, "password", password);
                Toast.makeText(getApplicationContext(), reply, Toast.LENGTH_LONG).show();

                if(reply.equals("Success!"))
                {
                    PrefUtils.setLoginStatus(getApplicationContext(), true);
                    PrefUtils.setPhoneNumber(getApplicationContext(), phone);
                    Intent intent = new Intent(SignupActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private class ValidateData extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog mProgressDialog = new ProgressDialog(SignupActivity.this);

        @Override
        protected void onPreExecute() {
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Perform validation here
            try
            {
                Thread.sleep(4000);
            }
            catch (InterruptedException e) {
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean val) {
            // Do some UI related stuff here
            mProgressDialog.dismiss();
            super.onPostExecute(val);
        }
    }

}
