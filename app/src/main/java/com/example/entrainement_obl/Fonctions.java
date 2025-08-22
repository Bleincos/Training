package com.example.entrainement_obl;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.Toast;

public class Fonctions {
    private Fonctions() {
    }
    private static final Fonctions instance = new Fonctions();

}

/*
Garbage :
@Override
            public void onTick(long millisUntilFinished) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String time = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                        -TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                                        -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)), Locale.getDefault());
                        //final String [] hourMinSec = time.split(":");
                        //minutes.setText(hourMinSec[1]);
                        //sec.setText(hourMinSec[2]);
                    }
                });
            }

 */
