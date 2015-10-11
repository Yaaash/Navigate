package com.example.navigate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.login_button)
    LoginButton fbLoginButton;

    @Bind(R.id.cannotLogin)
    Button cannotLogin;

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());

        //setting layout
        setContentView(R.layout.activity_login);

        // binding id's to view
        ButterKnife.bind(this);

        // facebook login button
        fbLoginButton.setReadPermissions("user_friends", "public_profile");

        // initializing callback function
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess (LoginResult loginResult) {
                // App code
//                getUserDetail();
//                Intent goToNavigation = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
            }

            @Override
            public void onCancel () {
                Snackbar.make(fbLoginButton, "Can not login using Facebook, Please try logging again or Enter application without logging in", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onError (FacebookException exception) {
                Snackbar.make(fbLoginButton, "Some Error occurred! Login again", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.cannotLogin)
    public void noLogin () {
        Intent navigationIntent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
        startActivity(navigationIntent);
    }
}
