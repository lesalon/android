package com.lesalon.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.lesalon.R;
import com.lesalon.chat.ChatActivity;

public class GalleryActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInResult result;
    private EditText mEnterChat;
    private Button mChatUser;
    private TextView mStatusTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mEnterChat = (EditText) findViewById(R.id.enterChat);
        mChatUser = (Button) findViewById(R.id.chat_user);
        mChatUser.setOnClickListener(this);
        mStatusTextView = (TextView) findViewById(R.id.textView);

    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d("Gallary Avtivity", "Got cached sign-in");
            result = opr.get();

            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                String name = acct.getDisplayName();
                mStatusTextView.setText(name);
            }
            //handleSignInResult(result);
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Gallery Activity", "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.chat_user:
                String chatUser = mEnterChat.getText().toString();
                if(chatUser.length() > 0) {
                    GoogleSignInAccount acct = result.getSignInAccount();
                    Intent chatIntent = ChatActivity.getIntent(this,
                            acct.getEmail(), chatUser, acct.getDisplayName());
                    startActivity(chatIntent);
                }
                break;
        }
    }
}