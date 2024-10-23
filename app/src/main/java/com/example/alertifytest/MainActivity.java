package com.example.alertifytest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.alertify.AlertType;
import com.example.alertify.Alertify;

public class MainActivity extends AppCompatActivity {
    private Alertify alertify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        alertify = findViewById(R.id.alertify); // Ensure the Alertify view is in your main layout

        Button successButton = findViewById(R.id.button_success);
        Button errorButton = findViewById(R.id.button_error);
        Button infoButton = findViewById(R.id.button_info);
        Button warningButton = findViewById(R.id.button_warning);

        successButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertify.showAlert("Success Alert!", AlertType.ERROR, 3000);
            }
        });

        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertify.showAlert("Error Alert!", AlertType.ERROR, 3000);
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertify.showAlert("Info Alert!", AlertType.INFO, 3000);
            }
        });

        warningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertify.showAlert("Warning Alert!", AlertType.WARNING, 3000);
            }
        });
    }
}