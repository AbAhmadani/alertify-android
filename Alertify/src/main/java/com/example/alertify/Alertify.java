package com.example.alertify;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class Alertify extends LinearLayout {

    private TextView alertMessage;
    private ImageView alertIcon;
    private ImageView closeButton;
    private final Handler handler = new Handler();
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
        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAlertWithAnimation();
            }
        });
    }

    // Show an alert with a specific type, message, and duration
    public void showAlert(String message, AlertType type, int duration) {
        alertMessage.setText(message);
        setAlertStyle(type);

        // Show the alert with an animation
        showAlertWithAnimation();

        // Cancel any previously scheduled hide operations
        handler.removeCallbacksAndMessages(null);

        // Hide the alert after a certain duration
        if (duration > 0) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideAlertWithAnimation();
                }
            }, duration);
        }
    }

    // Show the alert by expanding the height from 0 to full height
    private void showAlertWithAnimation() {
        // Prepare the alert by setting its height to 0 initially
        setVisibility(View.VISIBLE);
        // Measure the full height of the view
        measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = getMeasuredHeight();

        // Animate the height from 0 to the full measured height (expanding)
        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                // Update the height of the layout during the animation
                getLayoutParams().height = (int) animation.getAnimatedValue();
                requestLayout(); // Re-layout the view with the updated height
            }
        });

        animator.start();
    }


    // Hide the alert by collapsing the height back to 0
    private void hideAlertWithAnimation() {
        final int initialHeight = getHeight();

        // Animate the height from the full height to 0 (collapsing from the top)
        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                // Update the height of the layout during the animation
                getLayoutParams().height = (int) animation.getAnimatedValue();
                requestLayout(); // Re-layout the view with the updated height
            }
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
