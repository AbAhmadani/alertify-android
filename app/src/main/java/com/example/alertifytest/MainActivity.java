package com.example.alertifytest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.alertify.AlertListener;
import com.example.alertify.AlertPosition;
import com.example.alertify.AlertType;
import com.example.alertify.Alertify;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button successButton = findViewById(R.id.button_success);
        Button errorButton = findViewById(R.id.button_error);
        Button infoButton = findViewById(R.id.button_info);
        Button warningButton = findViewById(R.id.button_warning);

        successButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alertify.showAlert(MainActivity.this, "Success Alert!", AlertType.SUCCESS, 3000, AlertPosition.TOP, new AlertListener() {
                    @Override
                    public void onShow() {
                        System.out.println("Success Alert Showing");
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("Success Alert Close");
                    }
                });
            }
        });

        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alertify.showAlert(MainActivity.this, "Error Alert!", AlertType.ERROR, 3000, AlertPosition.TOP_RIGHT, new AlertListener() {
                    @Override
                    public void onShow() {
                        System.out.println("Error Alert Showing");
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("Error Alert Close");
                    }
                });
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alertify.showAlert(MainActivity.this, "Info Alert!", AlertType.INFO, 3000, AlertPosition.BOTTOM_RIGHT, new AlertListener() {
                    @Override
                    public void onShow() {
                        System.out.println("Info Alert Showing");
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("Info Alert Close");
                    }
                });
            }
        });

        warningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alertify.showAlert(MainActivity.this, "Warning Alert!", AlertType.WARNING, 3000, AlertPosition.BOTTOM, new AlertListener() {
                    @Override
                    public void onShow() {
                        System.out.println("Warning Alert Showing");
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("Warning Alert Close");
                    }
                });
            }
        });
    }
}