package ma.itirc.vaxmind.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import ma.itirc.vaxmind.R;

public class NotificationUtils {

    private static final String CHANNEL_ID = "vaccines";

    public static void show(Context ctx, String vaccineName) {
        NotificationManager nm = ctx.getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "Rappels vaccins",
                    NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(ch);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Rappel vaccination")
                .setContentText("Votre vaccin \"" + vaccineName + "\" est prévu dans une semaine.")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        nm.notify((int) System.currentTimeMillis(), builder.build());
    }
}
