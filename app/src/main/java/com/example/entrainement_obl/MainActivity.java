package com.example.entrainement_obl;

//import android.graphics.ImageDecoder;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    ImageView imageViewMain, imageViewGif;
    Button buttonActivity, button_dev;
    Switch switchimage;
    int []images;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
                ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toast toaast = Toast.makeText(this, "Bienvenue", Toast.LENGTH_SHORT);
        toaast.show();
        button_dev=findViewById(R.id.button_dev);
        buttonActivity=findViewById(R.id.buttonActivity);
        images = new int[]{R.drawable.do_it,R.drawable.bleincos_tag};
        imageViewMain = findViewById(R.id.imageViewMain);
        switchimage = findViewById(R.id.switchImage);
        switchimage.setText("Bleincos");
        imageViewGif = findViewById(R.id.imageViewGif);
        Glide.with(this)
                .load(R.drawable.do_it)
                .into(imageViewGif);
        switchimage.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            //imageViewMain.setVisibility(
                              //      View.VISIBLE);
                            switchimage.setText("GIIIIF");

                        imageViewMain.setImageResource(images[0]);
                        }else{
                            // imageViewCharacter.setVisibility(
                            //       View.INVISIBLE);
                            switchimage.setText("BLEINCOS");
                            imageViewMain.setImageResource(images[1]);
                        }
                    }
                }
        );
        button_dev.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(
                            MainActivity.this,
                            Dev_Info.class
                        );
                        startActivity(intent);
                    }
                }
        );
        buttonActivity.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(
                                MainActivity.this,
                                activite.class
                        );
                        startActivity(intent);
                    }
                }
        );
    }
    public void buttonSettings(View view){
        Log.i("Dev_Info","buttonBackClicked");
        Intent intent = new Intent(
                MainActivity.this,
                Settings.class
        );
        startActivity(intent);
    }
}