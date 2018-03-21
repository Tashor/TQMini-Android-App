package com.tashor.tqmini;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Tashor on 17.03.2018.
 */

public class BluetoothClass extends Application {

    private static final String appName = "TQMini";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");     //Serial Port Service ID

    private BluetoothAdapter mBTAdapter;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private BluetoothDevice mmBTDevice;
    private UUID deviceUUID;

    private ConnectedThread mConnectedThread;


    @Override
    public void onCreate() {
        super.onCreate();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmBTServerSocket;

        private AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = mBTAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmBTServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket BTSocket = null;

            try {
                BTSocket = mmBTServerSocket.accept();
            } catch (IOException e){
                e.printStackTrace();
            }

            if(BTSocket != null){
                connected(BTSocket, mmBTDevice);
            }
        }

        public void cancel() {
            try {
                mmBTServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmBTSocket;

        public ConnectThread (BluetoothDevice device, UUID uuid) {
            mmBTDevice = device;
            deviceUUID = uuid;
        }

        public void run() {
            BluetoothSocket tmp = null;

            try {
                tmp = mmBTDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmBTSocket = tmp;

            mBTAdapter.cancelDiscovery();

            try {
                mmBTSocket.connect();
            } catch (IOException e) {
                try {
                    mmBTSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            connected(mmBTSocket, mmBTDevice);
        }

        public void cancel() {
            try {
                mmBTSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Start the AcceptThread.
      */
    public synchronized void start() {
        if(mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if(mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    /**
     *  AcceptThread waits for connection.
     *  Start ConnectThread and attempt to make a connection.
      */
    public void startConnection(BluetoothDevice device) {
        mConnectThread = new ConnectThread(device, MY_UUID);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmBTSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmBTSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;


            try {
                tmpIn = mmBTSocket.getInputStream();
                tmpOut = mmBTSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            // ToDo: handle incoming messages, if needed in the future
            while(true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                mmBTSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void connected(BluetoothSocket mmBTSocket, BluetoothDevice mmBTDevice) {
        mConnectedThread = new ConnectedThread(mmBTSocket);
        mConnectedThread.start();
    }

    public void sendBytes(byte[] bytesOut) {
        mConnectedThread.write(bytesOut);
    }
}
