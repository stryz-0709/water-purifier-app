package com.snig.waterpurifier;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

public class RemindFragment extends Fragment {

    private static final String CHANNEL_ID = "WaterNotificationChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final long DEFAULT_INTERVAL = 60 * 60 * 1000; // Default interval of 1 hour
    private static final String ACTION_NOTIFICATION_CLICK = "ACTION_NOTIFICATION_CLICK";

    private Handler handler;
    private Runnable runnable;
    EditText intervalPref;
    Button intervalBtn;
    View mView;
    int interval;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_remind, container, false);
        intervalPref = mView.findViewById(R.id.interval_pref);
        intervalBtn = mView.findViewById(R.id.interval_btn);

        intervalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interval = Integer.parseInt(intervalPref.getText().toString());
                startNotificationTimer(interval);
            }
        });
        return mView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopNotificationTimer();
    }

    private void startNotificationTimer(int interval) {
        stopNotificationTimer(); // Stop previous timer if running

        // Retrieve the interval from user preferences or use the default
        long intervalMillis = (long) interval * 60 * 1000;
        if (intervalMillis <= 0) {
            intervalMillis = DEFAULT_INTERVAL;
        }

        // Schedule the notification
        long finalIntervalMillis = intervalMillis;
        runnable = new Runnable() {
            @Override
            public void run() {
                showWaterNotification();
                handler.postDelayed(this, finalIntervalMillis);
            }
        };
        handler.postDelayed(runnable, intervalMillis);
    }

    private void stopNotificationTimer() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
        }
    }

    private void showWaterNotification() {
        // Create a notification channel (required for Android 8.0 and above)
        createNotificationChannel();

        // Create an intent for the notification click event
        Intent notificationIntent = new Intent(requireContext(), MainActivity.class);
        notificationIntent.setAction(ACTION_NOTIFICATION_CLICK);
        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_water_drop_24)
                .setContentTitle("Drink Water")
                .setContentText("It's time to drink water!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void createNotificationChannel() {
        CharSequence name = "Water Notifications";
        String description = "Reminds you to drink water";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}