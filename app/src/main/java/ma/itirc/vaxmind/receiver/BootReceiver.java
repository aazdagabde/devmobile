package ma.itirc.vaxmind.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.WorkManager;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // WorkManager se reprogramme automatiquement,
        // ici on pourrait relancer une tâche si nécessaire.
    }
}
