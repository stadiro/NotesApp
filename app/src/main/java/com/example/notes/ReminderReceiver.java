package com.example.notes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "REMINDER_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Извлекаем данные из Intent
        String noteContent = intent.getStringExtra("noteContent");

        // Создаем уведомление
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Создаем канал уведомлений (для Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Напоминания",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Канал для напоминаний");
            notificationManager.createNotificationChannel(channel);
        }

        // Создаем PendingIntent для перехода по уведомлению
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Создаем само уведомление
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Напоминание!")
                .setContentText(noteContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM) // Важное уведомление
                .setAutoCancel(true) // Уведомление будет закрыто при клике
                .setContentIntent(pendingIntent) // Открыть экран по клику
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_SOUND) // Вибрация и звук по умолчанию
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC); // Уведомление будет отображаться на экране, даже если устройство заблокировано
                //.setFullScreenIntent(pendingIntent, false); // Показывает уведомление без перехода в приложение

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());

    }
}
