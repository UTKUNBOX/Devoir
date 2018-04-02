package com.kiit.viper.devoir;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kiit.viper.devoir.model.User;

/**
 * Created by Sukesh Panwar on 28-03-2017.
 */

public class Login extends AppCompatActivity{

    private SignInButton googleButton;
    private ProgressDialog mProgress;

    private static final int RC_SIGN_IN=1;

    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private static DatabaseReference mDatabase;
    FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener mAuthListner;

    private static final String TAG="LOGIN_ACT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth= FirebaseAuth.getInstance();

        mAuthListner=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                fUser = firebaseAuth.getCurrentUser();
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    startActivity(new Intent(Login.this, MainActivity.class));
                    //finish();
                }
            }
        };

        googleButton= (SignInButton) findViewById(R.id.googleButton);
        mProgress=new ProgressDialog(this);
        mProgress.setMessage("Signing In...");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient=new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(Login.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable())
                {
                    signIn();
                }
                else
                {
                    Toast.makeText(Login.this,"Internet not connected. Plz try again.",Toast.LENGTH_SHORT).show();
                }
            }
        });

       /* mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    // User is signed in
//                            shortRoidPreferences.setPrefString("email",user.getEmail());
//                            shortRoidPreferences.setPrefString("name",user.getDisplayName());
                    User u=new User();

                            for(UserInfo profile:user.getProviderData()) {
                                //for navigation Header Details
                                nvEmail = user.getEmail();
                                nvUserName = profile.getDisplayName();
                                nvProfilePhotoUri = profile.getPhotoUrl();
                            }
                    u.setEmail(user.getEmail());
                    u.setName(user.getDisplayName());
                    //u.setFcm(shortRoidPreferences.getPrefString("token"));
                    // shortRoidPreferences.setPrefBoolean("temp_forward",false);
                    database = FirebaseDatabase.getInstance();
                    mDatabase=database.getReference("users");
                    mDatabase.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            User cur_usr = dataSnapshot.getValue(User.class);
                            if(cur_usr.getEmail().equals(user.getEmail())){
                                mDatabase.child(dataSnapshot.getKey()).child("fcm").setValue(FirebaseInstanceId.getInstance().getToken());
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    if(!shortRoidPreferences.getPrefBoolean("logged_in"))
                        FireBaseUtil.addUserToDataBase(SignInActivity.this,u);

                    else
                    {
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        SignInActivity.this.finish();
                    }

                }
                else
                {
                    // User is signed out


                }
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }
        };*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }



    private void signIn() {
        mProgress.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if(task.isSuccessful()){
                            String id1 = mAuth.getCurrentUser().getUid();
                           // final FirebaseUser user=firebaseAuth.getCurrentUser();
                            //For adding into database

                            //mDatabase = FirebaseDatabase.getInstance().getReference("users");
                            //child("users").child(id1);
                            //mDatabase.child(id1).setValue(getComponentName());
                            /*User newUser = new User();
                            newUser.setEmail(fUser.getEmail());
                            newUser*/
                            DatabaseReference mDBRef=FirebaseDatabase.getInstance().getReference("users").child(id1);

                            mDBRef.child("Name").setValue(mAuth.getCurrentUser().getDisplayName());
                            mDBRef.child("email").setValue(fUser.getEmail());
                            mDBRef.child("userID").setValue(id1);
                            mDBRef.child("issueIDs").setValue(",");

                            Toast.makeText(getApplicationContext(), "Employee Added Successfully", Toast.LENGTH_SHORT).show();
                        }

                        else{
                            Toast.makeText(getApplicationContext(), "Please try Again", Toast.LENGTH_SHORT).show();
                        }
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    public boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager)
                getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
