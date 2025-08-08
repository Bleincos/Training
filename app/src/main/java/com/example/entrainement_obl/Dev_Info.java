package com.example.entrainement_obl;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.hardware.*;
import android.os.Build.VERSION_CODES;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.barteksc.pdfviewer.PDFView;

public class Dev_Info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dev_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        PDFView pdfV= findViewById(R.id.PdfView);
        pdfV.fromAsset("Entrainement_Dev_info.pdf").load();
    }

    /**
     * Function to return to main activity
     * @param view
     */
    public void buttonBackMain(View view){
        Log.i("Dev_Info","buttonBackClicked");
        Intent intent = new Intent(
                Dev_Info.this,
                MainActivity.class
        );
        startActivity(intent);
    }

    /**
     * Function to show context, just for fun
     */
    public void tooaster(){
        Log.i("Dev_info","Imageclickde");
        Toast toaster = Toast.makeText(this,"Oui c'est bien le dev :)",Toast.LENGTH_SHORT);
        toaster.show();
    }

}
