package com.ankesa.idp.idpankesa;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.fabtransitionactivity.SheetLayout;
import com.wajahatkarim3.easyflipview.EasyFlipView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SheetLayout.OnFabAnimationEndListener {

    private static final int REQUEST_CODE = 1;
    private SheetLayout mSheetLayout;
    private EasyFlipView flipView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme_NoActionBar); // <- Sets the default theme of the application
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // <- Sets the layout of the activity

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        flipView = (EasyFlipView) findViewById(R.id.flipViewId);
        flipView.setFlipDuration(1000);
        flipView.setFlipEnabled(true);
        flipView.setFlipOnTouch(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        mSheetLayout = (SheetLayout) findViewById(R.id.bottom_sheet);
        mSheetLayout.setFab(fab);
        mSheetLayout.setFabAnimationEndListener(this);

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
        startActivityForResult(ankesa, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            mSheetLayout.contractFab();
        }
    }

    @Override
    public void onClick(View v) {

        ConnectivityState connectivityState = new ConnectivityState(this);
        if (connectivityState.isConnected())
            mSheetLayout.expandFab();
        else
            Toast.makeText(getApplicationContext(), "Duhet të jeni i lidhur në internet për të bërë një ankesë.", Toast.LENGTH_LONG).show();


    }

    public void flipView(View view){
        flipView.flipTheView();
    }

}
