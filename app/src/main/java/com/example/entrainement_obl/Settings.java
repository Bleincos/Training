package com.example.entrainement_obl;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings extends AppCompatActivity implements SensorEventListener{
private static int valueMaxBrg = 255; // For the dev's device set it to 4096 for emulation set it to 255
/**
 * Variables
 */
public CheckBox boxAuto;
public SensorManager mySensorManager;
public Sensor sensorLight;
private Boolean success = false, bool;
private int brightness = 0;
private SeekBar seekBar;
private Activity activity;

/**
 * This is the elements call when the activity is create, the listenners and initialisations will be there or called from here
 *
 * @param savedInstanceState
 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
            getPermissionBrightness();
            checkSensor();
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        Toast tooast = Toast.makeText(this,"Valeur:"+getBrightness(), Toast.LENGTH_SHORT);
        tooast.show();
        /**
         * Pop up part
         */
        this.activity = this;
            /**
             SeekBar Part
             */
            seekBar = findViewById(R.id.brightnessBar);
            seekBar.setMax(valueMaxBrg);
            seekBar.setProgress(getBrightness());
            seekBar.setKeyProgressIncrement(1);
            /**
             Sensors part
             */
            boxAuto = findViewById(R.id.autolight);
            boxAuto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (boxAuto.isChecked()&& success) {
                        Toast.makeText(Settings.this, "The brightness will be set automatically", Toast.LENGTH_LONG).show();
                        mySensorManager.registerListener((SensorEventListener) Settings.this, sensorLight, mySensorManager.SENSOR_DELAY_NORMAL);
                        android.provider.Settings.System.putInt(getContentResolver(),
                                android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                    } else {
                        if (success) {
                            mySensorManager.unregisterListener((SensorEventListener) Settings.this);
                            android.provider.Settings.System.putInt(getContentResolver(),
                                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                        }else {
                            Toast.makeText(Settings.this, "Permissions aren't granted", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        /**
         * This function take three parameters in entry to react if the user change the luminosity via the bar
         * @param seekBar b
         * @param progress b
         * @param fromUser b
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && success) {
                setBrightness(progress);
            }
        }

        /**
         * This function deals what has to be done when the user start to touch the seekbar
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        /**
         * This function deals what has to be done when the user stop to touch the seek bar
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!success) {
                Toast.makeText(Settings.this, "Permission Not granted", Toast.LENGTH_SHORT).show();
            }
            if (success) {
                setBrightness(seekBar.getProgress());
            }
        }
        });
    }

    /**
     * This function interact with the system to adjust the brightness of the screen
     *
     * @param brightnessLocal the value we want to assing to the brightness
    */
    public void setBrightness(int brightnessLocal) {
        if (brightnessLocal < 0) {
            brightnessLocal = 0;
        } else {
            if (brightnessLocal > valueMaxBrg) {
                brightnessLocal = valueMaxBrg;
            }
        }
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        android.provider.Settings.System.putInt(contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS, brightnessLocal);
        seekBar.setProgress(getBrightness());
    }

    /**
     * This function allows to know the value of the brightness of the screen
     *
     * @return the value of the brightness
     */

    public int getBrightness() {
        try {
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            brightness = android.provider.Settings.System.getInt(contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch (android.provider.Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return brightness;
    }

    /**
     * Check the permission to deals with the brightness of the screen
    */
    private void getPermissionBrightness() {

        if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
            bool = android.provider.Settings.System.canWrite(getApplicationContext());
            if (bool) {
                success = true;
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivityForResult(intent, 1000);
            }
        }
    }

    /**
     * This functions deals with the
     *
     * @param requestCode the code request
     * @param resultCode  the resultCode
     * @param data        an intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
                bool = android.provider.Settings.System.canWrite(getApplicationContext());
                if (bool) {
                    success = true;
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                    ;
                }
            }
        }
    }

    /**
     * What has to be done when the application is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        mySensorManager.unregisterListener((Settings.this));
    }

    /**
     * What has to be done when the application is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        mySensorManager.registerListener((SensorEventListener) Settings.this, sensorLight, mySensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * check if the sensor (Light sensor) is avaible on the device and block the Automod if there is no light sensor
     */
    public void checkSensor() {
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (sensorLight == null) {
            Toast.makeText(this, "No light sensor found", Toast.LENGTH_SHORT).show();
            boxAuto.setChecked(false);
            boxAuto.setActivated(false);
        } else {
            Toast.makeText(this, "Light sensor found", Toast.LENGTH_SHORT).show();
            mySensorManager.unregisterListener(Settings.this);
        }
    }
    /**
     * Implements the changes when the Light sensor change
     *
     * @param event is the event when the sensors change
     */
    //@Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (boxAuto.isChecked()) {
                brightness = (int) (event.values[0]);// convert value, Max is 40 000 and max brightness is 255 for the emulated device so 40 000/ 255 equals to 156.8627 40 000/4096 equals 9.765625
                setBrightness(brightness);
                seekBar.setProgress(getBrightness());
            }
        }
    }

    /**
     * Function not used, to do something when the accuracy or precision of a sensor change
     *
     * @param sensor   the sensor
     * @param accuracy its accuracy
     */
    //@Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    public void goMain(View view){
            Log.i("Settings","Button_back Clicked");
            Intent intent = new Intent(
                    Settings.this,
                    MainActivity.class
            );
            startActivity(intent);
    }
}