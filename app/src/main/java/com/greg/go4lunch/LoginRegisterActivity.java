package com.greg.go4lunch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.auth.FacebookAuthProvider;
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

    private CallbackManager mCallbackManager;
    @BindView(R.id.facebook_login_button) LoginButton mFacebookLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        fireBaseAuth();
        clickGoogleSignInButton();
        googleSignInConfigure();

        initFacebook();
        initFacebookLoginButton();
    }

    // ---------------------------- Initialize Firebase authentication -----------------------------
    private void fireBaseAuth(){
        mAuth = FirebaseAuth.getInstance();
    }

    // ---------------------------- GOOGLE AUTHENTICATION ------------------------------------------------------------------------------------------------------------

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
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // ---------------------------- Handle Google result  ------------------------------------------
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

    // ---------------------------- FACEBOOK AUTHENTICATION ----------------------------------------------------------------------------------------------------------

    // ---------------------------- Initialize Facebook --------------------------------------------
    private void initFacebook(){
        mCallbackManager = CallbackManager.Factory.create();
    }

    // ---------------------------- Initialize Facebook login button -------------------------------
    private void initFacebookLoginButton(){
       mFacebookLoginButton = findViewById(R.id.facebook_login_button);
       mFacebookLoginButton.setReadPermissions("email", "public_profile");
       mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
           @Override
           public void onSuccess(LoginResult loginResult) {
               Log.d(TAG, "facebook:onSuccess:" + loginResult);
               Toasty.success(LoginRegisterActivity.this, "Login in with Facebook successfully", Toasty.LENGTH_SHORT).show();
               handleFacebookAccessToken(loginResult.getAccessToken());
           }

           @Override
           public void onCancel() {
               Log.d(TAG, "facebook:onCancel:");
               Toasty.warning(LoginRegisterActivity.this, "Login Facebook cancelled", Toasty.LENGTH_SHORT).show();
           }

           @Override
           public void onError(FacebookException error) {
               Log.d(TAG, "facebook:onError:" + error);
               Toasty.error(LoginRegisterActivity.this, "Login Facebook error", Toasty.LENGTH_SHORT).show();
           }
       });
    }

    // ---------------------------- Handle Facebook result  ----------------------------------------
    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "signInWithCredential:success");
                            Toasty.success(LoginRegisterActivity.this, "Login Facebook successful", Toasty.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else{
                            Log.w(TAG, "signInWithCredential:failure" + task.getException());
                            Toasty.error(LoginRegisterActivity.this, "Login with Facebook failed", Toasty.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
}
