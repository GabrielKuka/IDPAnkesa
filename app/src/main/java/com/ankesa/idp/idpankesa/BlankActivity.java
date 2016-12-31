package com.ankesa.idp.idpankesa;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.fragments.BackConfirmationFragment;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

public class BlankActivity extends AppCompatActivity implements VerticalStepperForm {

    public static final String STATE_NAME = "Name";
    public static final String STATE_EMAIL = "Email";
    public static final String STATE_ADRESA = "Adresa";
    public static final String STATE_QYTETI = "Qyteti";
    public static final String STATE_TELEFON = "Telefoni";
    public static final String STATE_KONTROLLUESI = "Kontrolluesi";
    public static final String STATE_ADKONTROLLUESIT = "Adresa e Kontrolluesit";
    public static final String STATE_QYTETIKONTROLLUESIT = "Qyteti i kontrolluesit";
    public static final String STATE_ANKESA = "Ankesa e qytetarit";
    public static final String STATE_KERKESA = "Kerkesa e qytetarit";

    //File manager vars
    static final int FILE_MANAGER_DIALOG_ID = 1998;
    private static final int NAME_STEP_NUM = 0;
    private static final int EMAIL_STEP_NUM = 1;
    private static final int ADRESA_STEP_NUM = 2;
    private static final int QYTETI_STEP_NUM = 3;
    private static final int TELEFON_STEP_NUM = 4;
    private static final int KONTROLLUESI_STEP_NUM = 5;
    private static final int ADKONTROLLUESIT_STEP_NUM = 6;
    private static final int QYTETIKONTROLLUESIT_STEP_NUM = 7;
    private static final int ANKESA_STEP_NUM = 8;
    private static final int KERKESA_STEP_NUM = 9;
    private static final int MIN_CHARACTERS_emri = 5;
    private static final int PASSWORD_DIALOG = 123456;
    private static final int CAPTCHA_DIALOG = 123456789;
    public Model[] modelItems;
    public File root, curFolder, selected;
    ImageView fileManagerBackButton;
    TextView currentPathText;
    ListView dialogListView;
    Dialog dialog;
    FileValidation fileValidation;
    EmailPassword emailPassword;
    private boolean confirmBack = true;
    private ProgressDialog progressDialog;
    private boolean beforeTextChanged = true;
    // Name step
    private EditText nameEditText;
    // Email step
    private EditText emailEditText;
    // Adresa step
    private EditText adresaEditText;
    // Qyteti step
    private EditText qytetiEditText;
    // Telefon step
    private EditText telefonEditText;
    // Kontrolluesi step
    private EditText kontrolluesiEditText;
    // Adresa e kontrolluesit step
    private EditText adkontrolluesitEditText;
    // Qyteti i kontrolluesit step
    private EditText qytetiKontrolluesitEditText;
    // Ankesa step
    private EditText ankesaEditText;
    // Kerkesa step
    private EditText kerkesaEditText;
    private VerticalStepperFormLayout verticalStepperForm;
    private List<String> fileList = new ArrayList<>();
    private boolean[] fileTypes;
    private int REQUEST_READWRITE_STORAGE = 1997;
    private String nameExtra, emailExtra;
    private Button buttonOpenDialog;
    private EditText password;
    private EditText captchaAnswer;
    private Captcha c;
    private  ImageView captchaImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nameExtra = extras.getString("NAME");
            emailExtra = extras.getString("EMAIL");
        }

        permissionCheckForMarshmallow(); // <- Grants run-time permission (for marshmallow)

        fileValidation = new FileValidation(false);

        buttonOpenDialog = (Button) findViewById(R.id.fileManagerButtonId);
        buttonOpenDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(FILE_MANAGER_DIALOG_ID);
            }
        });

        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        curFolder = root;

        String[] stepsemris = {"Emri dhe mbiemri", "Email-i", "Adresa", "Qyteti", "Telefoni", "Kontrolluesi publik ose privat", "Adresa e kontrolluesit", "Qyteti i kontrolluesit", "Ankesa", "Kërkesa"};
        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);

        // Here we find and initialize the form
        verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, stepsemris, this, this)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true)
                .showVerticalLineWhenStepsAreCollapsed(true)
                .init();

        emailPassword = new EmailPassword();

    }

    @Override
    protected Dialog onCreateDialog(int id) {

        dialog = null;

        switch (id) {
            case FILE_MANAGER_DIALOG_ID:
                dialog = new Dialog(BlankActivity.this);
                dialog.setContentView(R.layout.dialoglayout);
                dialog.setTitle("Zgjidh identifikimin");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);

                currentPathText = (TextView) dialog.findViewById(R.id.folder);
                fileManagerBackButton = (ImageView) dialog.findViewById(R.id.up);
                fileManagerBackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListFolders(curFolder.getParentFile());

                    }
                });

                dialogListView = (ListView) dialog.findViewById(R.id.dialoglist);
                dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selected = new File(fileList.get(position));
                        if (selected.isDirectory()) {
                            ListFolders(selected);
                        } else {

                            validateFileSelected(selected);
                            getDialog().dismiss();
                        }
                    }
                });

                break;
            case PASSWORD_DIALOG:
                dialog = new Dialog(BlankActivity.this);
                dialog.setContentView(R.layout.password_dialog_layout);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                password = (EditText) dialog.findViewById(R.id.passFieldId);
                Button passSubButton = (Button) dialog.findViewById(R.id.passwordSubmitButton);
                passSubButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (password.getText().toString().isEmpty()) {
                            displayMessage("Fjalëkalimi është bosh.");
                        } else {
                            emailPassword.setPassword(password.getText().toString());
                            dialog.dismiss();
                            showDialog(CAPTCHA_DIALOG);
                        }
                    }
                });

                break;
            case CAPTCHA_DIALOG:
                dialog = new Dialog(BlankActivity.this);
                dialog.setContentView(R.layout.captcha_dialog_layout);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                captchaAnswer = (EditText) dialog.findViewById(R.id.pergjigjaId);
                captchaImage = (ImageView) dialog.findViewById(R.id.imageCaptcha);
                Button captchaButton = (Button) dialog.findViewById(R.id.captchaButton);
                c = new MathCaptcha(300, 100, MathCaptcha.MathOptions.PLUS_MINUS_MULTIPLY);
                captchaImage.setImageBitmap(c.image);
                captchaImage.setLayoutParams(new LinearLayout.LayoutParams(c.getWidth() * 2, c.getHeight() * 2));

                captchaButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (captchaAnswer.getText().toString().equals(c.answer)) {
                            executeDataSending();
                        } else {
                            displayMessage("Gabim!");
                            captchaAnswer.setText("");
                            c = new MathCaptcha(300, 100, MathCaptcha.MathOptions.PLUS_MINUS_MULTIPLY);
                            captchaImage.setImageBitmap(c.image);
                            captchaImage.setLayoutParams(new LinearLayout.LayoutParams(c.getWidth() * 2, c.getHeight() * 2));
                        }
                    }
                });

                break;
        }
        return dialog;
    }

    // Validates the file sectected by the user
    public void validateFileSelected(File file) {
        if (file.getPath().endsWith(".jpg") || file.getPath().endsWith(".png") || file.getPath().endsWith(".pdf") || file.getPath().endsWith(".tiff") || file.getPath().endsWith(".doc") || file.getPath().endsWith(".bmp") || file.getPath().endsWith(".jpeg")) {
            buttonOpenDialog.setText(R.string.chosen);
            Toast.makeText(BlankActivity.this, "Dokumenti " + getFileName(selected) + " u zgjodh.",
                    Toast.LENGTH_LONG).show();
            fileValidation.setFileValidated(true);
        } else {
            buttonOpenDialog.setText(R.string.uploadID);
            Toast.makeText(BlankActivity.this, "Dokumenti që zgjodhët nuk është i vlefshëm.",
                    Toast.LENGTH_LONG).show();
            fileValidation.setFileValidated(false);
        }
    }

    // Returns the name of the file the user has selected
    public String getFileName(File file) {
        return file.getPath().substring(file.toString().lastIndexOf("/") + 1);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case FILE_MANAGER_DIALOG_ID:
                ListFolders(curFolder);
                break;
        }
    }

    // Returns the dialog created for the file manager
    private Dialog getDialog() {

        return dialog;

    }

    // Lists the files and folders of the device when the users wants to upload his document
    void ListFolders(File f) {
        if (f.equals(root)) {
            fileManagerBackButton.setEnabled(false);
        } else {
            fileManagerBackButton.setEnabled(true);
        }

        curFolder = f;
        currentPathText.setText(f.getPath());

        File[] files = f.listFiles();
        fileList.clear();

        int fileTypesIndex = -1;


        modelItems = new Model[files.length];
        fileTypes = new boolean[files.length];

        for (File file : files) {
            fileList.add(file.getPath());
            setTypeOfFile(file, ++fileTypesIndex);

        }

        for (int x = 0; x < files.length; x++) {
            if (fileTypes[x])
                modelItems[x] = new Model(fileList.get(x).substring(fileList.get(x).lastIndexOf("/") + 1), true);
            else
                modelItems[x] = new Model(fileList.get(x).substring(fileList.get(x).lastIndexOf("/") + 1), false);
        }

        CustomAdapter adapter = new CustomAdapter(BlankActivity.this, modelItems);
        dialogListView.setAdapter(adapter);

    }


    public void setTypeOfFile(File file, int a) {
        fileTypes[a] = file.isDirectory();
    }


    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        switch (stepNumber) {
            case NAME_STEP_NUM:
                view = createNameStep();
                break;
            case EMAIL_STEP_NUM:
                view = createEmailStep();
                break;
            case ADRESA_STEP_NUM:
                view = createAdresaStep();
                break;
            case QYTETI_STEP_NUM:
                view = createQytetiStep();
                break;
            case TELEFON_STEP_NUM:
                view = createTelefonStep();
                break;
            case KONTROLLUESI_STEP_NUM:
                view = createKontrolluesiStep();
                break;
            case ADKONTROLLUESIT_STEP_NUM:
                view = createADKontrolluesiStep();
                break;
            case QYTETIKONTROLLUESIT_STEP_NUM:
                view = createQytetiKontrolluesitStep();
                break;
            case ANKESA_STEP_NUM:
                view = createAnkesaStep();
                break;
            case KERKESA_STEP_NUM:
                view = createKerkesaStep();
                break;
        }
        return view;
    }

    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case NAME_STEP_NUM:
                // When this step is open, we check that the emri is correct
                checkNameStep(nameEditText.getText().toString());
                break;
            case EMAIL_STEP_NUM:
                checkEmailStep(emailEditText.getText().toString());
                break;
            case ADRESA_STEP_NUM:
                checkAdresaStep(adresaEditText.getText().toString());
                break;
            case QYTETI_STEP_NUM:
                checkQytetiStep(qytetiEditText.getText().toString());
                break;
            case TELEFON_STEP_NUM:
                checkTelefoniStep(telefonEditText.getText().toString());
                break;
            case KONTROLLUESI_STEP_NUM:
                checkKontrolluesiStep(kontrolluesiEditText.getText().toString());
                break;
            case ADKONTROLLUESIT_STEP_NUM:
                checkAdKontrolluesiStep(adkontrolluesitEditText.getText().toString());
                break;
            case QYTETIKONTROLLUESIT_STEP_NUM:
                checkQytetiKontrolluesitStep(qytetiKontrolluesitEditText.getText().toString());
                break;
            case ANKESA_STEP_NUM:
                checkAnkesaStep(ankesaEditText.getText().toString());
                break;
            case KERKESA_STEP_NUM:
                checkKerkesaStep(kerkesaEditText.getText().toString());
                break;
        }
    }

    @Override
    public void sendData() {

        ConnectivityState cS = new ConnectivityState(this);
        if (cS.isConnected()) {

            if (emailEditText.getText().toString().contains("@gmail.com")) {
                showDialog(PASSWORD_DIALOG);
            } else {
                showDialog(CAPTCHA_DIALOG);
            }
        } else {
            displayMessage("Ju nuk jeni lidhur me internet.");
        }

    }

    private void executeDataSending() {
        Log.d("Email-i: ", emailEditText.getText().toString());
        final String CC = emailEditText.getText().toString();
        final String subject = "Ankesë për IDP";
        final String message = "Emri/Mbiemri: " + nameEditText.getText().toString() + System.lineSeparator() + "Adresa: " + adresaEditText.getText().toString() + System.lineSeparator()
                + "Qyteti: " + qytetiEditText.getText().toString() + System.lineSeparator() + System.lineSeparator()
                + "Telefoni: " + telefonEditText.getText().toString() + System.lineSeparator() + System.lineSeparator()
                + "E-mail-i: " + emailEditText.getText().toString() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator()
                + "Kontrolluesi publik ose privat: " + kontrolluesiEditText.getText().toString() + System.lineSeparator() + System.lineSeparator()
                + "Adresa: " + adkontrolluesitEditText.getText().toString() + System.lineSeparator() + System.lineSeparator()
                + "Qyteti: " + qytetiKontrolluesitEditText.getText().toString() + System.lineSeparator() + System.lineSeparator()
                + "Përshkruani çdo veprim të pretenduar si shkelje nga Kontrolluesi: " + System.lineSeparator() + System.lineSeparator()
                + "~~~~~" + System.lineSeparator() + ankesaEditText.getText().toString() + System.lineSeparator() + "~~~~~" + System.lineSeparator() + System.lineSeparator()
                + "Përshkruani çfarë kërkoni nga Komisioneri për të Drejtën e Informimit dhe Mbrojtjen e të Dhënave Personale: " + System.lineSeparator() + System.lineSeparator()
                + "~~~~~" + System.lineSeparator() + kerkesaEditText.getText().toString() + System.lineSeparator() + "~~~~~" + System.lineSeparator() + System.lineSeparator()
                + "Data: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        if (emailEditText.getText().toString().contains("@gmail.com")) {


            SendEmailAsyncTask email = new SendEmailAsyncTask();
            email.activity = this;
            email.m = new Mail(emailExtra, emailPassword.getPassword());

            String[] toArr = {"kristi_semi@outlook.com"};

            email.m.setTo(toArr);
            email.m.setFrom(CC);
            email.m.setSubject(subject);
            email.m.setBody(message);
            email.execute();
        } else {


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(200);

                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setType("message/rfc822");
                        setResult(RESULT_OK, intent);
                        intent.setData(Uri.parse("mailto:" + "gabrie.kuka@gmail.com"));
                        intent.putExtra(Intent.EXTRA_CC, CC);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Ankesë për IDP");
                        intent.putExtra(Intent.EXTRA_TEXT, message);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        try {
                            startActivity(Intent.createChooser(intent, "Send mail...."));
                            // You must set confirmBack to false before calling finish() to avoid the confirmation dialog
                            confirmBack = false;
                            finish();
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(BlankActivity.this,
                                    "There is no email client installed.", Toast.LENGTH_SHORT).show();

                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private View createNameStep() {
        nameEditText = new EditText(this);
        nameEditText.setHint("Emri");
        nameEditText.setSingleLine(true);
        nameEditText.setWidth(1000);
        nameEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        nameEditText.setText(nameExtra);
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!checkNameStep(s.toString())) {
                    beforeTextChanged = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                beforeTextChanged = false;
                checkNameStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        nameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkNameStep(v.getText().toString())) {
                    beforeTextChanged = false;
                    verticalStepperForm.goToNextStep();
                }

                return false;
            }
        });

        return nameEditText;
    }

    private View createEmailStep() {
        emailEditText = new EditText(this);
        emailEditText.setHint("Email");
        emailEditText.setSingleLine(true);
        emailEditText.setWidth(1000);
        emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEditText.setText(emailExtra);
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextChanged = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                beforeTextChanged = false;
                checkEmailStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkEmailStep(v.getText().toString())) {
                    beforeTextChanged = false;
                    verticalStepperForm.goToNextStep();
                }
                return false;

            }
        });

        return emailEditText;
    }

    private View createAdresaStep() {
        adresaEditText = new EditText(this);
        adresaEditText.setHint("Adresa");
        adresaEditText.setSingleLine(true);
        adresaEditText.setWidth(1000);
        adresaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextChanged = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                beforeTextChanged = false;
                checkAdresaStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        adresaEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkAdresaStep(v.getText().toString())) {
                    beforeTextChanged = false;
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return adresaEditText;
    }

    private View createQytetiStep() {
        qytetiEditText = new EditText(this);
        qytetiEditText.setSingleLine(true);
        qytetiEditText.setHint("Qyteti");
        qytetiEditText.setWidth(1000);
        nameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        qytetiEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextChanged = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                beforeTextChanged = false;
                checkQytetiStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        qytetiEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkQytetiStep(v.getText().toString())) {
                    beforeTextChanged = false;
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return qytetiEditText;
    }

    private View createTelefonStep() {

        telefonEditText = new EditText(this);
        telefonEditText.setHint("Nr. i telefonit");
        telefonEditText.setSingleLine(true);
        telefonEditText.setWidth(1000);
        telefonEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        telefonEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextChanged = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                beforeTextChanged = false;
                checkTelefoniStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        telefonEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkTelefoniStep(v.getText().toString())) {
                    beforeTextChanged = false;
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return telefonEditText;
    }

    private View createKontrolluesiStep() {
        kontrolluesiEditText = new EditText(this);
        kontrolluesiEditText.setHint("Kontrolluesi");
        kontrolluesiEditText.setSingleLine(true);
        kontrolluesiEditText.setWidth(1000);
        nameEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        kontrolluesiEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextChanged = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                beforeTextChanged = false;
                checkKontrolluesiStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        kontrolluesiEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkKontrolluesiStep(v.getText().toString())) {
                    beforeTextChanged = false;
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return kontrolluesiEditText;
    }

    private View createADKontrolluesiStep() {
        adkontrolluesitEditText = new EditText(this);
        adkontrolluesitEditText.setSingleLine(true);
        adkontrolluesitEditText.setWidth(1000);
        adkontrolluesitEditText.setHint("Adresa e kontrolluesit");
        adkontrolluesitEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextChanged = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                beforeTextChanged = false;
                checkAdKontrolluesiStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        adkontrolluesitEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkAdKontrolluesiStep(v.getText().toString())) {
                    beforeTextChanged = false;
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return adkontrolluesitEditText;
    }

    private View createQytetiKontrolluesitStep() {
        qytetiKontrolluesitEditText = new EditText(this);
        qytetiKontrolluesitEditText.setSingleLine(true);
        qytetiKontrolluesitEditText.setWidth(1000);
        qytetiKontrolluesitEditText.setHint("Qyteti i Kontrolluesit");
        qytetiKontrolluesitEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextChanged = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                beforeTextChanged = false;
                checkQytetiKontrolluesitStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        qytetiKontrolluesitEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkQytetiKontrolluesitStep(v.getText().toString())) {
                    beforeTextChanged = false;
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return qytetiKontrolluesitEditText;
    }

    private View createAnkesaStep() {
        ankesaEditText = new EditText(this);
        ankesaEditText.setHint("Shkruani ankesën");
        ankesaEditText.setWidth(1000);
        ankesaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextChanged = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                beforeTextChanged = false;
                checkAnkesaStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ankesaEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkAnkesaStep(v.getText().toString())) {
                    beforeTextChanged = false;
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return ankesaEditText;
    }

    private View createKerkesaStep() {
        kerkesaEditText = new EditText(this);
        kerkesaEditText.setWidth(1000);
        kerkesaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextChanged = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                beforeTextChanged = false;
                checkKerkesaStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        kerkesaEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkKerkesaStep(v.getText().toString())) {
                    beforeTextChanged = false;
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return kerkesaEditText;
    }

    private boolean checkNameStep(String emri) {
        boolean nameIsCorrect = false;
        if (!beforeTextChanged || nameEditText.getText().toString().length() > MIN_CHARACTERS_emri) {
            beforeTextChanged = true;
            if (emri.length() >= MIN_CHARACTERS_emri) {
                nameIsCorrect = true;
                verticalStepperForm.setActiveStepAsCompleted();
            } else {
                String emriError = "Emri dhe mbiemri nuk janë të vlefshëm.";
                verticalStepperForm.setActiveStepAsUncompleted(emriError);
            }
        }
        return nameIsCorrect;
    }

    private boolean checkEmailStep(String email) {
        boolean emailIsCorrect = false;


        if (email.length() >= 7 && beforeTextChanged) {
            beforeTextChanged = false;
            if (email.contains("@") && email.length() >= 7) {

                emailIsCorrect = true;
                verticalStepperForm.setStepAsCompleted(1);
            } else {
                String emailError = "Email-i nuk është i vlefshëm.";
                verticalStepperForm.setActiveStepAsUncompleted(emailError);
            }
        }
        return emailIsCorrect;
    }

    private boolean checkAdresaStep(String adresa) {
        boolean adresaIsCorrect = false;
        if (!beforeTextChanged) {
            beforeTextChanged = true;
            if (adresa.length() >= 5) {
                adresaIsCorrect = true;
                verticalStepperForm.setStepAsCompleted(2);
            } else {
                String adresaError = "Adresa nuk është e vlefshme.";
                verticalStepperForm.setActiveStepAsUncompleted(adresaError);
            }
        }
        return adresaIsCorrect;
    }

    private boolean checkQytetiStep(String qyteti) {
        boolean qytetiIsCorrect = false;
        if (!beforeTextChanged) {
            beforeTextChanged = true;
            if (qyteti.length() >= 3) {
                qytetiIsCorrect = true;
                verticalStepperForm.setStepAsCompleted(3);
            } else {
                String qytetiError = "Qyteti nuk është i vlefshëm.";
                verticalStepperForm.setActiveStepAsUncompleted(qytetiError);
            }
        }
        return qytetiIsCorrect;
    }

    private boolean checkTelefoniStep(String telefoni) {
        boolean telefoniIsCorrect = false;
        if (!beforeTextChanged) {
            beforeTextChanged = true;
            if (telefoni.length() >= 3) {
                telefoniIsCorrect = true;
                verticalStepperForm.setStepAsCompleted(4);
            } else {
                String telefoniError = "Telefoni nuk është i vlefshëm.";
                verticalStepperForm.setActiveStepAsUncompleted(telefoniError);
            }
        }
        return telefoniIsCorrect;
    }

    private boolean checkKontrolluesiStep(String kontrolluesi) {
        boolean kontrolluesiIsCorrect = false;
        if (!beforeTextChanged) {
            beforeTextChanged = true;
            if (kontrolluesi.length() >= 4) {
                kontrolluesiIsCorrect = true;
                verticalStepperForm.setStepAsCompleted(5);
            } else {
                String telefoniError = "Kontrolluesi nuk është i vlefshëm.";
                verticalStepperForm.setActiveStepAsUncompleted(telefoniError);
            }
        }
        return kontrolluesiIsCorrect;
    }

    private boolean checkAdKontrolluesiStep(String adKontrolluesit) {
        boolean adKontrolluesiIsCorrect = false;
        if (!beforeTextChanged) {
            beforeTextChanged = true;
            if (adKontrolluesit.length() >= 4) {
                adKontrolluesiIsCorrect = true;
                verticalStepperForm.setStepAsCompleted(6);
            } else {
                String telefoniError = "Adresa e kontrolluesit nuk është e vlefshme.";
                verticalStepperForm.setActiveStepAsUncompleted(telefoniError);
            }
        }
        return adKontrolluesiIsCorrect;
    }

    private boolean checkQytetiKontrolluesitStep(String qytetiKontrolluesit) {
        boolean qytetiKontrolluesitIsCorrect = false;
        if (!beforeTextChanged) {
            beforeTextChanged = true;
            if (qytetiKontrolluesit.length() >= 4) {
                qytetiKontrolluesitIsCorrect = true;
                verticalStepperForm.setStepAsCompleted(7);
            } else {
                String telefoniError = "Qyteti i kontrolluesit nuk është i vlefshëm.";
                verticalStepperForm.setActiveStepAsUncompleted(telefoniError);
            }
        }
        return qytetiKontrolluesitIsCorrect;
    }

    private boolean checkAnkesaStep(String ankesa) {
        boolean ankesaIsCorrect = false;
        if (!beforeTextChanged) {
            beforeTextChanged = true;
            if (ankesa.length() >= 4) {
                ankesaIsCorrect = true;
                verticalStepperForm.setStepAsCompleted(8);
            } else {
                String ankesaError = "Ankesa nuk është e vlefshme.";
                verticalStepperForm.setActiveStepAsUncompleted(ankesaError);
            }
        }
        return ankesaIsCorrect;
    }

    private boolean checkKerkesaStep(String kerkesa) {
        boolean kerkesaIsCorrect = false;
        if (!beforeTextChanged) {
            beforeTextChanged = true;
            if (kerkesa.length() >= 4) {
                kerkesaIsCorrect = true;
                verticalStepperForm.setStepAsCompleted(9);
            } else {
                String ankesaError = "Kërkesa nuk është e vlefshme.";
                verticalStepperForm.setActiveStepAsUncompleted(ankesaError);
            }
        }
        return kerkesaIsCorrect;
    }

    // CONFIRMATION DIALOG WHEN USER TRIES TO LEAVE WITHOUT SUBMITTING

    private void confirmBack() {
        if (confirmBack && verticalStepperForm.isAnyStepCompleted()) {
            BackConfirmationFragment backConfirmation = new BackConfirmationFragment();
            backConfirmation.setOnConfirmBack(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    confirmBack = true;
                }
            });
            backConfirmation.setOnNotConfirmBack(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    confirmBack = false;
                    finish();
                }
            });
            backConfirmation.show(getSupportFragmentManager(), null);
        } else {
            confirmBack = false;
            finish();
        }
    }

    private void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    @Override
    public void onBackPressed() {
        confirmBack();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissDialog();
    }

    // SAVING AND RESTORING THE STATE

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Saving name field
        if (nameEditText != null) {
            savedInstanceState.putString(STATE_NAME, nameEditText.getText().toString());
        }

        // Saving email field
        if (emailEditText != null) {
            savedInstanceState.putString(STATE_EMAIL, emailEditText.getText().toString());
        }

        // Saving adresa field
        if (adresaEditText != null) {
            savedInstanceState.putString(STATE_ADRESA, adresaEditText.getText().toString());
        }

        // Saving qyteti field
        if (qytetiEditText != null) {
            savedInstanceState.putString(STATE_QYTETI, qytetiEditText.getText().toString());
        }

        // Saving telefon field
        if (telefonEditText != null) {
            savedInstanceState.putString(STATE_TELEFON, telefonEditText.getText().toString());
        }

        // Saving kotrrolluesi field
        if (kontrolluesiEditText != null) {
            savedInstanceState.putString(STATE_KONTROLLUESI, kontrolluesiEditText.getText().toString());
        }

        // Saving adKontrolluesi field
        if (adkontrolluesitEditText != null) {
            savedInstanceState.putString(STATE_ADKONTROLLUESIT, adkontrolluesitEditText.getText().toString());
        }

        // Saving qytetiKontrolluesit field
        if (qytetiKontrolluesitEditText != null) {
            savedInstanceState.putString(STATE_QYTETIKONTROLLUESIT, qytetiKontrolluesitEditText.getText().toString());
        }

        // Saving ankesa field
        if (ankesaEditText != null) {
            savedInstanceState.putString(STATE_ANKESA, ankesaEditText.getText().toString());
        }

        // Saving kerkesa field
        if (kerkesaEditText != null) {
            savedInstanceState.putString(STATE_KERKESA, kerkesaEditText.getText().toString());
        }

        // The call to super method must be at the end here
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        // Restoration of name field
        if (savedInstanceState.containsKey(STATE_NAME)) {
            String name = savedInstanceState.getString(STATE_NAME);
            nameEditText.setText(name);
        }

        // Restoration of email field
        if (savedInstanceState.containsKey(STATE_EMAIL)) {
            String email = savedInstanceState.getString(STATE_EMAIL);
            emailEditText.setText(email);
        }

        // Restoration of adresa field
        if (savedInstanceState.containsKey(STATE_ADRESA)) {
            String adresa = savedInstanceState.getString(STATE_ADRESA);
            adresaEditText.setText(adresa);
        }

        // Restoration of qyteti field
        if (savedInstanceState.containsKey(STATE_QYTETI)) {
            String qyteti = savedInstanceState.getString(STATE_QYTETI);
            qytetiEditText.setText(qyteti);
        }

        // Restoration of telefon field
        if (savedInstanceState.containsKey(STATE_TELEFON)) {
            String telefon = savedInstanceState.getString(STATE_TELEFON);
            telefonEditText.setText(telefon);
        }

        // Restoration of kontrolluesi field
        if (savedInstanceState.containsKey(STATE_KONTROLLUESI)) {
            String kontrolluesi = savedInstanceState.getString(STATE_KONTROLLUESI);
            kontrolluesiEditText.setText(kontrolluesi);
        }

        // Restoration of adKontrolluesi field
        if (savedInstanceState.containsKey(STATE_ADKONTROLLUESIT)) {
            String adKontrolluesit = savedInstanceState.getString(STATE_ADKONTROLLUESIT);
            adkontrolluesitEditText.setText(adKontrolluesit);
        }

        // Restoration of qytetiKontrolluesi field
        if (savedInstanceState.containsKey(STATE_QYTETIKONTROLLUESIT)) {
            String qytetiKontrolluesit = savedInstanceState.getString(STATE_QYTETIKONTROLLUESIT);
            qytetiKontrolluesitEditText.setText(qytetiKontrolluesit);
        }

        // Restoration of ankesa field
        if (savedInstanceState.containsKey(STATE_ANKESA)) {
            String ankesa = savedInstanceState.getString(STATE_ANKESA);
            ankesaEditText.setText(ankesa);
        }

        // Restoration of kerkesa field
        if (savedInstanceState.containsKey(STATE_KERKESA)) {
            String kerkesa = savedInstanceState.getString(STATE_KERKESA);
            kerkesaEditText.setText(kerkesa);
        }

        // The call to super method must be at the end here
        super.onRestoreInstanceState(savedInstanceState);
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
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_READWRITE_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // finishCreationStep();
            }
        }
    }

    public void displayMessage(String message) {
        Toast.makeText(BlankActivity.this, message, Toast.LENGTH_LONG).show();
    }

}

class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
    Mail m;
    BlankActivity activity;

    SendEmailAsyncTask() {
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {

            if (activity.fileValidation.getFileValidated()) {
                m.addAttachment(activity.selected.getAbsolutePath());
            }
            if (m.send()) {
                displayMessageWithUi("Ankesa u dërgua me sukses.");
                activity.finish();
            } else {
                displayMessageWithUi("Ankesa NUK u dërgua!");
            }

            return true;
        } catch (AuthenticationFailedException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
            e.printStackTrace();
            displayMessageWithUi("Email-i ose fjalëkalimi nuk është i saktë!");
            return false;
        } catch (MessagingException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Email failed");
            e.printStackTrace();
            displayMessageWithUi("Email-i dështoi së dërguari.");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            displayMessageWithUi("Ndodhi një gabim i papritur.");
            return false;
        }
    }

    private void displayMessageWithUi(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.displayMessage(message);
            }
        });
    }
}
