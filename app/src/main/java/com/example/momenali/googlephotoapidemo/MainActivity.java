package com.example.momenali.googlephotoapidemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.momenali.googlephotoapidemo.photo.GooglePhotoClient;
import com.example.momenali.googlephotoapidemo.photo.PhotoInfo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://photoslibrary.googleapis.com";
    private static final String CLIENT_ID = "971199180410-9gjapeombij1kovop75kkpcrtd4hfr9f.apps.googleusercontent.com";
    private static final String SECRET_ID = "";
    private static final String REDIRECT_URL = "googlephoto://callback";

    private static final String TAG = "MainActivity";
    // using betterKnife to bind the views
    @BindView(R.id.sign_in_button)
    SignInButton mSignInButton;

    @BindView(R.id.tv_json_result)
    TextView tvJsinResult;

    @BindView(R.id.sign_in_container)
    FrameLayout signInContainer;

    @BindView(R.id.main_container)
    LinearLayout mainContainer;

    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Scope[] scopes = new Scope[]{
                new Scope("https://www.googleapis.com/auth/photoslibrary")
                //, new Scope("https://www.googleapis.com/auth/photoslibrary.readonly"),
                //new Scope("https://www.googleapis.com/auth/photoslibrary.readonly.appcreateddata"),

        };

        GoogleSignInOptions myOptions = (new GoogleSignInOptions.Builder()).requestScopes(new Scope("https://www.googleapis.com/auth/drive.photos.readonly"), scopes).build();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(myOptions)
                .requestEmail()
                .requestIdToken(CLIENT_ID)
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mSignInButton.setSize(SignInButton.SIZE_WIDE);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void updateUI(GoogleSignInAccount account) {

        if (account!=null){

            signInContainer.setVisibility(View.GONE);
            tvJsinResult.setVisibility(View.VISIBLE);

            Log.d(TAG, "updateUI: "+account.getDisplayName());
            Log.d(TAG, "updateUI: "+account.getIdToken());

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder.build();

            GooglePhotoClient client = retrofit.create(GooglePhotoClient.class);

            Map<String, String> map = new HashMap<>();
            map.put("Content-type", "application/json");
            map.put("Authorization","Bearer "+account.getIdToken());


            Call<List<PhotoInfo>> call = client.fetchLibraryContents(map);

            call.enqueue(new Callback<List<PhotoInfo>>() {
                @Override
                public void onResponse(Call<List<PhotoInfo>> call, Response<List<PhotoInfo>> response) {

                }

                @Override
                public void onFailure(Call<List<PhotoInfo>> call, Throwable t) {
                    Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_LONG).show();
                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                handleSignInResult(task);

        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)  {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
