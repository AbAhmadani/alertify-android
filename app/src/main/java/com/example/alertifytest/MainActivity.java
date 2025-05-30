package com.example.alertifytest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.alertify.AlertType;
import com.example.alertify.Alertify;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button successButton = findViewById(R.id.button_success);
        Button errorButton = findViewById(R.id.button_error);
        Button infoButton = findViewById(R.id.button_info);
        Button warningButton = findViewById(R.id.button_warning);

        successButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alertify.showAlert(MainActivity.this, "Success Alert!", AlertType.SUCCESS);
            }
        });

        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alertify.showAlert(MainActivity.this, "Error Alert!", AlertType.ERROR);
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alertify.showAlert(MainActivity.this, "Info Alert!", AlertType.INFO);
            }
        });

        warningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alertify.showAlert(MainActivity.this, "Warning Alert!", AlertType.WARNING);
            }
        });
    }
}