/**
 * CSE651 - Mobile Application Programming
 * Final Project - ChatNShare
 * Created by Nikhita Chandra/Nirav Gandhi/Sangeeta Gill on 22-Apr-15.
 *
 * File:ChatNShareLogin.java
 * Functionalities:
 * Defines login activity, takes user name as input and starts the main activity.
 *
 */
package syr.edu.chatnshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//Class for Login activity
public class ChatNShareLogin  extends Activity {

    //private variables
    private Button getLogin;
    private EditText getUserName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        setContentView(R.layout.chatnshare_login); //inflating Login window

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //adjusting Login screen with soft keyboard
        getLogin = (Button) findViewById(R.id.btnStart);

        //Button Click
        getLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                getUserName = (EditText) findViewById(R.id.loginUser);
                String loginName = getUserName.getText().toString(); //getting the user name entered by user

                if (loginName.equals(""))
                {
                    //Toast notification
                    Toast.makeText(getApplicationContext(),
                            "Please enter a Username to chat",
                            Toast.LENGTH_LONG).show();

                } else {

                    // Calling main activity with Intent
                    Intent myIntent = new Intent(ChatNShareLogin.this,
                            ChatNShareMainActivity.class);
                    myIntent.putExtra("Login_name", loginName);
                    startActivity(myIntent);
                }

            }

        });

    }




}
