package com.example.administrator.facebookdemoapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity {


    public static final String USER_PROFILE1 = "http://graph.facebook.com/";// 1420370681588319+
    public static final String USER_PROFILE2 = "/picture?type=large";


    public static final String grap_api = "https://graph.facebook.com/me/picture?access_token=";

    private CallbackManager callbackManager;
    private TextView textView;

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {


            // App code
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            // Application code
                            Log.v("LoginActivity", response.toString());

                            String email = object.optString("email");

                            Profile profile = Profile.getCurrentProfile();
                            String firstName = profile.getFirstName();
                            System.out.println(profile.getProfilePictureUri(20, 20));
                            System.out.println(profile.getLinkUri());


                            String profilepic = USER_PROFILE1+""+profile.getId()+""+USER_PROFILE2;

//                            URL image_value = new URL(profilepic);

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields","id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        callbackManager = CallbackManager.Factory.create();




        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

                System.out.println("Access_token  ==   "+newToken.getApplicationId()+"   "+
                        newToken.getToken()+"      "+newToken.getUserId()+"       "+newToken.getPermissions());

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

                Profile.fetchProfileForCurrentAccessToken(); //manually force profile fetching from current token
                if(Profile.getCurrentProfile() != null) { //if it available
                    Log.i("PROFILE", Profile.getCurrentProfile().getName() + ", " + Profile.getCurrentProfile().getId() + ", " + Profile.getCurrentProfile().getLinkUri());
                } else {
                    Log.i("PROFILE", "Profile is null");
//                    showLoginActivity(); //no profile - login again
                }


                displayMessage(newProfile);

                System.out.println("Profile data  ==   " + newProfile.getId() + "   " +
                        newProfile.getFirstName()+ "      " +newProfile.getProfilePictureUri(50, 50)+ "       "
                        +newProfile.getLinkUri());


            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();


        loginButton.setReadPermissions(Arrays.asList("user_likes", "user_friends", "user_status", "public_profile", "email"/*,
                "read_friendlists"*/));
//        loginButton.setReadPermissions("user_friends"/*,"user_likes","user_status","public_profile","email","read_friendlists"*/);
//        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, callback);


        getHashCode();

    }


    @Override
    public void onStart() {
        super.onStart();

//        Session session = Session.getActiveSession();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
    }



    private void displayMessage(Profile profile){
        if(profile != null){
//            textView.setText(profile.getName());
        }
    }


    // This code used for generating SHA1 key which is used in facebook app
    // project.
    void getHashCode() {

        try {

            PackageInfo info = getPackageManager().getPackageInfo("com.example.administrator.facebookdemoapp", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                String hashval = Base64.encodeToString(md.digest(), Base64.DEFAULT);

                Log.i("SHA1 - Hashkey ===   ",hashval);

            }

        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }


}
