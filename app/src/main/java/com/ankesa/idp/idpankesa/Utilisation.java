package com.ankesa.idp.idpankesa;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;

 class Utilisation {

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    static boolean isNotEmpty(EditText editText) {
       return editText.getText().toString().trim().length() > 0;
   }

   @NonNull
    static String getString(EditText editText) {
       return editText.getText().toString().trim();
   }

    static boolean checkPermission(Context context, String permission) {
       if (isMarshmallow()) {
           int result = ContextCompat.checkSelfPermission(context, permission);
           return result == PackageManager.PERMISSION_GRANTED;
       } else {
           return true;
       }
   }

   private static boolean isMarshmallow() {
       return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
   }

}
