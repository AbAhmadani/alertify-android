package com.example.alertify;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class Alertify {

    private static View alertView;
    private static WindowManager windowManager;
    private static final Handler handler = new Handler();
    private static Runnable removeRunnable;

    public static void showAlert(Activity activity, String message, AlertType type, int duration, AlertListener listener) {
        // Prevent showing alert if the activity is finishing or destroyed
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        // --- MODIFIED LOGIC HERE ---
        // before showing the new alert.
        if (alertView != null) {
            removeAlert(); // Remove old alert
        }
        // Always proceed to show the new alert
        showAlertInternal(activity, message, type, duration, listener);
        // --- END MODIFIED LOGIC ---
    }

    public static void showAlert(Activity activity, String message, AlertType type, int duration) {
        // Prevent showing alert if the activity is finishing or destroyed
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        // --- MODIFIED LOGIC HERE ---
        // before showing the new alert.
        if (alertView != null) {
            removeAlert(); // Remove old alert
        }
        // Always proceed to show the new alert
        showAlertInternal(activity, message, type, duration, null);
        // --- END MODIFIED LOGIC ---
    }


    public static void showAlert(Activity activity, String message, AlertType type, AlertListener listener) {
        // Prevent showing alert if the activity is finishing or destroyed
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        // --- MODIFIED LOGIC HERE ---
        // before showing the new alert.
        if (alertView != null) {
            removeAlert(); // Remove old alert
        }
        // Always proceed to show the new alert
        showAlertInternal(activity, message, type, 3000, listener);
        // --- END MODIFIED LOGIC ---
    }

    public static void showAlert(Activity activity, String message, AlertType type) {
        // Prevent showing alert if the activity is finishing or destroyed
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        // --- MODIFIED LOGIC HERE ---
        // before showing the new alert.
        if (alertView != null) {
            removeAlert(); // Remove old alert
        }
        // Always proceed to show the new alert
        showAlertInternal(activity, message, type, 3000, null);
        // --- END MODIFIED LOGIC ---
    }


    private static void showAlertInternal(Activity activity, String message, AlertType type, int duration, AlertListener listener) {
        // Get the WindowManager service
        windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        // Inflate the custom alert layout
        LayoutInflater inflater = LayoutInflater.from(activity);
        alertView = inflater.inflate(R.layout.alertify_view, null);

        // Initialize UI components
        TextView alertMessage = alertView.findViewById(R.id.alert_message);
        ImageView alertIcon = alertView.findViewById(R.id.alert_icon);
        ImageView closeButton = alertView.findViewById(R.id.alert_close_button);

        // Set the alert message
        alertMessage.setText(message);

        // Determine background, icon, and icon color based on alert type
        int backgroundRes, iconRes, iconColor;
        switch (type) {
            case SUCCESS:
                backgroundRes = R.drawable.success_alert_bg;
                iconColor = R.color.success_dark;
                iconRes = R.drawable.ic_success;
                break;
            case ERROR:
                backgroundRes = R.drawable.error_alert_bg;
                iconColor = R.color.error_dark;
                iconRes = R.drawable.ic_error;
                break;
            case WARNING:
                backgroundRes = R.drawable.warning_alert_bg;
                iconColor = R.color.warning_dark;
                iconRes = R.drawable.ic_warning;
                break;
            default: // INFO type
                backgroundRes = R.drawable.info_alert_bg;
                iconColor = R.color.info_dark;
                iconRes = R.drawable.ic_info;
                break;
        }

        // Apply determined resources and colors
        alertView.setBackgroundResource(backgroundRes);
        alertIcon.setImageResource(iconRes);
        alertIcon.setColorFilter(ContextCompat.getColor(activity, iconColor));
        closeButton.setColorFilter(ContextCompat.getColor(activity, iconColor));

        // Set click listener for the close button to dismiss the alert
        closeButton.setOnClickListener(v -> {
            removeAlert();
            if (listener != null) listener.onCancel(); // Notify listener on manual cancel
        });

        // Define layout parameters for the alert window
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                Resources.getSystem().getDisplayMetrics().widthPixels - 20, // Width
                WindowManager.LayoutParams.WRAP_CONTENT, // Height
                WindowManager.LayoutParams.TYPE_APPLICATION, // Window type (within the application)
                // Flags: NOT_FOCUSABLE allows clicks to pass through to underlying views,
                // LAYOUT_IN_SCREEN ensures it's positioned relative to the screen.
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT // Allows transparency
        );

        // Set gravity based on the desired position
        params.gravity = Gravity.BOTTOM;
        // Add vertical and horizontal offsets for padding
        params.y = 10; // Vertical offset

        // Add the alert view to the window
        windowManager.addView(alertView, params);

        if (listener != null) listener.onShow(); // Notify listener that the alert is shown

        // Remove any existing scheduled removal runnable to prevent conflicts
        if (removeRunnable != null) {
            handler.removeCallbacks(removeRunnable);
        }

        // Schedule the alert to be removed automatically after the specified duration
        removeRunnable = () -> {
            removeAlert(); // Remove the alert
            if (listener != null) listener.onCancel(); // Notify listener on auto-cancel
        };

        handler.postDelayed(removeRunnable, duration);
    }


    private static void removeAlert() {
        if (alertView != null && windowManager != null) {
            try {
                windowManager.removeView(alertView);
            } catch (Exception ignored) {
                // Catching IllegalArgumentException if view is already removed.
            } finally {
                alertView = null;
            }
        }

        if (removeRunnable != null) {
            handler.removeCallbacks(removeRunnable);
            removeRunnable = null;
        }
    }
}
