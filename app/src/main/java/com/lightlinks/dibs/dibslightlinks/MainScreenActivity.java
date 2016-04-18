package com.lightlinks.dibs.dibslightlinks;

/**
 * Created by NickHome on 4/14/16.
 */
//import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
//import android.support.v4.app.FragmentActivity;
//import android.support.v7.app.AlertDialog;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.*;
import android.support.v7.widget.Toolbar;
import android.util.Log;
//import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
//import android.view.inputmethod.EditorInfo;
//import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import java.io.InputStream;
import java.io.OutputStream;

public class MainScreenActivity extends AppCompatActivity{
    /**
     * Variables used in MainActivity
     */
    private static final String TAG = "MainActivity";
    private BluetoothSocket mSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private String mConnectedDeviceName;
    private TextView textView;
    private ListView messages;
    private EditText outEditText;
    private ArrayAdapter<String> messageArrayAdapter;
    private StringBuffer outgoingStringBuffer;
    private BluetoothChatService mChatService = null;
    //private Button sendButton;
    private ImageButton standardPathwayButton;
    private ImageButton powerSavePathwayButton;
    private ImageButton partyModeButton;
    private ImageButton crowdModeButton;
    private ImageButton sleepModeButton;
    private ImageButton securityButton;
    private static final int REQUEST_BLUETOOTH=1;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 2;
    private int LED_red;
    private int LED_green;
    private int LED_blue;
    private String pattern;
    private String mode_default;
    private Color[] colors = new Color[4]; //Holds the color of the colorsets for party mode

    /**
     * Strings that will be sent as commands
     */
    private String cmd_standard_md;
    private String cmd_ps_md;
    private String cmd_party_md;
    private String cmd_crowd_md;
    private String cmd_sleep_md;
    private String cmd_security_md;
    private ColorPicker cp;
    private InputStream inputStream;
    private OutputStream outputStream;
    AppCompatActivity mActivity;

