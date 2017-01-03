package com.ankesa.idp.idpankesa;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

 class ConnectivityState {

    private Activity ac;

     ConnectivityState(Activity ac){
        this.ac = ac;
    }

    boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) ac.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        return nInfo != null && nInfo.isConnected();
    }

}
