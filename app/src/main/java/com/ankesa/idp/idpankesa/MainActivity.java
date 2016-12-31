package com.ankesa.idp.idpankesa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.fabtransitionactivity.SheetLayout;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SheetLayout.OnFabAnimationEndListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE = 1;
    GoogleSignInAccount account;
    private SheetLayout mSheetLayout;


    //google api client
    public GoogleApiClient mGoogleApiClient;
    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;
    private boolean logInStatus = false;
    GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        mSheetLayout = (SheetLayout) findViewById(R.id.bottom_sheet);
        mSheetLayout.setFab(fab);
        mSheetLayout.setFabAnimationEndListener(this);

        GoogleLoginInBackground logIn = new GoogleLoginInBackground();
        logIn.mA = this;
        logIn.execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.aboutInfo) {
            startActivity(new Intent(MainActivity.this, RrethNesh.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFabAnimationEnd() {
        Intent ankesa = new Intent(MainActivity.this, BlankActivity.class);
        if (logInStatus) {
            Log.d("logSts", "Login status: true");
            ankesa.putExtra("NAME", account.getDisplayName());
            ankesa.putExtra("EMAIL", account.getEmail());
        }else{
            Log.d("logSts", "Login status: false");
        }

        startActivityForResult(ankesa, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            mSheetLayout.contractFab();
        } else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
    }

    //This function will option signing intent
    public void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            account = result.getSignInAccount();

            //Displaying name and email
//            textViewName.setText(acct.getDisplayName());
//            textViewEmail.setText(acct.getEmail());
            logInStatus = true;
        } else {
            //If login fails
            logInStatus = false;
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        ConnectivityState connectivityState = new ConnectivityState(this);
        if(connectivityState.isConnected())
            mSheetLayout.expandFab();
        else
            Toast.makeText(getApplicationContext(), "Duhet të jeni i lidhur në internet për të bërë një ankesë.", Toast.LENGTH_LONG).show();

    }


}

class GoogleLoginInBackground extends AsyncTask<Void, Void, Boolean>{

    MainActivity mA;

    GoogleLoginInBackground(){}

    @Override
    protected Boolean doInBackground(Void... params){

        try{
            mA.gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mA.mGoogleApiClient = new GoogleApiClient.Builder(mA)
                    .enableAutoManage(mA /* FragmentActivity */, mA /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, mA.gso)
                    .build();

            mA.signIn();
        }catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }



}
