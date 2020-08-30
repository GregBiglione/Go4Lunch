package com.greg.go4lunch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;

public class LoginRegisterActivity extends AppCompatActivity {

    @BindView(R.id.sign_in_google_button) SignInButton mGoogleButton;
    private GoogleSignInClient mGoogleSignInClient;
    public static final String TAG = "LoginRegisterActivity";
    private FirebaseAuth mAuth;
    public static final int RC_SIGN_IN = 807;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        fireBaseAuth();
        clickGoogleSignInButton();
        googleSignInConfigure();
    }

    // ---------------------------- GOOGLE AUTHENTICATION ------------------------------------------------------------------------------------------------------------

    // ---------------------------- Initialize Firebase authentication -----------------------------
    private void fireBaseAuth(){
        mAuth = FirebaseAuth.getInstance();
    }

    // ---------------------------- Configure Google Sign In ---------------------------------------
    public void googleSignInConfigure(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    // ---------------------------- Click on Google Sign In button ---------------------------------
    public void clickGoogleSignInButton(){
        mGoogleButton = findViewById(R.id.sign_in_google_button);
        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    // ---------------------------- Progress dialog bar --------------------------------------------
    private void progressDialog(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
    }

    // ---------------------------- Sign In with Google --------------------------------------------
    private void signIn() {
        progressDialog();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // ---------------------------- Handle result  -------------------------------------------------
    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
            Toasty.success(this, "Signed in successfully", Toasty.LENGTH_SHORT).show();
            fireBaseGoogleAuth(googleSignInAccount);
        }
        catch(Exception e){
            Toasty.error(this, "Signed in failed", Toasty.LENGTH_SHORT).show();
            fireBaseGoogleAuth(null);
        }
    }

    // ---------------------------- Connection to Firebase with Google -----------------------------
    private void fireBaseGoogleAuth(GoogleSignInAccount googleSignInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toasty.success(LoginRegisterActivity.this, "Successful", Toasty.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
                else{
                    Toasty.error(LoginRegisterActivity.this, "Failed", Toasty.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    // ---------------------------- Get Google user information ------------------------------------
    private void updateUI(FirebaseUser user) {
        //GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (user != null){
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    // ---------------------------- Check if user is already logged with Google -----------------------------
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

}
