package com.example.notes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

public class ReminderHelper {
    public static void setReminder(Context context, long remindTime, String noteContent) {
        // Check if the app can schedule exact alarms (only for Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                scheduleAlarm(context, remindTime, noteContent);
            } else {
                // If not allowed, prompt the user to enable the permission
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                context.startActivity(intent);
            }
        } else {
            // For devices below Android 12, no special permission is required
            scheduleAlarm(context, remindTime, noteContent);
        }
    }

    private static void scheduleAlarm(Context context, long remindTime, String noteContent) {
        // Create Intent for sending Broadcast
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("noteContent", noteContent.length() > 10 ? noteContent.substring(0, 10) : noteContent);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) remindTime, // Unique ID for the reminder
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Schedule Alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    remindTime,
                    pendingIntent
            );
        }
    }
}

