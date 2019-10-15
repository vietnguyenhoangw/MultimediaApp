package com.example.changeprofilepicapp;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import org.michaelbel.bottomsheet.BottomSheet;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.profile_image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet();
            }
        });
    }

    public void bottomSheet() {
        BottomSheet.Builder builder = new BottomSheet.Builder(this);
        builder.setTitle("Test")
                .show();
    }
}
