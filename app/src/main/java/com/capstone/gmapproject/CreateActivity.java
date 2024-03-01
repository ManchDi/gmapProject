package com.capstone.gmapproject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class CreateActivity extends AppCompatActivity {

    private DbConnector dbConnect;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_screen);
        dbConnect = new DbConnector(this);
        //Button for switching from create account back to login
        Button back = (Button) findViewById(R.id.btnBack);

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //back to login screen
                startActivity(new Intent(CreateActivity.this, LoginActivity.class));
            }
        });

        Button createAccount = (Button) findViewById(R.id.btnCreate);
        createAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount()
    {
        db = dbConnect.getReadableDatabase();
        //first, check if that username is currently in use, then if not, create the account
        EditText usernameText = (EditText) findViewById(R.id.createUsername_text_input);
        String username = usernameText.getText().toString();

        EditText passwordText = (EditText) findViewById(R.id.createPassword_text_input);
        String password = passwordText.getText().toString();

        boolean valid = authenticateInfo(username, password);
        if (valid == false) printUsernameTakenError();
        else printConfirmCreation();

        db.close();
    }

    private boolean authenticateInfo(String username, String password)
    {
        db = dbConnect.getWritableDatabase();

        //query database to see if same username exists
        String query = "SELECT * FROM user_info WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        int error = cursor.getCount();
        cursor.close();
        //if it exists, they cannot create that account
        if(error > 0)
        {
            db.close();
            return false;
        }
        //if not, make the new account
        else
        {

            ContentValues usernameValues = new ContentValues();
            usernameValues.put("username", username);


            long result;

            result = db.insert("user_info", null, usernameValues);
            if(result == -1) printConfirmError();

            ContentValues passwordValues = new ContentValues();
            passwordValues.put("password", password);
            result = db.insert("user_cred", null, passwordValues);
            db.close();

            if(result == -1) return false;
            else return true;
        }
    }

    private void printUsernameTakenError()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Invalid Credentials");
        alertDialogBuilder.setMessage("This username already exists, try again.");

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

    private void printConfirmError()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Account Not Created");
        alertDialogBuilder.setMessage("There was an error creating your account, please try again.");

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

}
