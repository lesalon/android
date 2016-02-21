package com.lesalon.gallery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.lesalon.BookInfo;
import com.lesalon.R;
import com.lesalon.chat.ChatActivity;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInResult result;
    private EditText mEnterChat;
    private Button mChatUser;
    private TextView mStatusTextView;
    private GridView mGenreGrid;
    
    private static List<GalleryImageAdapter.GridCell> gridCells;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGenreGrid = (GridView) findViewById(R.id.imgGrid);
        if(gridCells == null) {
            gridCells = createGridCells(this);
        }
        GalleryImageAdapter adapter = new GalleryImageAdapter(this,gridCells);
        mGenreGrid.setAdapter(adapter);
        setGridItem();
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

    private void setGridItem() {
        mGenreGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryImageAdapter.GridCell cell = gridCells.get(position);
                switch(cell.imgSrc) {
                    case R.mipmap.scifi:

                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(GalleryActivity.this);

                        builderSingle.setTitle("Select Book");

                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(

                                GalleryActivity.this,android.R.layout.select_dialog_singlechoice);

                        arrayAdapter.add("Dune - Frank Herert");

                        arrayAdapter.add("The Book of the New Sun - Gene Wolfe");

                        arrayAdapter.add("The Moon is a Harsh Mistress - Robert A. Heinlein");

                        arrayAdapter.add("The Dispossessed - Ursula K. Le Guin");

                        arrayAdapter.add("Hyperion - Dan Simmons");

                        builderSingle .setAdapter(

                                arrayAdapter,

                                new DialogInterface.OnClickListener(){

                                    @Override

                                    public void onClick(DialogInterface dialog, int which) {

                                        String strName = arrayAdapter.getItem(which);

                                        Toast.makeText(getApplicationContext(), strName,Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(getApplicationContext(), BookInfo.class);

                                        i.putExtra(strName, 10);

                                        startActivity(i);

                                    }

                                }

                        );
                        builderSingle.create().show();
                        break;

                    case R.mipmap.comics:

                        AlertDialog.Builder builderSingle1 = new AlertDialog.Builder(GalleryActivity.this);

                        builderSingle1.setTitle("Select Book");

                        final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(

                                GalleryActivity.this,android.R.layout.select_dialog_singlechoice);

                        arrayAdapter1.add("Maus - Art Spiegelman");

                        arrayAdapter1.add("The Dark Night Returns - Frank Miller");

                        arrayAdapter1.add("V for Vendetta - Alan Moore");

                        arrayAdapter1.add("Sin City - Frank Miller");

                        arrayAdapter1.add("Ghost World - Dan Clowes");

                        builderSingle1 .setAdapter(

                                arrayAdapter1,

                                new DialogInterface.OnClickListener(){

                                    @Override

                                    public void onClick(DialogInterface dialog, int which) {

                                        String strName = arrayAdapter1.getItem(which);

                                        Toast.makeText(getApplicationContext(), strName,Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(getApplicationContext(), BookInfo.class);

                                        i.putExtra(strName, 10);

                                        startActivity(i);

                                    }

                                }

                        );
                        builderSingle1.create().show();
                        break;

                    case R.mipmap.drama:

                        AlertDialog.Builder builderSingle2 = new AlertDialog.Builder(GalleryActivity.this);

                        builderSingle2.setTitle("Select Book");

                        final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(

                                GalleryActivity.this,android.R.layout.select_dialog_singlechoice);

                        arrayAdapter2.add("Romeo and Juliet - William Shakespeare");

                        arrayAdapter2.add("Hamlet -William Shakespeare");

                        arrayAdapter2.add("The Crucible - Arthur Miller");

                        arrayAdapter2.add("The Importance of Being Earnest - Oscar Wilde");

                        arrayAdapter2.add("A Streetcar named desire - Tennessee Williams");

                        builderSingle2 .setAdapter(

                                arrayAdapter2,

                                new DialogInterface.OnClickListener(){

                                    @Override

                                    public void onClick(DialogInterface dialog, int which) {

                                        String strName = arrayAdapter2.getItem(which);

                                        Toast.makeText(getApplicationContext(), strName,Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(getApplicationContext(), BookInfo.class);

                                        i.putExtra(strName, 10);

                                        startActivity(i);

                                    }

                                }

                        );
                        builderSingle2.create().show();
                        break;

                    case R.mipmap.fantasy:

                        AlertDialog.Builder builderSingle3 = new AlertDialog.Builder(GalleryActivity.this);

                        builderSingle3.setTitle("Select Book");

                        final ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(

                                GalleryActivity.this,android.R.layout.select_dialog_singlechoice);

                        arrayAdapter3.add("The Lord of the Rings - J. R. R. Tolkien");

                        arrayAdapter3.add("A Game of Thrones - George R. R. Martin");

                        arrayAdapter3.add("The Hobbit - J. R. R. Tolkien");

                        arrayAdapter3.add("Harry Porter and the Sorcerer's Stone - J. K. Rowling");

                        arrayAdapter3.add("Harry Porter and the Sorcerer's Stone - J. K. Rowling");

                        builderSingle3 .setAdapter(

                                arrayAdapter3,

                                new DialogInterface.OnClickListener(){

                                    @Override

                                    public void onClick(DialogInterface dialog, int which) {

                                        String strName = arrayAdapter3.getItem(which);

                                        Toast.makeText(getApplicationContext(), strName,Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(getApplicationContext(), BookInfo.class);

                                        i.putExtra(strName, 10);

                                        startActivity(i);

                                    }

                                }

                        );
                        builderSingle3.create().show();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private static List<GalleryImageAdapter.GridCell> createGridCells(Context context) {
        Resources res = context.getResources();
        String[] names = res.getStringArray(R.array.genre);
        int[] images = {R.mipmap.tech,R.mipmap.scifi,R.mipmap.comics,R.mipmap.histfic,R.mipmap
                .scifi,R.mipmap.fantasy,R.mipmap.drama,R.mipmap.poetry,R.mipmap.scifi,
                R.mipmap.scifi,R.mipmap.scifi,R.mipmap.fables,R.mipmap.mythology,
                R.mipmap.tech,R.mipmap.scifi,R.mipmap.scifi,R.mipmap.scifi,
                R.mipmap.fables,R.mipmap.mythology};
        List<GalleryImageAdapter.GridCell> gridList = new ArrayList<>();
        for(int i = 0; i < names.length; i++) {
            gridList.add(new GalleryImageAdapter.GridCell(images[i], names[i]));
        }

        return gridList;
    }
}