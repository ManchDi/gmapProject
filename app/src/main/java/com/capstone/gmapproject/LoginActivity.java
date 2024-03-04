package com.capstone.gmapproject;

import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class LoginActivity extends AppCompatActivity {

    private int userID;
    private DbConnector dbConnect;
    private SQLiteDatabase db;
    private String username;

    private Button createAccount;
    private Button login;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        dbConnect = new DbConnector(this);

        //Button for switching from login to create account
        //transition to account creation page
        createAccount = (Button) findViewById(R.id.btnCreate);
        createAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CreateActivity.class));
            }
        });

        //Button for switching from login screen to main activity
        login = (Button) findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Check if user gave valid credentials
                boolean valid = authenticateLogin();
                if(!valid) {
                    //if credentials weren't valid, inform user
                    printFalseCredentials();
                } //if they were, log in
                else{
                    setUserID();
                    MainActivity.setUsername(username);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        });
    }

    //method to check if accurate username and password were given, returns true if so, false if not
    private boolean authenticateLogin()
    {
        db = dbConnect.getReadableDatabase();
        //get the username and password from their respective text fields
        EditText usernameText = (EditText) findViewById(R.id.username_text_input);
        username = usernameText.getText().toString();

        EditText passwordText = (EditText) findViewById(R.id.password_text_input);
        String password = passwordText.getText().toString();

        //compare username to the usernames in the database to see if a match is present
        String query = "SELECT * FROM user_info WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        if(!(cursor.getCount() > 0)) {
            db.close();
            return false;
        }
        cursor.close();

        query = "SELECT user_id FROM user_info WHERE username = ?";
        cursor = db.rawQuery(query, new String[]{username});
        cursor.moveToFirst();
        int tempUserID = cursor.getInt(0);
        cursor.close();

        //check password
        query = "SELECT password FROM user_cred WHERE user_id = ?";
        cursor = db.rawQuery(query, new String[]{String.valueOf(tempUserID)});
        if(!(cursor.getCount() > 0))
        {
            db.close();
            return false;
        }
        cursor.moveToFirst();
        String compare = cursor.getString(0);
        if(!compare.equals(password))
        {
            db.close();
            return false;
        }

        db.close();
        return true;
    }

    //set the static user ID variable to the logged in user's ID, for future use.
    private void setUserID() {
        String query = "SELECT user_id FROM user_info WHERE username = ?";

        db = dbConnect.getReadableDatabase();

        EditText usernameText = (EditText) findViewById(R.id.username_text_input);
        username = usernameText.getText().toString();

        Cursor cursor = db.rawQuery(query, new String[]{username});
        cursor.moveToFirst();
        userID = cursor.getInt(0);
        MainActivity.setUserID(userID);
        MainActivity.setLoggedIn(true);
        cursor.close();
        db.close();

    }

    private void printFalseCredentials()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Incorrect Login Credentials");
        alertDialogBuilder.setMessage("Your username or password was incorrect, please try again.");

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }

    private void printConfirmCreation()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Account Created");
        alertDialogBuilder.setMessage("You have successfully created an account, please go back to the main menu and log in.");

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}
