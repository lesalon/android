package com.lesalon.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.lesalon.Constants;
import com.lesalon.MainActivity;
import com.lesalon.R;
import com.lesalon.model.ChatMessage;
import com.pubnub.api.Callback;
import com.pubnub.api.PnGcmMessage;
import com.pubnub.api.PnMessage;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private Pubnub mPubNub;
//    private Button mChannelView;
    private EditText mMessageET;
    private ListView mListView;
    private ChatAdapter mChatAdapter;
    private SharedPreferences mSharedPrefs;

    private String currentUserId;
    private String currentUserName;
    private String channel;

    private GoogleCloudMessaging gcm;
    private String gcmRegId;

    private static final String CHAT_USERS_CURRENT = "chat_user";
    private static final String CURRENT_USER_ID = "current_id";

    private static final String TAG = ChatActivity.class.toString();

    public static Intent getIntent(Context context, String currentUserId, String secondUserId,
                                   String currentUser) {
        String[] chatRoom = {currentUserId, secondUserId};
        Arrays.sort(chatRoom);
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.CHAT_ROOM, chatRoom[0] + chatRoom[1]);
        intent.putExtra(CHAT_USERS_CURRENT, currentUser);
        intent.putExtra(CURRENT_USER_ID, currentUserId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initUsers(getIntent(), savedInstanceState);
        mSharedPrefs = getSharedPreferences(Constants.CHAT_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPrefs.edit();
        edit.putString(Constants.CHAT_USERNAME, currentUserId);
        edit.apply();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!mSharedPrefs.contains(Constants.CHAT_USERNAME)){
            finish();
        }
        this.mListView = (ListView) findViewById(R.id.list);
        this.mChatAdapter = new ChatAdapter(this, new ArrayList<ChatMessage>());
        this.mChatAdapter.userPresence(this.currentUserId, "join"); // Set user to online. Status changes handled in presence
        setupAutoScroll();
        this.mListView.setAdapter(mChatAdapter);
        setupListView();

        this.mMessageET = (EditText) findViewById(R.id.message_et);
//        this.mChannelView = (Button) findViewById(R.id.channel_bar);
//        this.mChannelView.setText(this.channel);

        initPubNub();
    }

    private void initUsers(Intent newIntent, Bundle savedInstance) {
        if(savedInstance != null) {
            channel = savedInstance.getString(Constants.CHAT_ROOM);
            currentUserId = savedInstance.getString(CURRENT_USER_ID);
            currentUserName = savedInstance.getString(CHAT_USERS_CURRENT);
        } else if(newIntent != null) {
            channel = newIntent.getStringExtra(Constants.CHAT_ROOM);
            currentUserId = newIntent.getStringExtra(CHAT_USERS_CURRENT);
            currentUserName = newIntent.getStringExtra(CHAT_USERS_CURRENT);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.CHAT_ROOM, channel);
        outState.putString(CURRENT_USER_ID, currentUserId);
        outState.putString(CHAT_USERS_CURRENT, currentUserName);
    }

    /**
     * Might want to unsubscribe from PubNub here and create background service to listen while
     *   app is not in foreground.
     * PubNub will stop subscribing when screen is turned off for this demo, messages will be loaded
     *   when app is opened through a call to history.
     * The best practice would be creating a background service in onStop to handle messages.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (this.mPubNub != null)
            this.mPubNub.unsubscribeAll();
    }

    /**
     * Instantiate PubNub object if it is null. Subscribe to channel and pull old messages via
     *   history.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (this.mPubNub==null){
            initPubNub();
        } else {
            subscribeWithPresence();
            history();
        }
    }

    /**
     * I remove the PubNub object in onDestroy since turning the screen off triggers onStop and
     *   I wanted PubNub to receive messages while the screen is off.
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Instantiate PubNub object with currentUserId as UUID
     *   Then subscribe to the current channel with presence.
     *   Finally, populate the listview with past messages from history
     */
    private void initPubNub(){
        this.mPubNub = new Pubnub(Constants.PUBLISH_KEY, Constants.SUBSCRIBE_KEY);
        this.mPubNub.setUUID(this.currentUserId);
        subscribeWithPresence();
        history();
        gcmRegister();
    }

    /**
     * Use PubNub to send any sort of data
     * @param type The type of the data, used to differentiate groupMessage from directMessage
     * @param data The payload of the publish
     */
    public void publish(String type, JSONObject data){
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("data", data);
        } catch (JSONException e) { e.printStackTrace(); }

        this.mPubNub.publish(this.channel, json, new BasicCallback());
    }

    /**
     * Called at login time, sets meta-data of users' log-in times using the PubNub State API.
     *   Information is retrieved in getStateLogin
     */
    public void setStateLogin(){
        Callback callback = new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                Log.d(TAG, "State: " + response.toString());
            }
        };
        try {
            JSONObject state = new JSONObject();
            state.put(Constants.STATE_LOGIN, System.currentTimeMillis());
            this.mPubNub.setState(this.channel, this.mPubNub.getUUID(), state, callback);
        }
        catch (JSONException e) { e.printStackTrace(); }
    }

    /**
     * Subscribe to channel, when subscribe connection is established, in connectCallback, subscribe
     *   to presence, set login time with setStateLogin and update hereNow information.
     * When a message is received, in successCallback, get the ChatMessage information from the
     *   received JSONObject and finally put it into the listview's ChatAdapter.
     * Chat adapter calls notifyDatasetChanged() which updates UI, meaning must run on UI thread.
     */
    public void subscribeWithPresence(){
        Callback subscribeCallback = new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                if (message instanceof JSONObject){
                    try {
                        JSONObject jsonObj = (JSONObject) message;
                        JSONObject json = jsonObj.getJSONObject("data");
                        String name = json.getString(Constants.JSON_USER);
                        String msg  = json.getString(Constants.JSON_MSG);
                        long time   = json.getLong(Constants.JSON_TIME);
                        if (name.equals(mPubNub.getUUID())) return; // Ignore own messages
                        final ChatMessage chatMsg = new ChatMessage(name, msg, time);
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mChatAdapter.addMessage(chatMsg);
                            }
                        });
                    } catch (JSONException e){ e.printStackTrace(); }
                }
                Log.d(TAG, "Channel: " + channel + " Msg: " + message.toString());
            }

            @Override
            public void connectCallback(String channel, Object message) {
                Log.d(TAG, "Connected! " + message.toString());
                setStateLogin();
            }
        };
        try {
            mPubNub.subscribe(this.channel, subscribeCallback);
        } catch (PubnubException e){ e.printStackTrace(); }
    }

    /**
     * Get last 100 messages sent on current channel from history.
     */
    public void history(){
        this.mPubNub.history(this.channel,100,false,new Callback() {
            @Override
            public void successCallback(String channel, final Object message) {
                try {
                    JSONArray json = (JSONArray) message;
                    Log.d(TAG, json.toString());
                    JSONArray messages = json.getJSONArray(0);
                    final List<ChatMessage> chatMsgs = new ArrayList<>();
                    for (int i = 0; i < messages.length(); i++) {
                        try {
                            if (!messages.getJSONObject(i).has("data")) continue;
                            JSONObject jsonMsg = messages.getJSONObject(i).getJSONObject("data");
                            String name = jsonMsg.getString(Constants.JSON_USER);
                            String msg = jsonMsg.getString(Constants.JSON_MSG);
                            long time = jsonMsg.getLong(Constants.JSON_TIME);
                            ChatMessage chatMsg = new ChatMessage(name, msg, time);
                            chatMsgs.add(chatMsg);
                        } catch (JSONException e) { // Handle errors silently
                            e.printStackTrace();
                        }
                    }

                    ChatActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Chat message");
                            mChatAdapter.setMessages(chatMsgs);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.d(TAG, error.toString());
            }
        });
    }

    /**
     * Log out, remove currentUserId from SharedPreferences, unsubscribe from PubNub, and send user back
     *   to the LoginActivity
     */
    public void signOut(){
        this.mPubNub.unsubscribeAll();
        SharedPreferences.Editor edit = mSharedPrefs.edit();
        edit.remove(Constants.CHAT_USERNAME);
        edit.apply();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("oldUsername", this.currentUserId);
        startActivity(intent);
    }

    /**
     * Setup the listview to scroll to bottom anytime it receives a message.
     */
    private void setupAutoScroll(){
        this.mChatAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mListView.setSelection(mChatAdapter.getCount() - 1);
                // mListView.smoothScrollToPosition(mChatAdapter.getCount()-1);
            }
        });
    }

    /**
     * On message click, display the last time the user logged in.
     */
    private void setupListView(){
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatMessage chatMsg = mChatAdapter.getItem(position);
                sendNotification(chatMsg.getUsername());
            }
        });
    }

    /**
     * Publish message to current channel.
     * @param view The 'SEND' Button which is clicked to trigger a sendMessage call.
     */
    public void sendMessage(View view){
        String message = mMessageET.getText().toString().trim();
        if (message.equals("")) return;
        mMessageET.setText("");
        ChatMessage chatMsg = new ChatMessage(currentUserName, message, System.currentTimeMillis());
        try {
            JSONObject json = new JSONObject();
            json.put(Constants.JSON_USER, chatMsg.getUsername());
            json.put(Constants.JSON_MSG,  chatMsg.getMessage());
            json.put(Constants.JSON_TIME, chatMsg.getTimeStamp());
            publish(Constants.JSON_GROUP, json);
        } catch (JSONException e){ e.printStackTrace(); }
        mChatAdapter.addMessage(chatMsg);
    }

    /**
     * Create an alert dialog with a text view to enter a new channel to join. If the channel is
     *   not empty, unsubscribe from the current channel and join the new one.
     *   Then, get messages from history and update the channelView which displays current channel.
     * @param view
     */
    public void changeChannel(View view){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.channel_change, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        userInput.setText(this.channel);                       // Set text to current ID
        userInput.setSelection(userInput.getText().length());  // Move cursor to end

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String newChannel = userInput.getText().toString();
                                if (newChannel.equals("")) return;

                                mPubNub.unsubscribe(channel);
                                mChatAdapter.clearMessages();
                                channel = newChannel;
//                                mChannelView.setText(channel);
                                subscribeWithPresence();
                                history();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * GCM Functionality.
     * In order to use GCM Push notifications you need an API key and a Sender ID.
     * Get your key and ID at - https://developers.google.com/cloud-messaging/
     */

    private void gcmRegister() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            try {
                gcmRegId = getRegistrationId();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (gcmRegId.isEmpty()) {
                registerInBackground();
            } else {
                Log.d(TAG, "Registration ID already exists: " + gcmRegId);
            }
        } else {
            Log.e("GCM-register", "No valid Google Play Services APK found.");
        }
    }

    private void gcmUnregister() {
        new UnregisterTask().execute();
    }

    private void removeRegistrationId() {
        SharedPreferences prefs = getSharedPreferences(Constants.CHAT_PREFS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.GCM_REG_ID);
        editor.apply();
    }

    public void sendNotification(String toUser) {
        PnGcmMessage gcmMessage = new PnGcmMessage();
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.GCM_POKE_FROM, this.currentUserId);
            json.put(Constants.GCM_CHAT_ROOM, this.channel);
            gcmMessage.setData(json);

            PnMessage message = new PnMessage(
                    this.mPubNub,
                    toUser,
                    new BasicCallback(),
                    gcmMessage);
            message.put("pn_debug",true); // Subscribe to yourchannel-pndebug on console for reports
            message.publish();
        }
        catch (JSONException e) { e.printStackTrace(); }
        catch (PubnubException e) { e.printStackTrace(); }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e("GCM-check", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {
        new RegisterTask().execute();
    }

    private void storeRegistrationId(String regId) {
        SharedPreferences prefs = getSharedPreferences(Constants.CHAT_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.GCM_REG_ID, regId);
        editor.apply();
    }


    private String getRegistrationId() {
        SharedPreferences prefs = getSharedPreferences(Constants.CHAT_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(Constants.GCM_REG_ID, "");
    }

    private void sendRegistrationId(String regId) {
        this.mPubNub.enablePushNotificationsOnChannel(this.currentUserId, regId, new BasicCallback());
    }

    private class RegisterTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(ChatActivity.this);
                }
                gcmRegId = gcm.register(Constants.GCM_SENDER_ID);
                sendRegistrationId(gcmRegId);
                storeRegistrationId(gcmRegId);
                Log.i("GCM-register", "Device registered, registration ID: " + gcmRegId);
            } catch (IOException e){
                e.printStackTrace();
            }
            return gcmRegId;
        }
    }

    private class UnregisterTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(ChatActivity.this);
                }

                // Unregister from GCM
                gcm.unregister();

                // Remove Registration ID from memory
                removeRegistrationId();

                // Disable Push Notification
                mPubNub.disablePushNotificationsOnChannel(currentUserId, gcmRegId);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
