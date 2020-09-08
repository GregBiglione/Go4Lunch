package com.greg.go4lunch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

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
import com.google.firebase.auth.TwitterAuthProvider;
import com.greg.go4lunch.ui.home.HomeFragment;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
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

    @BindView(R.id.twitter_login_button) TwitterLoginButton mTwitterLoginButton;
    private ProgressBar mProgressBar;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @BindView(R.id.email_login_button) Button mEmailLoginButton;

    @BindView(R.id.language_button) Button mLanguageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        fireBaseAuth();
        clickGoogleSignInButton();
        googleSignInConfigure();

        initFacebook();
        clickFacebookLoginButton();

        initTwitter();
        handleTwitterAccess();
        goToHomeFragment();

        clickEmailLogInButton();

        clickChangeLanguageButton();
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
        mProgressDialog.setMessage(getString(R.string.wait_message));
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
            handleEmailAccess(requestCode, resultCode, data);
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
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

    // ---------------------------- Get Google/FB/Twitter user information -------------------------
    private void updateUI(FirebaseUser user) {
        if (user != null){
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    // ---------------------------- Check if user is already logged with Google/FB/Twitter ---------
    @Override
    protected void onStart() {
        super.onStart();
        //FirebaseUser user = mAuth.getCurrentUser();
        //updateUI(user);
        loggedUser();
    }

    private void loggedUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    // ---------------------------- FACEBOOK AUTHENTICATION ----------------------------------------------------------------------------------------------------------

    // ---------------------------- Initialize Facebook --------------------------------------------
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

    // ---------------------------- TWITTER AUTHENTICATION ------------------------------------------------------------------------------------------------------------

    // ---------------------------- Initialize Twitter ---------------------------------------------
    private void initTwitter(){
        TwitterAuthConfig mTwitterAuthConfig = new TwitterAuthConfig(getString(R.string.consumer_key), getString(R.string.consumer_secret));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(mTwitterAuthConfig)
                .build();
        Twitter.initialize(twitterConfig);
        showTwitterButton();
    }

    // ---------------------------- Show Twitter Login button --------------------------------------
    private void showTwitterButton(){
        setContentView(R.layout.activity_login_register);
    }

    // ---------------------------- 3) Handle Twitter result ---------------------------------------
    private void handleTwitterAccess(){
        mTwitterLoginButton = findViewById(R.id.twitter_login_button);
        mProgressBar = findViewById(R.id.indeterminateProgressBar);

        mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toasty.success(LoginRegisterActivity.this, "Login Twitter successful", Toasty.LENGTH_SHORT).show();
                signToFireBaseWithTwitterSession(result.data);
                mProgressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            }

            @Override
            public void failure(TwitterException exception) {
                Toasty.error(LoginRegisterActivity.this, "Login with Twitter failed", Toasty.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                updateUI(null);
            }
        });
    }

    // ---------------------------- Connection to Firebase with Twitter ----------------------------
    private void signToFireBaseWithTwitterSession(TwitterSession data) {
        AuthCredential credential = TwitterAuthProvider.getCredential(data.getAuthToken().token, data.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toasty.success(LoginRegisterActivity.this, "Sign in with Twitter successfully", Toasty.LENGTH_SHORT).show();
                        if (!task.isSuccessful()){
                            Toasty.error(LoginRegisterActivity.this, "Sign in with Twitter failed", Toasty.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // ---------------------------- Redirection to Home Fragment -----------------------
    private void goToHomeFragment(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    Intent twitterIntent = new Intent(LoginRegisterActivity.this, HomeFragment.class);
                    startActivity(twitterIntent);
                }
            }
        };
    }

    // ---------------------------- EMAIL AUTHENTICATION --------------------------------------------------------------------------------------------------------------

    // ---------------------------- Click on Email Sign In button ----------------------------------
    private void clickEmailLogInButton(){
        mEmailLoginButton = findViewById(R.id.email_login_button);
        mEmailLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivity();
            }
        });
    }

    // ---------------------------- Launch email authentication ------------------------------------
    private void startSignInActivity() {
        List<AuthUI.IdpConfig> provider = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
        startActivityForResult(
                AuthUI.getInstance()
                .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                .setAvailableProviders(provider)
                        .setIsSmartLockEnabled(false, true)
                .build(),
                RC_SIGN_IN
        );
    }

    // ---------------------------- Handle response after sign in ----------------------------------
    private void handleEmailAccess(int requestCode, int resultCode, Intent data){
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN){
            if (resultCode == RESULT_OK){
                Toasty.success(LoginRegisterActivity.this, "Login with email successfully", Toasty.LENGTH_SHORT).show();
            }
            else{
                if (response == null){
                    Toasty.info(LoginRegisterActivity.this, "Sign in with email canceled", Toasty.LENGTH_SHORT).show();
                }
                else if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                    Toasty.error(LoginRegisterActivity.this, "No internet", Toasty.LENGTH_SHORT).show();
                }
                else if(response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR){
                    Toasty.error(LoginRegisterActivity.this, "Unknown error", Toasty.LENGTH_SHORT).show();
                }
            }
        }
    }

    //private void handleEmailAccess(int resultCode, Intent data){
    //    IdpResponse response = IdpResponse.fromResultIntent(data);
//
    //    if (resultCode == RESULT_OK){
    //        Toasty.success(LoginRegisterActivity.this, "Login with email successfully", Toasty.LENGTH_SHORT).show();
    //    }
    //    else{
    //        if (response == null){
    //            Toasty.info(LoginRegisterActivity.this, "Sign in with email canceled", Toasty.LENGTH_SHORT).show();
    //        }
    //        else if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
    //            Toasty.error(LoginRegisterActivity.this, "No internet", Toasty.LENGTH_SHORT).show();
    //        }
    //        else if(response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR){
    //            Toasty.error(LoginRegisterActivity.this, "Unknown error", Toasty.LENGTH_SHORT).show();
    //        }
    //    }
    //}

    // ---------------------------- MULTI LANGUAGES ----------------------------------------------------------------------------------------------------------
    public void clickChangeLanguageButton(){
        mLanguageButton = findViewById(R.id.language_button);
        mLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLanguagesDialog();
            }
        });
    }

    private void openLanguagesDialog() {
        LanguagesDialog languagesDialog = new LanguagesDialog();
        languagesDialog.show(getSupportFragmentManager(), "languages dialog");
    }
}
