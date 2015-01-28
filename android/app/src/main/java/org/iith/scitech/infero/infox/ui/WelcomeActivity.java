package org.iith.scitech.infero.infox.ui;

import android.content.Intent;
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
import org.iith.scitech.infero.infox.util.PrefUtils;

import java.security.Permission;


public class WelcomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Log.v("DEBUG: ", Environment.getExternalStorageDirectory().toString());
        Log.v("DEBUG: ", Environment.getExternalStoragePublicDirectory("InfoX/").getAbsolutePath());
        Log.v("DEBUG: ", PrefUtils.getDownloadDirectory(WelcomeActivity.this));

        findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println(findViewById(R.id.loginPhone).toString());

                EditText mPhoneNo = (EditText) findViewById(R.id.loginPhone);
                EditText mPass = (EditText) findViewById(R.id.loginPass);

                //Log.v("Hello", mPhoneNo.getText().toString());
                //Log.v("Hello", mPass.getText().toString());

                if(PrefUtils.getPhoneNumber(WelcomeActivity.this).equals(mPhoneNo.getText().toString()) &&
                   PrefUtils.getLoginPassword(WelcomeActivity.this).equals(mPass.getText().toString()))
                {
                    Intent intent = new Intent(WelcomeActivity.this, BrowseActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(WelcomeActivity.this, "Authentication Error", Toast.LENGTH_SHORT).show();
                }
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

}
