package com.lightlinks.dibs.dibslightlinks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


/**
 * Created by NickHome on 3/13/16.
 */
public class BluetoothChatService {

    private static final String TAG = "BluetoothChatService";

    private static final String NAME = "HC-05"; //Only need to connect to this device for our project
    //So using the name of the bluetooth module as the Name

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;


    /**
     * Constructor. Prepares a new BluetoothChat session
     */

    public BluetoothChatService (Context context, Handler handler){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;

    }

    // Sets state of connection
    private synchronized void setState(int state){
        Log.d(TAG, "setState() " + mState + "-> " +state );
        mState = state;

        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState(){
        return mState;
    }

    public synchronized void start(){
        Log.d(TAG, "start -- CANCELLING ALL THREADS");

        //cancel any thread attempting to make a connection
        if (mConnectThread!=null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        //cancel any thread currently running a connection
        if (mConnectedThread!=null){
            mConnectedThread.cancel();
            mConnectedThread=null;
        }

        setState(STATE_LISTEN);

        //start the thread to listen on a BluetoothServerSocket

        if (mAcceptThread == null){
            mAcceptThread = new AcceptThread(true);
            mAcceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device, boolean secure){
        Log.d(TAG, "connect to : "+device);

        //cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING){
            if (mConnectThread != null){
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        //cancel any thread currently running a connection
        if (mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;

        }

        // Start the thread to connect to the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType){
        Log.d(TAG, "connected, Socket Type: "+socketType+ " Device Name: "+device.getName());

        //cancel the thread that completed the connection
        if (mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        //cancel any thread currently running a connection
        if (mConnectedThread!=null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        //cancel the accept thread because only want to connect to one device
        if (mAcceptThread != null){
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        //Send the name of the device back to the UI Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME,device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        //Set state to connected
        setState(STATE_CONNECTED);
    }

    // Stop all threads
    public synchronized void stop(){
        Log.d(TAG, "Stop");

        if (mConnectThread != null){
            mConnectedThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null){
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        setState(STATE_NONE);
    }


    public void write(byte[] out){
        //create temporary object
        ConnectedThread r;
        //Synchronize a copy of the ConnectedThread
        synchronized (this){
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }

        //Perform the write unsynchronized
        r.write(out);
    }

    private void connectionFailed(){
        //Send a failure message back to the activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        //Start the service over to restart listening mode
        BluetoothChatService.this.start();
    }

    private void connectionLost(){
        //Send a message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        BluetoothChatService.this.start();
    }



    private class AcceptThread extends Thread{
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;
        private int mmState;

        public AcceptThread(boolean secure){
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            //Create new listening server socket
            try{
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            }
            catch (IOException ex){
                Log.e(TAG, "Socket Type: "+mSocketType+ " listen() failed",ex);
            }

            mmServerSocket = tmp;
        }

        public void run(){
            Log.d(TAG, "Socket Type: " + mSocketType + " BEGIN mAcceptThread " + this);
            setName(" AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

            //Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED || mState != STATE_CONNECTING){
                try{
                    //Blocking call will only return if successful connection or gets IOException
                    socket = mmServerSocket.accept();
                }
                catch (IOException ex){
                    Log.e(TAG, "Accepted Thread -- Socket Type: "+ mSocketType+ " accept() failed",ex );
                    break;
                }

                if (socket == null)
                    Log.d(TAG, "AcceptedThread -- run:SOCKET IS NULL!!! ");
                //If connection was accepted
                if (socket != null){
                    synchronized (BluetoothChatService.this) {
                        switch (mState){
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                //Situation normal. Start connected thread
                                connected(socket,socket.getRemoteDevice(),mSocketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate the new socket
                                try{
                                    socket.close();
                                    Log.d(TAG, "Accepted Thread run: closed the socket ");
                                }
                                catch (IOException ex){
                                    Log.e(TAG, "Could not close unwanted socket", ex);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread, Socket Type: "+ mSocketType);
        }

        public void cancel(){
            Log.d(TAG, "Accepted Thread Cancel method -- Socket Type: "+mSocketType+ " cancel" + this);
            try{
                mmServerSocket.close();
                Log.d(TAG, "Accepted Thread cancel: closed server socket successfully ");
            } catch (IOException ex){
                Log.e(TAG, "Socket Type: "+ mSocketType+" close() of server failed", ex);
            }
        }
    }


    private class ConnectThread extends Thread{
        private final BluetoothSocket mmsocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure){
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            }catch (IOException ex){
                Log.e(TAG, "Socket Type: "+mSocketType+ "create() failed", ex);
            }
            mmsocket = tmp;
        }

        public void run(){
            Log.i(TAG, "BEGIN mConnectThread SocketType: " + mSocketType);
            setName("ConnectThread"+mSocketType);

            //Always cancel discovery to ensure better connection
            mAdapter.cancelDiscovery();

            //Make a connection to the BluetoothSocket
            try{
                mmsocket.connect();
            }catch (IOException ex){
                try{
                    mmsocket.close();
                }catch (IOException ex2){
                    Log.e(TAG, "unable to close() "+mSocketType +" socket during connection failure", ex2);
                }
                connectionFailed();
                return;
            }

            //Reset the connected thread because we are done
            synchronized (BluetoothChatService.this){
                mConnectThread = null;
            }

            //Start the connected thread to actually start communication :)
            connected(mmsocket, mmDevice, mSocketType);
        }

        public void cancel(){
            try{
                mmsocket.close();
            }catch (IOException ex){
                Log.e(TAG, "close() of connect "+mSocketType+ " socket failed", ex);
            }
        }
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //Constructor
        public ConnectedThread(BluetoothSocket socket, String socketType){
            Log.d(TAG, "created ConnectedThread: "+socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }catch (IOException ex){
                Log.e(TAG, "temp sockets not created", ex);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // keep listening to InputStream while connected
            while (true){
                try{
                    bytes = mmInStream.read(buffer);

                    //send the obtained bytes to the UI Avtivity
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                }catch (IOException ex){
                    Log.e(TAG, "Disconnected", ex);
                    connectionLost();
                    //Start service over to restart listening mode
                    BluetoothChatService.this.start();
                    break;
                }
            }
        }

        public void write(byte[] buffer){
            try {
                mmOutStream.write(buffer);

                //Share the sent message back to the UI Activity
                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            }catch (IOException ex){
                Log.e(TAG, "Exception during trying to write", ex);
            }
        }

        public void cancel(){
            try{
                mmSocket.close();
            }catch (IOException e){
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}