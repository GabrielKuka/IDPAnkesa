package com.ankesa.idp.idpankesa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;

import it.michelelacorte.scrollableappbar.ScrollableAppBar;

public class RrethNesh extends AppCompatActivity {
    ScrollableAppBar appBarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle e) {
        super.onCreate(e);
        setContentView(R.layout.rrethnesh_layout);
        appBarAnimation();

    }


    private void appBarAnimation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.white));
        appBarLayout = (ScrollableAppBar) findViewById(R.id.appbar);
        if (appBarLayout != null)
            appBarLayout.collapseToolbar();


        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int mutedColor = palette.getVibrantSwatch().getRgb();
                collapsingToolbarLayout.setContentScrimColor(mutedColor);
                collapsingToolbarLayout.setStatusBarScrimColor(mutedColor);

            }
        });

       // appBarLayout.collapseToolbar(true);
    }

    public void launchWebsite(View view){
        String url = "http://www.idp.al/index.php/sq/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void callIDP(View view){
        String phoneNumber = "+35542237200";
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    public void complain(View view){
        startActivity(new Intent(this, BlankActivity.class));
    }

}