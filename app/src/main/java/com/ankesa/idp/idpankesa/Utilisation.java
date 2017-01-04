package com.ankesa.idp.idpankesa;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;

class Utilisation {

    // File manager variables
    static final int REQUEST_READWRITE_STORAGE = 1997;
    static final int FILE_MANAGER_DIALOG_ID = 1998;

    // Gmail log in variables
    static final int REQUEST_ACCOUNT_PICKER = 1999;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    // Vertical step number variables
    static final int NAME_STEP_NUM = 0;
    static final int EMAIL_STEP_NUM = 1;
    static final int ADRESA_STEP_NUM = 2;
    static final int TELEFON_STEP_NUM = 3;
    static final int KONTROLLUESI_STEP_NUM = 4;
    static final int ADKONTROLLUESIT_STEP_NUM = 5;
    static final int ANKESA_STEP_NUM = 6;
    static final int KERKESA_STEP_NUM = 7;

    static final int MIN_CHARACTERS_emri = 5;

    // Dialog variables
    static final int PASSWORD_DIALOG = 123456;
    static final int CAPTCHA_DIALOG = 123456789;

    static final String STATE_NAME = "Name";
    static final String STATE_EMAIL = "Email";
    static final String STATE_ADRESA = "Adresa";
    static final String STATE_TELEFON = "Telefoni";
    static final String STATE_KONTROLLUESI = "Kontrolluesi";
    static final String STATE_ADKONTROLLUESIT = "Adresa e Kontrolluesit";
    static final String STATE_ANKESA = "Ankesa e qytetarit";
    static final String STATE_KERKESA = "Kerkesa e qytetarit";

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
