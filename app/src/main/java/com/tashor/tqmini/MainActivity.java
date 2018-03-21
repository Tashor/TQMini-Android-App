package com.tashor.tqmini;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private static final byte CONTROL_BYTE = (byte)255;
    private static final byte DIRECTION_BYTE = (byte)1;
    private static final byte SPEED_BYTE = (byte)2;
    private static final byte STOP = (byte)0;
    private static final byte FORWARD = (byte)1;
    private static final byte BACKWARD = (byte)2;
    private static final byte LEFT = (byte)3;
    private static final byte RIGHT = (byte)4;

    private static final byte ACTION_BYTE = (byte)3;
    private static final byte ACTION_SAY_HI = (byte)1;
    private static final byte ACTION_SHAKE = (byte)2;
    private static final byte ACTION_PUSH_UP = (byte)3;

    private TextView textDirection, textSpeed;
    private SeekBar seekbarSpeed;
    private BluetoothClass mBluetoothClass;
    private BluetoothAdapter mBluetoothAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }

        setupImageButtons();
        setupButtons();
        setupSeekBar();
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

        } else if (mBluetoothClass == null) {
            mBluetoothClass = (BluetoothClass) getApplicationContext();
        }
    }

    // use MenuBar from: "res/menu/main_menu.xml"
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.bluetooth:
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListAdapterActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListAdapterActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BluetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mBluetoothClass.startConnection(device);
                }
                break;

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled
                    mBluetoothClass = (BluetoothClass) getApplicationContext();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, "Bluetooth not enabled.\nPlease enable Bluetooth!", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    private void setupImageButtons() {
        ImageButton imgBtnUp = (ImageButton) findViewById(R.id.imagebutton_up);
        ImageButton imgBtnRight = (ImageButton) findViewById(R.id.imagebutton_right);
        ImageButton imgBtnDown = (ImageButton) findViewById(R.id.imagebutton_down);
        ImageButton imgBtnLeft = (ImageButton) findViewById(R.id.imagebutton_left);

        textDirection = (TextView) findViewById(R.id.txt_current_direction);

        imgBtnUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                onTouchAction(view, motionEvent, "Forward", FORWARD);
                return false;
            }
        });

        imgBtnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                onTouchAction(view, motionEvent, "Right", RIGHT);
                return false;
            }
        });

        imgBtnDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                onTouchAction(view, motionEvent, "Backward", BACKWARD);
                return false;
            }
        });

        imgBtnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                onTouchAction(view, motionEvent, "Left", LEFT);
                return false;
            }
        });
    }

    private void setupButtons() {
        Button btnSayHi = (Button) findViewById(R.id.button_say_hi);
        Button btnShake = (Button) findViewById(R.id.button_shake);
        Button btnPushUp = (Button) findViewById(R.id.button_push_up);

        // ToDo: write initialisation method for the buttons
        btnSayHi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.getBackground().setColorFilter(Color.parseColor("#1A237E"), PorterDuff.Mode.SRC_ATOP);
                    view.invalidate();
                    byte[] tmpByteArray = {CONTROL_BYTE, ACTION_BYTE, ACTION_SAY_HI};
                    try {
                        mBluetoothClass.sendBytes(tmpByteArray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (motionEvent.getAction() ==  MotionEvent.ACTION_UP) {
                    view.getBackground().clearColorFilter();
                    view.invalidate();
                }
                return false;
            }
        });
        btnShake.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.getBackground().setColorFilter(Color.parseColor("#1A237E"), PorterDuff.Mode.SRC_ATOP);
                    view.invalidate();
                    byte[] tmpByteArray = {CONTROL_BYTE, ACTION_BYTE, ACTION_SHAKE};
                    try {
                        mBluetoothClass.sendBytes(tmpByteArray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (motionEvent.getAction() ==  MotionEvent.ACTION_UP) {
                    view.getBackground().clearColorFilter();
                    view.invalidate();
                }
                return false;
            }
        });
        btnPushUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.getBackground().setColorFilter(Color.parseColor("#1A237E"), PorterDuff.Mode.SRC_ATOP);
                    view.invalidate();
                    byte[] tmpByteArray = {CONTROL_BYTE, ACTION_BYTE, ACTION_PUSH_UP};
                    try {
                        mBluetoothClass.sendBytes(tmpByteArray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (motionEvent.getAction() ==  MotionEvent.ACTION_UP) {
                    view.getBackground().clearColorFilter();
                    view.invalidate();
                }
                return false;
            }
        });
    }

    private void onTouchAction(View view, MotionEvent motionEvent, final String txtCurrentDirection, byte sendViaBluetooth) {
        if (motionEvent.getAction() ==  MotionEvent.ACTION_DOWN){
            view.getBackground().setColorFilter(Color.parseColor("#1A237E"), PorterDuff.Mode.SRC_ATOP);
            view.invalidate();
            textDirection.setText(txtCurrentDirection);
            byte[] tmpByteArray = {CONTROL_BYTE, DIRECTION_BYTE, sendViaBluetooth};
            try{
                mBluetoothClass.sendBytes(tmpByteArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            textDirection.setText(txtCurrentDirection);
            byte[] tmpByteArray = {CONTROL_BYTE, DIRECTION_BYTE, sendViaBluetooth};
            try{
                mBluetoothClass.sendBytes(tmpByteArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (motionEvent.getAction() ==  MotionEvent.ACTION_UP){
            view.getBackground().clearColorFilter();
            view.invalidate();
            textDirection.setText("Stop");
            byte[] tmpByteArray = {CONTROL_BYTE, DIRECTION_BYTE, STOP};
            try{
                mBluetoothClass.sendBytes(tmpByteArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void setupSeekBar() {
        seekbarSpeed = (SeekBar) findViewById(R.id.seekbar_speed);
        textSpeed = (TextView) findViewById(R.id.txt_speed_value);

        seekbarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progressValue = progress;
                String valueString = String.valueOf(progress+1) + "/" + String.valueOf(seekbarSpeed.getMax()+1);    // display values starting by 1 to max+1 (instead of 0 to max)
                textSpeed.setText(valueString);
                byte[] tmpByteArray = {CONTROL_BYTE, SPEED_BYTE, (byte)(progress+1)};
                try {
                    mBluetoothClass.sendBytes(tmpByteArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                byte[] tmpByteArray = {CONTROL_BYTE, SPEED_BYTE, (byte)(progressValue+1)};
                try {
                    mBluetoothClass.sendBytes(tmpByteArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
