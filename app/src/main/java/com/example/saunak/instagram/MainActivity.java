package com.example.saunak.instagram;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnKeyListener {

    final String TAG = MainActivity.class.getSimpleName();
    RelativeLayout relativeLayout;
    EditText userName;
    EditText passWord;
    TextView changeLoginModeTextView;
    Button signUpButton;
    ImageView instaImageView;
    Boolean signUpModeActive = true;
    Intent userListIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = findViewById(R.id.userNameEditText);
        passWord = findViewById(R.id.passwordEditText);
        changeLoginModeTextView = findViewById(R.id.changeLoginModeTextView);
        signUpButton = findViewById(R.id.signUpButton);
        relativeLayout = findViewById(R.id.backgroundRelativeLayout);
        instaImageView = findViewById(R.id.instaImageView);


        changeLoginModeTextView.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
        instaImageView.setOnClickListener(this);

        passWord.setOnKeyListener(this);

        if(ParseUser.getCurrentUser() != null){
            Log.i(TAG,"Current User not null");
            redirectToUserList();
        }
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    public void signUp(View view) {
        if (userName.getText().toString().matches("") || passWord.getText().toString().matches("")){
            Toast.makeText(this, "UserName or PassWord Required", Toast.LENGTH_SHORT).show();
        }else{
            if(signUpModeActive){
            ParseUser user = new ParseUser();
            user.setUsername(userName.getText().toString());
            user.setPassword(passWord.getText().toString());
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        Log.i(TAG,"Sign Up Successful");
                        redirectToUserList();
                    }else {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
                ParseUser.logInInBackground(userName.getText().toString(), passWord.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null && user != null){
                            Log.i(TAG,"Login Successful");
                            redirectToUserList();
                        }else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.changeLoginModeTextView:
                if(signUpModeActive){
                    signUpModeActive = false;
                    signUpButton.setText("Login");
                    changeLoginModeTextView.setText("Sign Up");
                }else {
                    signUpModeActive = true;
                    signUpButton.setText("Sign Up");
                    changeLoginModeTextView.setText("Login");
                }
                break;

            case R.id.backgroundRelativeLayout:
                hideKeyBoard();
                break;
            case R.id.instaImageView:
                hideKeyBoard();
                break;
            default:
                Toast.makeText(this, "No match", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            signUp(view);
        }
        return false;
    }

    public void hideKeyBoard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }

    public void redirectToUserList(){
        userListIntent = new Intent(this,UserListActivity.class);
        startActivity(userListIntent);
    }
}
