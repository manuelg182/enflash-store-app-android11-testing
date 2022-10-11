/*
 * Copyright 2013-2017 Amazon.com,
 * Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the
 * License. A copy of the License is located at
 *
 *      http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, express or implied. See the License
 * for the specific language governing permissions and
 * limitations under the License.
 */

package com.enflash.mobile.storeapp.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
//import

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChooseMfaContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.enflash.mobile.storeapp.R;
import com.enflash.mobile.storeapp.application.AppHelper;
import com.enflash.mobile.storeapp.main.MainActivity;
import com.enflash.mobile.storeapp.utils.CustomProgress;
import com.enflash.mobile.storeapp.utils.PreferencesManager;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private AlertDialog userDialog;
    private ProgressDialog waitDialog;
    private CustomProgress progress = new CustomProgress();

    private EditText inEmail;
    private EditText inPassword;

    private MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation;
    private ForgotPasswordContinuation forgotPasswordContinuation;
    private NewPasswordContinuation newPasswordContinuation;
    private ChooseMfaContinuation mfaOptionsContinuation;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppHelper.init(getApplicationContext());
        initApp();
        findCurrent();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                // Register user
                if (resultCode == RESULT_OK) {
                    String name = data.getStringExtra("name");
                    if (!name.isEmpty()) {
                        inEmail.setText(name);
                        inPassword.setText("");
                        inPassword.requestFocus();
                    }
                    String userPasswd = data.getStringExtra("password");
                    if (!userPasswd.isEmpty()) {
                        inPassword.setText(userPasswd);
                    }
                    if (!name.isEmpty() && !userPasswd.isEmpty()) {
                        // We have the user details, so sign in!
                        email = name;
                        password = userPasswd;
                        AppHelper.getPool().getUser(email).getSessionInBackground(authenticationHandler);
                    }
                }
                break;
            case 2:
                // Confirm register user
                if (resultCode == RESULT_OK) {
                    String name = data.getStringExtra("name");
                    if (!name.isEmpty()) {
                        inEmail.setText(name);
                        inPassword.setText("");
                        inPassword.requestFocus();
                    }
                }
                break;
            case 3:
                // Forgot password
                if (resultCode == RESULT_OK) {
                    String newPass = data.getStringExtra("newPass");
                    String code = data.getStringExtra("code");
                    if (newPass != null && code != null) {
                        if (!newPass.isEmpty() && !code.isEmpty()) {
                            showWaitDialog("Actualizando la contraseña...");
                            forgotPasswordContinuation.setPassword(newPass);
                            forgotPasswordContinuation.setVerificationCode(code);
                            forgotPasswordContinuation.continueTask();
                        }
                    }
                }
                break;
            case 4:
                // User
                if (resultCode == RESULT_OK) {
                    clearInput();
                    String name = data.getStringExtra("TODO");
                    if (name != null) {
                        if (!name.isEmpty()) {
                            name.equals("exit");
                            onBackPressed();
                        }
                    }
                }
                break;
            case 5:
                //MFA
                closeWaitDialog();
                if (resultCode == RESULT_OK) {
                    String code = data.getStringExtra("mfacode");
                    if (code != null) {
                        if (code.length() > 0) {
                            showWaitDialog("Logeandose...");
                            multiFactorAuthenticationContinuation.setMfaCode(code);
                            multiFactorAuthenticationContinuation.continueTask();
                        } else {
                            inPassword.setText("");
                            inPassword.requestFocus();
                        }
                    }
                }
                break;
            case 6:
                //New password
                closeWaitDialog();
                Boolean continueSignIn = false;
                if (resultCode == RESULT_OK) {
                    continueSignIn = data.getBooleanExtra("continueSignIn", false);
                }
                if (continueSignIn) {
                    continueWithFirstTimeSignIn();
                }
                break;
            case 7:
                // Choose MFA
                closeWaitDialog();
                if (resultCode == RESULT_OK) {
                    String option = data.getStringExtra("mfaOption");
                    if (option != null) {
                        if (option.length() > 0) {
                            Log.d(TAG, " -- Selected Option: " + option);
                            conitnueWithSelectedMfa(option);
                        } else {
                            inPassword.setText("");
                            inPassword.requestFocus();
                        }
                    }
                }
        }
    }

    // Login if a user is already present
    public void logIn(View view) {
        signInUser();
    }

    // Forgot password processing
    public void forgotPassword(View view) {
        forgotpasswordUser();
    }

    private long mLastClickTime = 0;

    private void signInUser() {

        progress.show(LoginActivity.this, false);

        if (SystemClock.elapsedRealtime() - mLastClickTime < 4000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        email = inEmail.getText().toString();
        if (email == null || email.length() < 1) {
            inEmail.setBackground(getDrawable(R.drawable.text_border_error));
            inEmail.requestFocus();
            inEmail.setError("Campo requerido.");
            hide();
            return;
        }

        password = inPassword.getText().toString();
        if (password == null || password.length() < 1) {
            inPassword.setBackground(getDrawable(R.drawable.text_border_error));
            inPassword.requestFocus();
            inPassword.setError("Campo requerido");
            hide();
            return;
        }
        LoginActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    AppHelper.setUser(email);
                    AppHelper.getPool().getUser(email).getSessionInBackground(authenticationHandler);
                }, 800);
            }
        });
    }

    private void forgotpasswordUser() {
        email = inEmail.getText().toString();
        if (email == null) {
            inEmail.setBackground(getDrawable(R.drawable.text_border_error));
            inEmail.requestFocus();
            inEmail.setError("Campo requerido");
            return;
        }

        if (email.length() < 1) {
            inEmail.setBackground(getDrawable(R.drawable.text_border_error));
            inEmail.requestFocus();
            inEmail.setError("Campo requerido");
            return;
        }

        showWaitDialog("");
        AppHelper.getPool().getUser(email).forgotPasswordInBackground(forgotPasswordHandler);
    }

    private void getForgotPasswordCode(ForgotPasswordContinuation forgotPasswordContinuation) {
        this.forgotPasswordContinuation = forgotPasswordContinuation;
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        intent.putExtra("destination", forgotPasswordContinuation.getParameters().getDestination());
        intent.putExtra("deliveryMed", forgotPasswordContinuation.getParameters().getDeliveryMedium());
        startActivityForResult(intent, 3);
    }

    private void mfaAuth(MultiFactorAuthenticationContinuation continuation) {
       /* multiFactorAuthenticationContinuation = continuation;
        Intent mfaActivity = new Intent(this, MFAActivity.class);
        mfaActivity.putExtra("mode", multiFactorAuthenticationContinuation.getParameters().getDeliveryMedium());
        startActivityForResult(mfaActivity, 5);*/
    }

    private void firstTimeSignIn() {
       /* Intent newPasswordActivity = new Intent(this, NewPassword.class);
        startActivityForResult(newPasswordActivity, 6);*/
    }

    private void selectMfaToSignIn(List<String> options, Map<String, String> parameters) {
       /* Intent chooseMfaActivity = new Intent(this, ChooseMFA.class);
        AppHelper.setMfaOptionsForDisplay(options, parameters);
        startActivityForResult(chooseMfaActivity, 7);*/
    }

    private void conitnueWithSelectedMfa(String option) {
        // mfaOptionsContinuation.setChallengeResponse("ANSWER", option);
        mfaOptionsContinuation.setMfaOption(option);
        mfaOptionsContinuation.continueTask();
    }

    private void continueWithFirstTimeSignIn() {
        newPasswordContinuation.setPassword(AppHelper.getPasswordForFirstTimeLogin());
        Map<String, String> newAttributes = AppHelper.getUserAttributesForFirstTimeLogin();
        if (newAttributes != null) {
            for (Map.Entry<String, String> attr : newAttributes.entrySet()) {
                Log.d(TAG, String.format(" -- Adding attribute: %s, %s", attr.getKey(), attr.getValue()));
                newPasswordContinuation.setUserAttribute(attr.getKey(), attr.getValue());
            }
        }
        try {
            newPasswordContinuation.continueTask();
        } catch (Exception e) {
            closeWaitDialog();

            inPassword.setBackground(getDrawable(R.drawable.text_border_error));
            inPassword.requestFocus();
            inPassword.setError("No se pudo logear");

            inEmail.setBackground(getDrawable(R.drawable.text_border_error));
            inEmail.requestFocus();
            inEmail.setError("No se pudo logear");

            showDialogMessage("No se pudo logear", AppHelper.formatException(e));
        }
    }

    private void launchUser() {
        Intent userActivity = new Intent(this, MainActivity.class);
        userActivity.putExtra("name", email);
        userActivity.putExtra("start", false);
        startActivityForResult(userActivity, 4);
        finish();
    }

    private void findCurrent() {
        CognitoUser user = AppHelper.getPool().getCurrentUser();
        email = user.getUserId();
        if (email != null) {
            AppHelper.setUser(email);
            inEmail.setText(user.getUserId());
            user.getSessionInBackground(authenticationHandler);
        }
    }

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        if (username != null) {
            this.email = username;
            AppHelper.setUser(username);
        }
        if (this.password == null) {
            inEmail.setText(username);
            password = inPassword.getText().toString();
            if (password == null) {
                inPassword.setBackground(getDrawable(R.drawable.text_border_error));
                inPassword.requestFocus();
                inPassword.setError("Ingresa la contraseña");
                return;
            }

            if (password.length() < 1) {
                inPassword.setBackground(getDrawable(R.drawable.text_border_error));
                inPassword.requestFocus();
                inPassword.setError("Ingresa la contraseña");
                return;
            }
        }
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(this.email, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    // initialize app
    private void initApp() {
        inEmail = findViewById(R.id.editTextUserId);
        inEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    inEmail.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inPassword = findViewById(R.id.editTextUserPassword);
        inPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    inPassword.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        //Función para hacer el drawable clickeable y descubrir contraseña
       /* inPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                boolean clicked = false;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (inPassword.getRight() - inPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {



                        return true;

                    }

                }
                return false;
            }

            });*/

    }

    // Callbacks
    ForgotPasswordHandler forgotPasswordHandler = new ForgotPasswordHandler() {
        @Override
        public void onSuccess() {
            closeWaitDialog();
            showDialogMessage("Contraseña cambiada correctamente", "");
            inPassword.setText("");
            inPassword.requestFocus();
        }

        @Override
        public void getResetCode(ForgotPasswordContinuation forgotPasswordContinuation) {
            closeWaitDialog();
            getForgotPasswordCode(forgotPasswordContinuation);
        }

        @Override
        public void onFailure(Exception e) {
            closeWaitDialog();
            showDialogMessage("No se pudo recupear tu contraseña", AppHelper.formatException(e));
        }
    };

    private void hide() {
        if (progress.getDialog() != null) {
            if (progress.getDialog().isShowing()) {
                progress.getDialog().dismiss();
            }
        }
    }
    GetDetailsHandler getDetailsHandler = new GetDetailsHandler() {
        @Override
        public void onSuccess(CognitoUserDetails cognitoUserDetails) {
            try {
                Map<String, String> m = cognitoUserDetails.getAttributes().getAttributes();
                if(m.containsKey("custom:companyId")){
                    PreferencesManager.setCompanyId(Long.valueOf(m.get("custom:companyId")));
                }
                Log.i(TAG, PreferencesManager.getCompanyId().toString());
            }catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
            hide();
            closeWaitDialog();
            launchUser();
        }

        @Override
        public void onFailure(Exception exception) {
            hide();
            // Fetch user details failed, check exception for the cause
        }
    };
    //
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.d(TAG, " -- Auth Success");
            AppHelper.setCurrSession(cognitoUserSession);
            AppHelper.newDevice(device);
            CognitoUser user = AppHelper.getPool().getCurrentUser();
            user.getDetailsInBackground(getDetailsHandler);
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            Locale.setDefault(Locale.US);
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            hide();
            closeWaitDialog();
            mfaAuth(multiFactorAuthenticationContinuation);
        }

        @Override
        public void onFailure(Exception e) {
            hide();
            closeWaitDialog();
            inPassword.setBackground(getDrawable(R.drawable.text_border_error));
            inPassword.requestFocus();
            inPassword.setError("No se pudo logear");

            inEmail.setBackground(getDrawable(R.drawable.text_border_error));
            inEmail.requestFocus();
            inEmail.setError("No se pudo logear");
            showDialogMessage("No se pudo logear", AppHelper.formatException(e));
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            hide();
            /**
             * For Custom authentication challenge, implement your logic to present challenge to the
             * user and pass the user's responses to the continuation.
             */
            if ("NEW_PASSWORD_REQUIRED".equals(continuation.getChallengeName())) {
                // This is the first sign-in attempt for an admin created user
                newPasswordContinuation = (NewPasswordContinuation) continuation;
                AppHelper.setUserAttributeForDisplayFirstLogIn(newPasswordContinuation.getCurrentUserAttributes(),
                        newPasswordContinuation.getRequiredAttributes());
                closeWaitDialog();
                firstTimeSignIn();
            } else if ("SELECT_MFA_TYPE".equals(continuation.getChallengeName())) {
                closeWaitDialog();
                mfaOptionsContinuation = (ChooseMfaContinuation) continuation;
                List<String> mfaOptions = mfaOptionsContinuation.getMfaOptions();
                selectMfaToSignIn(mfaOptions, continuation.getParameters());
            }
        }
    };

    private void clearInput() {
        if (inEmail == null) {
            inEmail = (EditText) findViewById(R.id.editTextUserId);
        }

        if (inPassword == null) {
            inPassword = (EditText) findViewById(R.id.editTextUserPassword);
        }

        inEmail.setText("");
        inEmail.requestFocus();
        inEmail.setBackground(getDrawable(R.drawable.text_border_selector));
        inPassword.setText("");
        inPassword.setBackground(getDrawable(R.drawable.text_border_selector));
    }

    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    };

    private void closeWaitDialog() {
        try {
            if (waitDialog != null) {
                if (waitDialog.isShowing()) {
                    waitDialog.dismiss();
                }
            }
        } catch (Exception e) {
            //
        }
    }
}
