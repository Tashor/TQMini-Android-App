package com.tashor.tqmini;

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

public class MainActivity extends AppCompatActivity {

    static final byte CONTROL_BYTE = (byte)255;
    static final byte DIRECTION_BYTE = (byte)1;
    static final byte SPEED_BYTE = (byte)2;
    static final byte STOP = (byte)0;
    static final byte FORWARD = (byte)1;
    static final byte BACKWARD = (byte)2;
    static final byte LEFT = (byte)3;
    static final byte RIGHT = (byte)4;

    static final byte ACTION_BYTE = (byte)3;
    static final byte ACTION_SAY_HI = (byte)1;
    static final byte ACTION_SHAKE = (byte)2;
    static final byte ACTION_PUSH_UP = (byte)3;

    ImageButton imgBtnUp, imgBtnRight, imgBtnDown, imgBtnLeft;
    Button btnSayHi, btnShake, btnPushUp;
    TextView textDirection, textSpeed;
    SeekBar seekbarSpeed;
    BluetoothClass bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt = (BluetoothClass) getApplicationContext();
        setupImageButtons();
        setupButtons();
        setupSeekBar();
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
                if(!bt.isDeviceConnected()) {
                    if(bt.connect()) {
                        // ToDo: visual confirmation -> change icon
                    }
                } else {
                    bt.disconnect();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupImageButtons() {
        imgBtnUp = (ImageButton) findViewById(R.id.imagebutton_up);
        imgBtnRight = (ImageButton) findViewById(R.id.imagebutton_right);
        imgBtnDown = (ImageButton) findViewById(R.id.imagebutton_down);
        imgBtnLeft = (ImageButton) findViewById(R.id.imagebutton_left);

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
        btnSayHi = (Button) findViewById(R.id.button_say_hi);
        btnShake = (Button) findViewById(R.id.button_shake);
        btnPushUp = (Button) findViewById(R.id.button_push_up);

        // ToDo: write initialisation method for the buttons
        btnSayHi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.getBackground().setColorFilter(Color.parseColor("#1A237E"), PorterDuff.Mode.SRC_ATOP);
                    view.invalidate();
                    try {
                        bt.sendByte(CONTROL_BYTE);
                        bt.sendByte(ACTION_BYTE);
                        bt.sendByte(ACTION_SAY_HI);
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
                    try {
                        bt.sendByte(CONTROL_BYTE);
                        bt.sendByte(ACTION_BYTE);
                        bt.sendByte(ACTION_SHAKE);
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
                    try {
                        bt.sendByte(CONTROL_BYTE);
                        bt.sendByte(ACTION_BYTE);
                        bt.sendByte(ACTION_PUSH_UP);
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
            try{
                bt.sendByte(CONTROL_BYTE);
                bt.sendByte(DIRECTION_BYTE);
                bt.sendByte(sendViaBluetooth);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            textDirection.setText(txtCurrentDirection);
            try{
                bt.sendByte(CONTROL_BYTE);
                bt.sendByte(DIRECTION_BYTE);
                bt.sendByte(sendViaBluetooth);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (motionEvent.getAction() ==  MotionEvent.ACTION_UP){
            view.getBackground().clearColorFilter();
            view.invalidate();
            textDirection.setText("Stop");
            try{
                bt.sendByte(CONTROL_BYTE);
                bt.sendByte(DIRECTION_BYTE);
                bt.sendByte(STOP);
                Thread.sleep(25);
                bt.sendByte(CONTROL_BYTE);
                bt.sendByte(DIRECTION_BYTE);
                bt.sendByte(STOP);
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
                try {
                    bt.sendByte(CONTROL_BYTE);
                    bt.sendByte(SPEED_BYTE);
                    bt.sendByte((byte)(progress+1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    bt.sendByte(CONTROL_BYTE);
                    bt.sendByte(SPEED_BYTE);
                    bt.sendByte((byte)(progressValue+1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
