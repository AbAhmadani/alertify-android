package com.example.alertify;

import android.app.Activity;
import android.content.Context;
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
    private static AlertPosition currentAlertPosition = AlertPosition.BOTTOM; // Default position
    public static void showAlert(Activity activity, String message, AlertType type, int duration, AlertPosition position, AlertListener listener) {
        // Prevent showing alert if the activity is finishing or destroyed
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        // --- MODIFIED LOGIC HERE ---
        // If an alert is already visible, remove it immediately (skip animation)
        // before showing the new alert.
        if (alertView != null) {
            removeAlertImmediate(); // Remove old alert without animation
        }
        // Always proceed to show the new alert, which will animate in.
        showAlertInternal(activity, message, type, duration, position, listener);
        // --- END MODIFIED LOGIC ---
    }

    private static void showAlertInternal(Activity activity, String message, AlertType type, int duration, AlertPosition position, AlertListener listener) {
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
                WindowManager.LayoutParams.WRAP_CONTENT, // Width
                WindowManager.LayoutParams.WRAP_CONTENT, // Height
                WindowManager.LayoutParams.TYPE_APPLICATION, // Window type (within the application)
                // Flags: NOT_FOCUSABLE allows clicks to pass through to underlying views,
                // LAYOUT_IN_SCREEN ensures it's positioned relative to the screen.
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT // Allows transparency
        );

        // Set gravity based on the desired position
        params.gravity = getGravity(position);
        // Add vertical and horizontal offsets for padding
        params.y = 60; // Vertical offset
        params.x = 30; // Horizontal offset

        // Store the current alert position for removal animation
        currentAlertPosition = position;

        // Initial state for animation: off-screen and fully transparent
        alertView.setTranslationY(getTranslationY(position));
        alertView.setAlpha(0f);

        // Add the alert view to the window
        windowManager.addView(alertView, params);

        // Animate the alert into view (slide in and fade in)
        alertView.animate()
                .translationY(0) // Slide to its final position
                .alpha(1f)       // Fade in
                .setDuration(300) // Animation duration
                .start();

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

    private static int getGravity(AlertPosition position) {
        switch (position) {
            case TOP:
                return Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            case BOTTOM:
                return Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            case CENTER:
                return Gravity.CENTER;
            case TOP_LEFT:
                return Gravity.TOP | Gravity.LEFT;
            case TOP_RIGHT:
                return Gravity.TOP | Gravity.RIGHT;
            case BOTTOM_LEFT:
                return Gravity.BOTTOM | Gravity.LEFT;
            case BOTTOM_RIGHT:
                return Gravity.BOTTOM | Gravity.RIGHT;
            default: // Default to BOTTOM_CENTER
                return Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        }
    }
    private static int getTranslationY(AlertPosition position) {
        switch (position) {
            case TOP:
            case TOP_LEFT:
            case TOP_RIGHT:
                return -300; // Slide out upwards
            case CENTER:
                return 0; // No Animation
            default:
                return 300; // Slide out downwards
        }
    }

    public static void removeAlert() {
        if (alertView != null && windowManager != null) {
            int endY = getTranslationY(currentAlertPosition); // Get the end Y-translation for slide-out

            // Animate the alert out (slide out and fade out)
            alertView.animate()
                    .translationY(endY) // Slide to off-screen position
                    .alpha(0f)          // Fade out
                    .setDuration(300)   // Animation duration
                    .withEndAction(() -> {
                        // This runnable executes after the animation completes.
                        // Add a small delay to ensure the animation is fully rendered before removing the view.
                        handler.postDelayed(() -> {
                            try {
                                if (alertView != null && windowManager != null) {
                                    windowManager.removeView(alertView); // Remove the view from the window
                                }
                            } catch (Exception ignored) {
                                // Catching IllegalArgumentException if view is already removed,
                                // or other exceptions. Ignoring them to prevent crashes.
                            } finally {
                                alertView = null; // Clear the reference to the alert view
                            }
                        }, 50); // Small delay (e.g., 50ms)
                    })
                    .start();
        }

        // Remove any pending auto-removal runnable
        if (removeRunnable != null) {
            handler.removeCallbacks(removeRunnable);
            removeRunnable = null;
        }
    }

    private static void removeAlertImmediate() {
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
