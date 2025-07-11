package com.example.entrainement_obl;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class activite extends AppCompatActivity {
    private int duration = 120;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_activite);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button buttonLaunch;
        EditText editmin,editsec, hour, min, sec;
        int[] tab = new int[4]; // [0] minutes [1] secondes [2] répétitions [3] temps de récup.
        /*
        int[] sec= new int[10];
        int[] rep = new int[1];
        CountDownTimer countdown;
        */
        Chronometer chronos;
        editmin = findViewById(R.id.min);
        editsec = findViewById(R.id.sec);
        chronos = findViewById(R.id.chronos);
        buttonLaunch= findViewById(R.id.button_go);
        buttonLaunch.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {
                chronos.start();
                tab[0] = Integer.parseInt(editmin.getText().toString()); // Récupération des minutes
                Log.i("Activite_Min", "min_saisies");
                Log.e("Activity", "Erreur de minutes");
                tab[1] = Integer.parseInt(editsec.getText().toString()); // récupération des secondes
                System.out.println(tab[1]);
                Log.i("Activite_Sec", "sec_saisies");
                tab[2] = Integer.parseInt(editmin.getText().toString()); // récupération des répétitions
                Log.i("Activite_Min", "min_saisies");
                Log.e("Activity", "Erreur de minutes");
                System.out.println(tab[2]);
                tab[3] = Integer.parseInt(editmin.getText().toString()); // récupération des temps de récupération
                Log.i("Activite_Sec", "sec_saisies");
                if (tab[0] < 0) {
                    Toast toaast = Toast.makeText(activite.this, "La valeur rentrée en minutes est incorrecte", Toast.LENGTH_SHORT);
                    toaast.show();
                }
                if (tab[1] > 60) {
                    Toast toaast = Toast.makeText(activite.this, "La valeur rentrée en minutes est incorrecte", Toast.LENGTH_SHORT);
                    toaast.show();
                }
                if (tab[0] < 0) {
                    Toast toaast = Toast.makeText(activite.this, "La valeur rentrée en minutes est incorrecte", Toast.LENGTH_SHORT);
                    toaast.show();
                }
                setContentView(R.layout.activity_programme);
            debut_entrainement(tab);
            }
        });
    }
    public void buttonBackMain(View view){
        Log.i("Activite","buttonBackClicked");
        Intent intent = new Intent(
                activite.this,
                MainActivity.class
        );
        startActivity(intent);
    }
    public void debut_entrainement(int[] tab){
        TextView minutes = findViewById(R.id.textViewMin);
        TextView sec = findViewById(R.id.textViewSecondes);
        EditText rep = findViewById(R.id.editText_Recup);
        Log.i("Avancement","Récup des id");
        minutes.setText(tab[0]);
        sec.setText(tab[1]);
        rep.setText(tab[2]);
        //System.out.println(tab[3]);
        Toast toaast = Toast.makeText(activite.this, "Wow !", Toast.LENGTH_SHORT);
        toaast.show();
        /*
        new CountDownTimer(duration * 1000, 1000){
            @Override
            public void onFinish(){
                Toast toaast = Toast.makeText(activite.this, "Timer Finish!!", Toast.LENGTH_SHORT);
                toaast.show();
                duration = 120;
            }
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
                        final String [] hourMinSec = time.split(":");
                        minutes.setText(hourMinSec[1]);
                        sec.setText(hourMinSec[2]);
                    }
                });
            }
        }.start();
        */
    }
}