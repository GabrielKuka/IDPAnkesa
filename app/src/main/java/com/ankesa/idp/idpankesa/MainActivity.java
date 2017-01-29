package com.ankesa.idp.idpankesa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.github.fabtransitionactivity.SheetLayout;
import com.melnykov.fab.FloatingActionButton;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import static com.ankesa.idp.idpankesa.Utilisation.REQUEST_READWRITE_STORAGE;


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

        permissionCheckForMarshmallow();

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

    public void permissionCheckForMarshmallow() {
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READWRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READWRITE_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // finishCreationStep();
            }
        }
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
            MyDynamicToast.warningMessage(MainActivity.this, "Duhet të jeni i lidhur në internet për të bërë një ankesë.");


    }

    public void flipView(View view) {
        flipView.flipTheView();
    }

}
