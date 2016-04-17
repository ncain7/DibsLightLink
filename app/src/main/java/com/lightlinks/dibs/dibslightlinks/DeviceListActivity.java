package com.lightlinks.dibs.dibslightlinks;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by NickHome on 3/11/16.
 */
public class DeviceListActivity extends Activity{

        /**
         * Tag for Log
         */
        private static final String TAG = "DeviceListActivity";

        /**
         * Return Intent extra
         */
        public static String EXTRA_DEVICE_ADDRESS = "device_address";

        /**
         * Member fields
         */
        private BluetoothAdapter mBluetoothAdapter;

        private BluetoothDevice mBluetoothDevice;

        /**
         * Newly discovered devices
         */
        private ArrayAdapter<String> newDevicesArrayAdapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.unregisterReceiver(mReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        setResult(Activity.RESULT_CANCELED);


        newDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.device_name);

        ListView deviceListView = (ListView) findViewById(R.id.bluetooth_list);
        if (deviceListView == null){
            Toast.makeText(this, "Could not find listview :(", Toast.LENGTH_SHORT).show();
            finish();
        }
        deviceListView.setAdapter(newDevicesArrayAdapter);
        deviceListView.setOnItemClickListener(mDeviceClickListener);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        //set bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> bluetoothDevices = mBluetoothAdapter.getBondedDevices();

        if (bluetoothDevices.size() > 0 ){
            for (BluetoothDevice device : bluetoothDevices){
                if (device.getName().equals("HC-05")){
                    mBluetoothDevice = device;
                    break;
                }
            }
        }

        //If didnt find device, search for it
        mBluetoothAdapter.startDiscovery();

    }

    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBluetoothAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra("device_address", address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //When discovery finds a device, add it to arraylist
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                //Get BluetoothDevice from Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                newDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                if (newDevicesArrayAdapter.getCount()==0){
                    String none = getResources().getText(R.string.bt_no_device_found).toString();
                    newDevicesArrayAdapter.add(none);
                }
            }
        }
    };
    }