    SharedPreferences prefs;// = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    //ArrayAdapter<String> newDeviceArrayAdapter;
    Toolbar mainToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_mainscreen);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        standardPathwayButton = (ImageButton) findViewById(R.id.imageButton_standard_mode);
        powerSavePathwayButton = (ImageButton) findViewById(R.id.imageButton_powerSave_mode);
        partyModeButton = (ImageButton) findViewById(R.id.imageButton_party_mode);
        crowdModeButton = (ImageButton) findViewById(R.id.imageButton_crowd_mode);
        sleepModeButton = (ImageButton) findViewById(R.id.imageButton_sleep_mode);
        securityButton = (ImageButton) findViewById(R.id.imageButton_security_mode);


        //Check if first time opening the application
        Boolean isFirstRun = prefs.getBoolean("isFirstRun",true);

        if (isFirstRun){

            Toast.makeText(this,"Welcome to Dibs LightLinks System!",Toast.LENGTH_SHORT).show();
            //Update to not being the first run
            prefs.edit().putBoolean("isFirstRun",false).apply();
            //Set all the modes to default settings
            prefs.edit().putString("StandardModePref",getString(R.string.def_pathway_mode_cmnd)).apply();
            prefs.edit().putString("PSModePref",getString(R.string.def_ps_pathway_mode_cmd)).apply();
            prefs.edit().putString("PartyModePref",getString(R.string.def_party_mode_cmd)).apply();
            prefs.edit().putString("CrowdModePref",getString(R.string.def_crowd_mode_cmd)).apply();
            prefs.edit().putString("SleepModePref",getString(R.string.def_sleep_mode_cmd)).apply();
            prefs.edit().putString("SecurityModePref",getString(R.string.def_security_mode_cmd)).apply();
        }

        cp = new ColorPicker(this, 0,0,0);
        mainToolBar = (Toolbar) findViewById(R.id.main_toolbar);

        Log.d(TAG, "onCreate: About to try getting adapter");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d(TAG, "onCreate: Finished getting adapter");
        //Check if phone has bluetooth capablities
        if (mBluetoothAdapter == null){
            Toast.makeText(this, "Bluetooth not capable on this device, Closing..", Toast.LENGTH_LONG).show();
            this.finish();
        }
        setSupportActionBar(mainToolBar);

        mActivity = this;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.change_color:
                cp.show();
                Button updateColor = (Button)  cp.findViewById(R.id.button_update_color);
                if (updateColor!=null) {
                    updateColor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LED_red = cp.getRed();
                            LED_green = cp.getGreen();
                            LED_blue = cp.getBlue();
                            cp.dismiss();
                            String message = "LED "+LED_red +" "+ LED_green+ " "+LED_blue+" 10|";
                            sendMessage(message);
                        }
                    });
                }else {
                    Log.d(TAG, "onOptionsItemSelected: Got Null for update color");
                }
                break;
            case R.id.make_connection:
                if (!mBluetoothAdapter.isEnabled() ){
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, REQUEST_BLUETOOTH);
                }
                //create new intent for listing devices
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                break;

            case R.id.configure_mode:
                //create an intent to start the configure activity
                Intent configIntent = new Intent(this,ConfigureModeActivity.class);
                startActivity(configIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mBluetoothAdapter.isEnabled() ){
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_BLUETOOTH);
        }/*else if (mChatService==null){
            setupChat();
        }
        */

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mChatService!=null){
            if (mChatService.getState() == BluetoothChatService.STATE_NONE){
                mChatService.start();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Closing application!!!");
        if (mChatService!=null){
            mChatService.stop();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    //textView.setText(R.string.bt_device_found);
                    connectDevice(data, true);
                }
                break;
            case REQUEST_BLUETOOTH:
                if (resultCode == AppCompatActivity.RESULT_OK){
                    //BT Enabled
                    Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
                    setupChat();
                }
                else {
                    Toast.makeText(this, "Bluetooth not enabled, closing app...", Toast.LENGTH_SHORT).show();
                    mActivity.finish();
                }

        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        Log.d(TAG, "connectDevice: MAC Address of device is "+address);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);


        if (device ==null){
            Log.d(TAG, "connectDevice: Device not found with adapter!!");
            Toast.makeText(mActivity, "Device not found with adapter!!", Toast.LENGTH_SHORT).show();
            return;
        }

        setupChat();
        Log.d(TAG, "connectDevice: Attempting to connect to the device...");
        //Attempt to connect to the device
        mChatService.connect(device, secure);
        Log.d(TAG, "connectDevice: Connect to the device...");


    }


    private void setupChat(){
        Log.d(TAG, "setupChat: About to set up the chat session...");

        //Initialize the Standard Pathway ModeClass Button to send the commands for this mode
        standardPathwayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = prefs.getString("StandardModePref", getString(R.string.def_pathway_mode_cmnd));
                sendMessage(message);
            }
        });

        powerSavePathwayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = prefs.getString("PSModePref", getString(R.string.def_ps_pathway_mode_cmd));
                sendMessage(message);
            }
        });

        partyModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = prefs.getString("PartyModePref",getString(R.string.def_party_mode_cmd));
                sendMessage(message);
            }
        });

        crowdModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = prefs.getString("CrowdModePref",getString(R.string.def_crowd_mode_cmd));
                sendMessage(message);
            }
        });

        sleepModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = prefs.getString("SleepModePref",getString(R.string.def_sleep_mode_cmd));
                sendMessage(message);
            }
        });

        securityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = prefs.getString("SecurityModePref",getString(R.string.def_security_mode_cmd));
            }
        });

        mChatService = new BluetoothChatService(mActivity,mHandler);

        //outgoingStringBuffer = new StringBuffer("");
        Log.d(TAG, "setupChat: Made it to the end of function...this is a test!!");
    }

    private void sendMessage(String message){

        if (mChatService == null){
            Toast.makeText(this, "Not connected, please connect first",Toast.LENGTH_SHORT).show();
            return;
        }
        //Make sure actually connected first
        if (mChatService.getState()!= BluetoothChatService.STATE_CONNECTED){
            Toast.makeText(mActivity, R.string.not_connected,Toast.LENGTH_SHORT).show();
            return;
        }

        //Check that there is actually something to send
        if (message.length()>0){
            //Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            //Reset out string buffer to zero and clear the edit text field
            //outgoingStringBuffer.setLength(0);
            //outEditText.setText(""); //outEditText.setText(outgoingStringBuffer);
        }
    }

    /* Not needed anymore


    private TextView.OnEditorActionListener writeListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            //If the action is a key-up event on the return key, send message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP){
                String message = outEditText.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    */


    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */

    /*
    private void setStatus(int resId) {
        if (null == mActivity) {
            return;
        };
        if (mainToolBar == null)
            return;

        mainToolBar.setSubtitle(resId);
    }

    */
    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */

    /*
    private void setStatus(CharSequence subTitle) {

        if (null == mActivity) {
            return;
        }

        if (null == mainToolBar) {
            return;
        }
        mainToolBar.setSubtitle(subTitle);
    }
    */

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1){
                        case BluetoothChatService.STATE_CONNECTED:
                            //messageArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;

                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //messageArrayAdapter.add("ME: " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    //Construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //messageArrayAdapter.add(mConnectedDeviceName + ": "+ readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    //Save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != mActivity) {
                        Toast.makeText(mActivity, "Connected to "+mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Constants.MESSAGE_TOAST:
                    if (null != mActivity){
                        Toast.makeText(mActivity, msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }
    };


}
