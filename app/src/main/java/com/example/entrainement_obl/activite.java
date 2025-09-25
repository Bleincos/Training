package com.example.entrainement_obl;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.EditText;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.security.Signature;

public class activite extends AppCompatActivity implements SensorEventListener {
    //private int duration = 120;
    public long elapseRealTime = SystemClock.elapsedRealtime();
    public SensorManager mySensorManager;
    public Sensor sensorLight;
    public static int valueMaxBrg = 255;
    public int[] tab = new int[4]; // [0] répétitions [1] minutes [2] secondes [3] temps de récup.
    private Boolean success = false, bool, stop, boolCh; //success and bool are used for the screenBrighness, stop to stop the training and boolCh for the chronometers.
    public MediaPlayer buzz, bip;
    public long chronotrainBase = 0;
    public CharSequence tempsecoule = "0.00";
    EntrainementAdapter adapter;
    ListView listEntrainement;
    public int  position=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_activite_original);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.top);
            return insets;
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionBrightness();
            checkSensor();
        }
        listEntrainement = findViewById(R.id.listeEntrainement);
        EntrainementType entrainementType1 = new EntrainementType("TEST",10,5,10,1);
        Singleton.getInstance().entrainements.add(entrainementType1);
        Toast toast = Toast.makeText(activite.this, Singleton.getInstance().entrainements.get(Singleton.getInstance().i).name,Toast.LENGTH_SHORT);
        toast.show();
        main();
    }

    public void main() {
        setContentView(R.layout.activity_activite_original);
        Button buttonLaunch;
        EditText editRep, editMin, editSec, editRec;
        editRep = findViewById(R.id.Rep);
        editMin = findViewById(R.id.min);
        editSec = findViewById(R.id.sec);
        editRec = findViewById(R.id.Rec);
        buttonLaunch = findViewById(R.id.button_go);
        stop = false;
        Chronometer chronometerSet = findViewById(R.id.chronoSet);
        chronometerSet.setText(tempsecoule);
        updateListView();
        buttonLaunch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editRep.getText().toString().isEmpty() || editMin.getText().toString().equals("") || editSec.getText().toString().equals("") || editRec.getText().toString().equals("") || editRep.getText().toString().equals(("0"))) {
                    Toast toaast = Toast.makeText(activite.this, "Les valeurs saisies sont incorrectes", Toast.LENGTH_SHORT);
                    toaast.show();
                } else {
                    tab[0] = Integer.parseInt(editRep.getText().toString()); // Récupération des répétitions
                    Log.i("Activite_Min", "min_saisies");
                    Log.e("Activity", "Erreur de minutes");
                    tab[1] = Integer.parseInt(editMin.getText().toString()); // récupération des minutes
                    System.out.println(tab[1]);
                    Log.i("Activite_Sec", "sec_saisies");
                    tab[2] = Integer.parseInt(editSec.getText().toString()); // récupération des secondes
                    Log.i("Activite_Min", "min_saisies");
                    Log.e("Activity", "Erreur de minutes");
                    System.out.println(tab[2]);
                    tab[3] = Integer.parseInt(editRec.getText().toString()); // récupération des temps de récupération
                    Log.i("Activite_Sec", "sec_saisies");
                    demarrage();
                }
            }
        });
    }

    /**
     * Function to return to the main activity
     *
     * @param view
     */
    public void buttonBackMain(View view) {
        Log.i("Activite", "buttonBackClicked");
        Intent intent = new Intent(
                activite.this,
                MainActivity.class
        );
        startActivity(intent);
        finish();
    }

    public void demarrage() {
        setContentView(R.layout.activity_demarrage);
        TextView temps;
        temps = findViewById(R.id.dem);
        temps.setText("3");
        MediaPlayer soundDepart = MediaPlayer.create(activite.this, R.raw.mkgo);
        soundDepart.start();
        ConstraintLayout layout =findViewById(R.id.activity_demarrage_layout);
        new CountDownTimer(4000, 1000) {
            public void onFinish() {
                boolCh = false;
                debut_entrainement();
            }

            public void onTick(long l) {
                if (l / 1000 < 1) {
                    onFinish();
                    cancel();
                }
                temps.setText("" + l / 1000);
                switch ((int) (l / 1000)) {
                    case 0:
                        layout.setBackgroundColor(Color.parseColor("#2E7A3C"));
                        break;

                    case 1:
                         layout.setBackgroundColor(Color.parseColor("#FF2400"));
                        break;

                    case 2:
                         layout.setBackgroundColor(Color.parseColor("#2626ff"));
                        break;
                    default: layout.setBackgroundColor(Color.parseColor("#F0FE26"));
                        break;
                }
            }
        }.start();

    }

    /**
     * Method to start a training, it allows the user to launch the countDown and start the program.
     */
    public void debut_entrainement() {
        android.provider.Settings.System.putInt(getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        setBrightness(valueMaxBrg);
        setContentView(R.layout.activity_programme);
        TextView minutes = findViewById(R.id.textViewMin);
        TextView sec = findViewById(R.id.textViewSecondes);
        TextView rep = findViewById(R.id.textViewRep);
        buzz = MediaPlayer.create(this, R.raw.bip);
        bip = MediaPlayer.create(this, R.raw.longbip);
        Chronometer chronosTraining = findViewById(R.id.chronoTrain);
        if (boolCh) {
            chronosTraining.setBase(chronotrainBase);
        }
        chronosTraining.start();
        Log.i("Avancement", "Récup des id");
        rep.setText("Répétitions : " + tab[0]);
        minutes.setText("Minutes restantes : " + tab[1]);
        sec.setText("Secondes restantes : " + tab[2]);
        new CountDownTimer(tab[1] * 60 * 1000 + tab[2] * 1000, 1000) {
            @Override
            public void onFinish() {
                bip.start();
                chronotrainBase = chronosTraining.getBase();
                chronosTraining.stop();
                callRecup();
            }

            @Override
            public void onTick(long l) {
                if (l / 60000 >= 1) {
                    sec.setText("Secondes restantes : " + (l % 60000) / 1000);
                    minutes.setText("Minutes restantes :" + l / 60000);
                } else {
                    minutes.setText("Minutes restantes : 0");
                    sec.setText("Secondes Restantes : " + l / 1000);
                }
                if (l / 1000 < 3) {
                    buzz.start();
                }
                if (stop) {
                    cancel();
                }
            }
        }.start();
    }

    /**
     * Method to call the recuperation
     */
    public void callRecup() {
        setContentView(R.layout.activity_programme_recup);
        ImageView imageViewGif = findViewById(R.id.imageViewGif2);
        Glide.with(this).load(R.drawable.repos).into(imageViewGif);
        TextView tempsRest = findViewById(R.id.Repos_restant);
        Chronometer chronoRecup = findViewById(R.id.chronoRecup);
        chronoRecup.setBase(chronotrainBase);
        chronoRecup.start();
        buzz = MediaPlayer.create(this, R.raw.bip);
        bip = MediaPlayer.create(this, R.raw.longbip);
        MediaPlayer endbip = MediaPlayer.create(this, R.raw.endbip);


        new CountDownTimer((long) tab[3] * 1000, 1000) {
            @Override
            public void onFinish() {
                Toast toaast = Toast.makeText(activite.this, "Timer Finish!!", Toast.LENGTH_SHORT);
                toaast.show();
                if (tab[0] != 1) { // machine count the 0 while everybody starts from 1 so we finish the last rep at 1 (function call at the end of the set so)
                    tab[0]--;
                    bip.start();
                    boolCh = true;
                    debut_entrainement();
                } else {
                    endbip.start();
                    toaast = Toast.makeText(activite.this, "TRAINING COMPLETE, WOW!", Toast.LENGTH_SHORT);
                    toaast.show();
                    chronoRecup.stop();
                    tempsecoule = chronoRecup.getText();
                    main();
                }
            }

            public void onTick(long l) {
                if (l / 1000 < 3) {
                    buzz.start();
                }
                tempsRest.setText("Secondes restantes : " + l / 1000);
                if (stop) {
                    cancel();
                }
            }
        }.start();
    }

    /**
     * This function interact with the system to adjust the brightness of the screen
     *
     * @param brightnessLocal the value we want to assign to the brightness
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
    }

    /**
     * Check the permission to deals with the brightness of the screen
     */
    private void getPermissionBrightness() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
     * check if the sensor (Light sensor) is available on the device
     */
    public void checkSensor() {
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (sensorLight == null) {
            Toast.makeText(this, "No light sensor found", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Light sensor found", Toast.LENGTH_SHORT).show();
            mySensorManager.unregisterListener(activite.this);
        }
    }

    /**
     * What has to be done when the application is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        mySensorManager.unregisterListener(activite.this);
    }

    /**
     * What has to be done when the application is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
        mySensorManager.registerListener(activite.this, sensorLight, mySensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onDestroy() {
        android.provider.Settings.System.putInt(getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        super.onDestroy();
    }

    /**
     * Implements the changes when the Light sensor change
     *
     * @param event is the event when the sensors change
     */
    public void onSensorChanged(SensorEvent event) {
    }

    /**
     * Function not used, to do something when the accuracy or precision of a sensor change
     *
     * @param sensor   the sensor
     * @param accuracy its accuracy
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Method to stop the training
     *
     * @param View
     */
    public void callStop(View View) {
        buzz.stop();
        bip.stop();
        stop = true;
        Intent intent = new Intent(this,
                activite.class
        );
        startActivity(intent);
        finish();
    }

    public void saveAtraining(View view) {
        EditText editRep, editMin, editSec, editRec, editName;
        String name;
        int rep, min, sec, rec;
        editRep = findViewById(R.id.Rep);
        editMin = findViewById(R.id.min);
        editSec = findViewById(R.id.sec);
        editRec = findViewById(R.id.Rec);
        editName = findViewById(R.id.Name);
        name = editName.getText().toString();
        rep = Integer.parseInt(editRep.getText().toString());
        min = Integer.parseInt(editMin.getText().toString());
        sec = Integer.parseInt(editSec.getText().toString());
        rec = Integer.parseInt(editRec.getText().toString());
        if (editRep.getText().toString().equals("") || editMin.getText().toString().equals("") || editSec.getText().toString().equals("") || editRec.getText().toString().equals("") || editRep.getText().toString().equals(("0")) || editName.getText().equals("")) {
            Toast toaast = Toast.makeText(activite.this, "Les valeurs saisies sont incorrectes", Toast.LENGTH_SHORT);
            toaast.show();
        } else {
            //if(Singleton.getInstance().state==Boolean.TRUE) // on edite?
            EntrainementType entrainementType = new EntrainementType(name, rep, min, sec, rec);
            Singleton.getInstance().entrainements.add(entrainementType);
        }
        position++;
        Singleton.getInstance().i=position;
        EntrainementType trainnn = Singleton.getInstance().entrainements.get(0);
        Toast toast = Toast.makeText(activite.this, Singleton.getInstance().entrainements.get(Singleton.getInstance().i).name,Toast.LENGTH_SHORT);
        toast.show();
        updateListView();
    }

    void updateListView() {

        adapter = new EntrainementAdapter(activite.this, R.layout.items, Singleton.getInstance().entrainements);
        listEntrainement.setAdapter(adapter);

    }
}