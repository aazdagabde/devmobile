package ma.itirc.vaxmind.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import ma.itirc.vaxmind.util.NotificationUtils;

public class VaccineReminderWorker extends Worker {

    public VaccineReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String vaccine = getInputData().getString("vaccineName");
        if (vaccine != null) {
            NotificationUtils.show(getApplicationContext(), vaccine);
        }
        return Result.success();
    }
}
