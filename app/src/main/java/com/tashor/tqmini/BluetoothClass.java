package com.tashor.tqmini;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Riko on 17.03.2018.
 */

public class BluetoothClass extends Application {
    // ToDo: find better solution than using hardware address
    private final String DEVICE_ADDRESS = "00:21:13:00:F2:22";      // hardware adress of HC-05 module
    private static final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");     //Serial Port Service ID

    private BluetoothDevice BTDevice;
    private BluetoothSocket BTSocket;
    private OutputStream outStream;
    private InputStream inStream;

    private boolean deviceConnected = false;

    public boolean connect() {
        boolean success = false;
        if(setupBluetooth()) {
            if(connectBluetooth()) {
                success = true;
                deviceConnected = true;
            }
        }
        return success;
    }

    public void disconnect() {
        try {
            outStream.close();
            inStream.close();
            BTSocket.close();
            deviceConnected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean setupBluetooth() {

        boolean found = false;  // gets true when setup successful

        BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (BTAdapter == null) {
            Toast.makeText(getApplicationContext(), "No Bluetooth Adapter found!", Toast.LENGTH_SHORT).show();
        }
        if (!BTAdapter.isEnabled()) {
            BTAdapter.enable();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = BTAdapter.getBondedDevices();
        if (bondedDevices.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No paired devices found. Please pair the device!", Toast.LENGTH_LONG).show();
        } else {

            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    BTDevice = iterator;
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    private boolean connectBluetooth() {
        boolean connected = true;
        try {
            BTSocket = BTDevice.createRfcommSocketToServiceRecord(PORT_UUID);
            BTSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }

        if (connected) {
            try {
                outStream = BTSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inStream = BTSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return connected;
    }


    public boolean isDeviceConnected() {
        return deviceConnected;
    }


    public void sendByte(byte value) {
        try {
            outStream.write(value);
            Thread.sleep(5);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void sendText(String text) {
        try {
            outStream.write(text.getBytes());
            Thread.sleep(5);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
