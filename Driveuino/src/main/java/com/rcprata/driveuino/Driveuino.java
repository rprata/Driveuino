package com.rcprata.driveuino;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.TextView;

import com.rcprata.driveuino.core.AccelerometerService;
import com.rcprata.driveuino.utils.Globals;

public class Driveuino extends Activity {

        private TextView axisX, axisY, axisZ;

        private AccelerometerService accelerometerService;

        private final Handler handler = new Handler() {

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Globals.HANDLER_STATE_ACCELEROMETER:
                        float[] accelerometerValues = (float[]) msg.obj;
                        axisX.setText(getString(R.id.axis_x) + accelerometerValues[0]);
                        axisY.setText(getString(R.id.axis_y) + accelerometerValues[1]);
                        axisZ.setText(getString(R.id.axis_z) + accelerometerValues[2]);
                        break;
                    default:
                        break;
                }
            }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);

            axisX = (TextView)findViewById(R.id.axis_x);
            axisY = (TextView)findViewById(R.id.axis_y);
            axisZ = (TextView)findViewById(R.id.axis_z);

            accelerometerService = new AccelerometerService(this, handler);
        }

        @Override
        protected void onResume() {
            super.onResume();
            accelerometerService.registerAccelerometerServiceListener();
        }

        @Override
        protected void onPause() {
            super.onPause();
            accelerometerService.unregisterAccelerometerServiceListener();
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.driveuino, menu);
            return true;
        }

    }