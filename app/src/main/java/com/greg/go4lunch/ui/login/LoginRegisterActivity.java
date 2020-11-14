package com.greg.go4lunch.ui.login;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.greg.go4lunch.ui.main_activity.MainActivity;
import com.greg.go4lunch.R;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;

public class LoginRegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public static final int RC_SIGN_IN = 807;
    public static final String TAG = "LoginRegisterActivity";
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private CallbackManager mCallbackManager;
    @BindView(R.id.facebook_login_button) LoginButton mFacebookLoginButton;
    @BindView(R.id.twitter_login_button) TwitterLoginButton mTwitterLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTwitter();
        setContentView(R.layout.activity_login_register);

        initFireBase();
        startSignInActivity();
        //----------------------------- Email ------------------------------------------------------
        signInWithEmail();
        //----------------------------- Facebook ---------------------------------------------------
        initFacebook();
        clickFacebookLoginButton();
    }

    //----------------------------- Initialize FireBase --------------------------------------------
    private void initFireBase(){
        mAuth = FirebaseAuth.getInstance();
    }

    //----------------------------- Check if user is currently logged ------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthStateListener);
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null){
            startActivity(new Intent(LoginRegisterActivity.this, MainActivity.class));
        }
    }

    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(
                        Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                new AuthUI.IdpConfig.TwitterBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.ic_logo_go4lunch_login)
                .build(),
                RC_SIGN_IN);
    }
//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //----------------------------- Email ------------------------------------------------------
        handleResponseAfterSignIn(requestCode, resultCode, data);
        //----------------------------- Google -----------------------------------------------------
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        //----------------------------- Facebook ---------------------------------------------------
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        //----------------------------- Twitter ----------------------------------------------------
        mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- EMAIL LOGIN ----------------------------------------------------
    //----------------------------------------------------------------------------------------------

    //----------------------------- SignIn with email ----------------------------------------------
    private void signInWithEmail(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user !=  null){
                    Toasty.success(LoginRegisterActivity.this, getString(R.string.connection_succeed), Toasty.LENGTH_SHORT).show();
                    updateUI(user);
                }
                else{
                    updateUI(null);
                }
            }
        };
    }

    //----------------------------- Handle email response ------------------------------------------
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                Toasty.success(this, getString(R.string.connection_succeed), Toasty.LENGTH_SHORT).show();
            } else { // ERRORS
                if (response == null) {
                    Toasty.warning(this, getString(R.string.error_authentication_canceled), Toasty.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toasty.error(this, getString(R.string.error_no_internet), Toasty.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toasty.error(this, getString(R.string.error_unknown_error), Toasty.LENGTH_SHORT).show();
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- GOOGLE LOGIN ---------------------------------------------------
    //----------------------------------------------------------------------------------------------

    // ---------------------------- Connection to FireBase with Google -----------------------------
    private void fireBaseGoogleAuth(GoogleSignInAccount googleSignInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toasty.success(LoginRegisterActivity.this, getString(R.string.connection_succeed), Toasty.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
                else{
                    Toasty.error(LoginRegisterActivity.this, getString(R.string.connection_failed), Toasty.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    // ---------------------------- Handle Google result  ------------------------------------------
    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
            Toasty.success(this, getString(R.string.connection_succeed), Toasty.LENGTH_SHORT).show();
            fireBaseGoogleAuth(googleSignInAccount);
        }
        catch(Exception e){
            Toasty.error(this, getString(R.string.connection_failed), Toasty.LENGTH_SHORT).show();
            fireBaseGoogleAuth(null);
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- FACEBOOK LOGIN -------------------------------------------------
    //----------------------------------------------------------------------------------------------

    //----------------------------- Initialize Facebook --------------------------------------------
    private void initFacebook(){
        mCallbackManager = CallbackManager.Factory.create();
    }

    // ---------------------------- Initialize Facebook login button -------------------------------
    private void clickFacebookLoginButton(){
        mFacebookLoginButton = findViewById(R.id.facebook_login_button);
        mFacebookLoginButton.setReadPermissions("email", "public_profile");
        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                Toasty.success(LoginRegisterActivity.this, getString(R.string.connection_succeed), Toasty.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel:");
                Toasty.warning(LoginRegisterActivity.this, getString(R.string.error_authentication_canceled), Toasty.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError:" + error);
                Toasty.error(LoginRegisterActivity.this, getString(R.string.connection_failed), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    //----------------------------- Handle Facebook result  ----------------------------------------
    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "signInWithCredential:success");
                            Toasty.success(LoginRegisterActivity.this, getString(R.string.connection_succeed), Toasty.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else{
                            Log.w(TAG, "signInWithCredential:failure" + task.getException());
                            Toasty.error(LoginRegisterActivity.this, getString(R.string.connection_failed), Toasty.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- TWITTER LOGIN --------------------------------------------------
    //----------------------------------------------------------------------------------------------

    // ---------------------------- Initialize Twitter ---------------------------------------------
    private void initTwitter(){
        TwitterAuthConfig mTwitterAuthConfig = new TwitterAuthConfig(getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(mTwitterAuthConfig)
                .build();
        Twitter.initialize(twitterConfig);
    }
}
