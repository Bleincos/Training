package com.example.entrainement_obl;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import java.nio.file.Files;

public class activite extends AppCompatActivity implements SensorEventListener {
    public long elapseRealTime = SystemClock.elapsedRealtime();
    public SensorManager mySensorManager;
    public Sensor sensorLight;
    public static int valueMaxBrg = 255;
    public int[] tab = new int[5]; // [0] répétitions [1] minutes [2] secondes [3] temps de récup [4] copies des répétitions pour afficher au démarrage.
    private Boolean success = false, bool, stop, vpause = false, boolCh, norecup,init, speaker; //success and bool are used for the screenBrighness, stop to stop the training, vpause to pause, and boolCh for the chronometers, norecup is used to determine if the user want to recup (if not a function isn't called) and init is used to know if the activity is initialized or not (to set digit in the textview).
    public MediaPlayer buzz, bip;
    public long chronotrainBase = 0;
    public CharSequence tempsecoule = "0.00";
    private SeekBar seekbar;
    EntrainementAdapter adapter;
    ListView listEntrainement;
    public int  position=0;
    //public long valTimer=0;

    /**
     * method called on creation
     * @param savedInstanceState x
     */
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
        init = true;
        main();
    }

    /**
     * main, allows to loop and go back to the settings of the training
     */
    public void main() {
        setContentView(R.layout.activity_activite_original);
        Button buttonLaunch;
        EditText editRep, editMin, editSec, editRec, editName;
        ImageButton soundButton = findViewById(R.id.imageButtonSpeaker);
        AudioManager audioManager = (AudioManager) activite.this.getSystemService(AUDIO_SERVICE); // min value 0, max value 16
        editRep = findViewById(R.id.Rep);
        editMin = findViewById(R.id.min);
        editSec = findViewById(R.id.sec);
        editRec = findViewById(R.id.Rec);
        editName = findViewById(R.id.Name);
        buttonLaunch = findViewById(R.id.button_go);
        seekbar = findViewById(R.id.seekBarSound);
        stop = false;
        Chronometer chronometerSet = findViewById(R.id.chronoSet);
        chronometerSet.setText(tempsecoule);
        loadData();
        updateListView();
        if (!init){
            editRep.setText(""+tab[4]);
            editMin.setText(""+tab[1]);
            editSec.setText(""+tab[2]);
            editRec.setText(""+tab[3]);
        }
        listEntrainement.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView,
                                           View view, int i, long l) {
                Toast toast = Toast.makeText(activite.this, "Entrainement :"+Singleton.getInstance().entrainements.get(i).name+" supprimé",Toast.LENGTH_SHORT);
                toast.show();
                Singleton.getInstance().entrainements.remove(i);
                updateListView();
                return true;
            }
        });
        listEntrainement.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editName.setText(""+Singleton.getInstance().entrainements.get(position).name);
                editRep.setText(""+Singleton.getInstance().entrainements.get(position).repetitions);
                editMin.setText(""+Singleton.getInstance().entrainements.get(position).minutes);
                editSec.setText(""+Singleton.getInstance().entrainements.get(position).secondes);
                editRec.setText(""+Singleton.getInstance().entrainements.get(position).recuperation);
            }
        });
        buttonLaunch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkValues()){
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
                    if(editRec.getText().toString().equals("0")){
                        norecup=true;
                    }else{
                        norecup=false;
                    }
                    init=false;
                    tab[4]=tab[0];
                    demarrage();
                }
            }
        });
        seekbar.setMax(16);
        seekbar.setMin(0);
        if(audioManager.isMusicActive()){
            Toast.makeText(activite.this,""+audioManager.isMusicActive(),Toast.LENGTH_SHORT).show();
            speaker=false;
            soundButton.setImageResource(R.drawable.silent_off);
        }
        speaker=true;
        seekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        seekbar.setKeyProgressIncrement(1);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * This function take three parameters in entry to react if the user change the luminosity via the bar
             * @param seekBar b
             * @param progress b
             * @param fromUser b
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
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
                if(seekBar.getProgress()==audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)){

                }
                else if (seekBar.getProgress()>audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) {
                    for (int i = 0; i < (seekBar.getProgress() - audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)); i++) {
                        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                    }
                }else{
                    for(int i=0;i<(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)-seekBar.getProgress());i++){
                           audioManager.adjustVolume(AudioManager.ADJUST_LOWER,AudioManager.FLAG_PLAY_SOUND);
                    }
                }
                if(seekBar.getProgress()==0){
                    soundButton.setImageResource(R.drawable.silent_off);
                }
                soundButton.setImageResource(R.drawable.speaker_on);
            }
        });
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(speaker){
                    Toast.makeText(activite.this,"dans if",Toast.LENGTH_SHORT).show();
                    soundButton.setImageResource(R.drawable.speaker_on);
                    audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_PLAY_SOUND);
                    speaker =false;
                }else {
                    Toast.makeText(activite.this, "hors if", Toast.LENGTH_SHORT).show();
                    soundButton.setImageResource(R.drawable.silent_off);
                    audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_PLAY_SOUND);
                    speaker = true;
                }
            }
        });
    }

    /**
     * Function to return to the main activity
     *
     * @param view
     */
    public void buttonBackMain(View view) throws FileNotFoundException {
        Log.i("Activite", "buttonBackClicked");
        saveData();
        Intent intent = new Intent(

                activite.this,
                MainActivity.class
        );
        startActivity(intent);
        finish();
    }

    /**
     * method at the beginning of the training (just graphic and sound)
     */
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
        //ImageView ipause= findViewById(R.id.imageButton_pause);
        buzz = MediaPlayer.create(this, R.raw.bip);
        bip = MediaPlayer.create(this, R.raw.longbip);
        MediaPlayer endbip = MediaPlayer.create(this, R.raw.endbip);
        Chronometer chronosTraining = findViewById(R.id.chronoTrain);
        if (boolCh) {
            chronosTraining.setBase(chronotrainBase);
        }
        chronosTraining.start();
        Log.i("Avancement", "Récup des id");
        rep.setText("Répétitions : " + tab[0]);
        minutes.setText("" + tab[1]);
        sec.setText("" + tab[2]);
        //valTimer = tab[1] * 60 * 1000 + (tab[2] + 1) * 1000;
        new CountDownTimer(tab[1] * 60 * 1000 + (tab[2] + 1) * 1000, 1000) {
            @Override
            public void onFinish() {
                if (!vpause) {
                    bip.start();
                    chronosTraining.stop();
                    chronotrainBase = chronosTraining.getBase();
                    if (norecup) {
                        if (tab[0] != 1) { // machine count the 0 while everybody starts from 1 so we finish the last rep at 1 (function call at the end of the set so)
                            tab[0]--;
                            bip.start();
                            boolCh = true;
                            debut_entrainement();
                        } else {
                            endbip.start();
                            Toast toaast = Toast.makeText(activite.this, "TRAINING COMPLETE, WOW!", Toast.LENGTH_SHORT);
                            toaast.show();
                            main();
                        }
                    } else {
                        callRecup();
                    }
                } else {
                    debut_entrainement();
                }
            }

            @Override
            public void onTick(long l) {
                /* function to stop the countdown, need fix and not sure to keep
                if (vpause) {
                    valTimer = l;
                    ipause.setImageResource(R.drawable.play);
                    chronosTraining.stop();
                } else {
                    ipause.setImageResource(R.drawable.pause);
                }

                 */
                if (l / 61000 >= 1) {
                    sec.setText("" + (l % 60000) / 1000);
                    minutes.setText("" + l / 60000);
                } else {
                    minutes.setText("0");
                    sec.setText("" + l / 1000);
                }
                if (l / 1000 < 4) {
                    buzz.start();
                }
                if (l / 1000 < 1) {
                    onFinish();
                    cancel();
                }
                if (stop) {
                    cancel();
                    main();
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


        new CountDownTimer((long) (tab[3]+1) * 1000, 1000) {
            @Override
            public void onFinish() {
                chronotrainBase =chronoRecup.getBase();
                if (tab[0] != 1) { // machine count the 0 while humans starts from 1 so we finish the last rep at 1 (function call at the end of the set so)
                    tab[0]--;
                    bip.start();
                    boolCh = true;
                    debut_entrainement();
                } else {
                    endbip.start();
                    Toast toaast = Toast.makeText(activite.this, "TRAINING COMPLETE, WOW!", Toast.LENGTH_SHORT);
                    toaast.show();
                    //chronotrainBase =chronoRecup.getBase();
                    chronoRecup.stop();
                    tempsecoule = chronoRecup.getText();
                    main();
                }
            }

            public void onTick(long l) {
                if (l / 1000 < 4) {
                    buzz.start();
                }
                if (l / 1000 < 1) {
                    onFinish();
                    cancel();
                }
                tempsRest.setText("" + l / 1000);
                if (stop) {
                    cancel();
                    main();
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
        //updateListView();
        mySensorManager.registerListener(activite.this, sensorLight, mySensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * explicit
     */
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
    }

    /**
     * Method to pause and resume the timer
     * @param View
     */
    /*
    public void callPause(View View) {
        buzz.stop();
        bip.stop();
        if(vpause){
                //ipause.setImageResource(R.drawable.pause);
            }
        vpause = !vpause;
    }

     */

    /**
     * method to save a training in the arraylist
     * @param view
     */
    public void saveAtraining(View view) {
        EditText editRep, editMin, editSec, editRec, editName;
        String name;
        int rep, min, sec, rec;
        editRep = findViewById(R.id.Rep);
        editMin = findViewById(R.id.min);
        editSec = findViewById(R.id.sec);
        editRec = findViewById(R.id.Rec);
        editName = findViewById(R.id.Name);

            if(checkValues()) {
            name = editName.getText().toString();
            rep = Integer.parseInt(editRep.getText().toString());
            min = Integer.parseInt(editMin.getText().toString());
            sec = Integer.parseInt(editSec.getText().toString());
            rec = Integer.parseInt(editRec.getText().toString());
            //if(Singleton.getInstance().state==Boolean.TRUE) // on edite?
            EntrainementType entrainementType = new EntrainementType(name, rep, min, sec, rec);
            Singleton.getInstance().entrainements.add(entrainementType);
            position++;
            Singleton.getInstance().i=position;
            updateListView();
            saveData();
        }
    }

    /**
     * Method to update the list of trainings on the display
     */
    void updateListView() {
        listEntrainement = findViewById(R.id.listeEntrainement);
        adapter = new EntrainementAdapter(activite.this, R.layout.items, Singleton.getInstance().entrainements);
        listEntrainement.setAdapter(adapter);

    }

    /**
     * Method to verify if the values set for the training or saved are legits
     * @return OK/NOK
     */
    boolean checkValues(){
        boolean result=true;
        EditText editRep, editMin, editSec, editRec, editName;
        editRep = findViewById(R.id.Rep);
        editMin = findViewById(R.id.min);
        editSec = findViewById(R.id.sec);
        editRec = findViewById(R.id.Rec);
        editName = findViewById(R.id.Name);
        int maxvalueint = 65535;
        if(editRep.getText().toString().isEmpty() || Integer.parseInt(editRep.getText().toString())>maxvalueint || editMin.getText().toString().equals("") || editSec.getText().toString().equals("") || editRec.getText().toString().equals("") || editRep.getText().toString().equals(("0"))) {
            Toast toaast = Toast.makeText(activite.this, "Les valeurs saisies sont incorrectes", Toast.LENGTH_SHORT);
            toaast.show();
            result=false;
        }
        return result;
    }

    /**
     * Method to save the trainings on the devices memory
     */
    public void saveData() {
        try {
            FileOutputStream file = openFileOutput("myTrainings.txt", MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file);

            for (int i = 0; i < Singleton.getInstance().entrainements.size(); i++) {
                outputStreamWriter.write(String.format("%s,%d,%d,%d,%d", Singleton.getInstance().entrainements.get(i).getName(), Singleton.getInstance().entrainements.get(i).getRepetitions(), Singleton.getInstance().entrainements.get(i).getMinutes(), Singleton.getInstance().entrainements.get(i).getSecondes(), Singleton.getInstance().entrainements.get(i).getRecuperation())+ "\n");
            }

            outputStreamWriter.flush();
            outputStreamWriter.close();
            Toast.makeText(activite.this, "Programme Sauvegardé", Toast.LENGTH_SHORT)
                    .show();

        } catch (IOException e) {
            Toast.makeText(activite.this, e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Method to load trainings from device memory
     */
    public void loadData() {
        Singleton.getInstance().entrainements.clear();

        File file = getApplicationContext()
                .getFileStreamPath("myTrainings.txt");
        String lineFromfile;

        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("myTrainings.txt")));

                while ((lineFromfile = reader.readLine()) != null) {
                    StringTokenizer tokenizer = new StringTokenizer(lineFromfile, ",");
                    EntrainementType entrainementType = new EntrainementType(tokenizer.nextToken(), Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()));
                    Singleton.getInstance().entrainements.add(entrainementType);
                }
                reader.close();
                updateListView();
            } catch (IOException e) {
                Toast.makeText(activite.this, e.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }
        }
        if(Singleton.getInstance().entrainements.isEmpty()) {
            EntrainementType entrainementParDefaut = new EntrainementType("Entrainement par défaut", 5, 0, 40, 20);
            Singleton.getInstance().entrainements.add(entrainementParDefaut);
            position++;
        }
    }
}