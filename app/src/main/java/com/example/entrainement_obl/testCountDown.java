package com.example.entrainement_obl;

import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class testCountDown extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_count_down2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText etext1, etext2, etext3;
        etext1= findViewById(R.id.editTextNumberSigned);
        etext2= findViewById(R.id.editTextNumberSigned2);
        etext3= findViewById(R.id.editTextNumberSigned3);
    }
    public void Go(View view){
        Chronometer chronometer= findViewById(R.id.chronometter);
        chronometer.setCountDown(true);
        chronometer.start();
        long l = 000L;
        chronometer.setBase(l);
        if(chronometer.getText().equals("-00.05")){
            chronometer.stop();
        }
    }
}