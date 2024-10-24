package com.example.alertify;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class Alertify extends LinearLayout {

    private TextView alertMessage;
    private ImageView alertIcon;
    private ImageView closeButton;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private AlertListener alertListener; // To store the alert listener

    private static final long ANIMATION_DURATION = 200; // Animation duration in milliseconds

    public Alertify(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Alertify(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        // Inflate the custom alert layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.alertify_view, this, true);

        // Get views for message, icon, and close button
        alertMessage = view.findViewById(R.id.alert_message);
        alertIcon = view.findViewById(R.id.alert_icon);
        closeButton = view.findViewById(R.id.alert_close_button);

        // Initially hide the alert container
        setVisibility(View.GONE);

        // Close button action
        closeButton.setOnClickListener(v -> hideAlertWithAnimation());
    }

    // Show an alert with an optional type, message, duration, and alert listener
    public void showAlert(String message, AlertType type, Integer duration, AlertListener alertListener) {
        alertMessage.setText(message);
        setAlertStyle(type);

        // Store the alert listener for later use when dismissing
        this.alertListener = alertListener;

        // Show the alert with an animation
        showAlertWithAnimation();

        // Cancel any previously scheduled hide operations
        handler.removeCallbacksAndMessages(null);

        // Hide the alert after a certain duration if provided
        if (duration != null && duration > 0) {
            handler.postDelayed(this::hideAlertWithAnimation, duration);
        }
    }

    // Show an alert with an optional type, message, and alert listener
    public void showAlert(String message, AlertType type, AlertListener alertListener) {
        alertMessage.setText(message);
        setAlertStyle(type);

        // Store the alert listener for later use when dismissing
        this.alertListener = alertListener;

        // Show the alert with an animation
        showAlertWithAnimation();

        // Cancel any previously scheduled hide operations
        handler.removeCallbacksAndMessages(null);
    }


    // Overloaded method to make duration and alertListener optional
    public void showAlert(String message, AlertType type) {
        showAlert(message, type, null, null);
    }

    public void showAlert(String message, AlertType type, int duration) {
        showAlert(message, type, duration, null);
    }

    private void showAlertWithAnimation() {
        // Make the alert visible and set its height to 0 initially
        setVisibility(View.VISIBLE);

        // Measure the full height of the view
        measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = getMeasuredHeight();

        // Set the height to 0 to start the animation
        getLayoutParams().height = 0; // Keep this as 0 to expand from the bottom
        requestLayout(); // Ensure the layout is updated

        // Animate the height from 0 to the full measured height
        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(animation -> {
            // Update the height of the layout during the animation
            getLayoutParams().height = (int) animation.getAnimatedValue();
            requestLayout(); // Re-layout the view with the updated height
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (alertListener != null) {
                    alertListener.onShow(); // Call onShow callback after the alert is fully shown
                }
            }
        });

        animator.start();
    }


    private void hideAlertWithAnimation() {
        // Get the current height of the alert
        final int initialHeight = getHeight();

        // Animate the height from the full height to 0 (collapsing)
        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(animation -> {
            // Update the height of the layout during the animation
            getLayoutParams().height = (int) animation.getAnimatedValue();
            requestLayout(); // Re-layout the view with the updated height
        });

        // Add a listener to handle the end of the animation
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Ensure the view is gone when animation ends
                setVisibility(View.GONE);
                // Reset the height back to wrap_content for future showAlert calls
                getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                requestLayout();

                if (alertListener != null) {
                    alertListener.onCancel(); // Call onCancel callback after the alert is dismissed
                }
            }
        });

        animator.start();
    }



    // Set the style based on the alert type (affects only inner content)
    private void setAlertStyle(AlertType type) {
        int iconRes;
        int iconColor;

        // Set style based on the type of alert
        switch (type) {
            case SUCCESS:
                setBackgroundResource(R.drawable.success_alert_bg);
                iconColor = R.color.success_dark;
                iconRes = R.drawable.ic_success;
                break;
            case ERROR:
                setBackgroundResource(R.drawable.error_alert_bg);
                iconColor = R.color.error_dark;
                iconRes = R.drawable.ic_error;
                break;
            case WARNING:
                setBackgroundResource(R.drawable.warning_alert_bg);
                iconColor = R.color.warning_dark;
                iconRes = R.drawable.ic_warning;
                break;
            default:
                setBackgroundResource(R.drawable.info_alert_bg);
                iconColor = R.color.info_dark;
                iconRes = R.drawable.ic_info;
                break;
        }

        // Set background color and icon for inner content
        alertIcon.setImageResource(iconRes);
        alertIcon.setColorFilter(ContextCompat.getColor(getContext(), iconColor), android.graphics.PorterDuff.Mode.SRC_IN); // Tint the icon
        closeButton.setColorFilter(ContextCompat.getColor(getContext(), iconColor), android.graphics.PorterDuff.Mode.SRC_IN); // Tint the close button
    }
}
