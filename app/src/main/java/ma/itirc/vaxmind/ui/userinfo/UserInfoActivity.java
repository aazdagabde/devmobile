package ma.itirc.vaxmind.ui.userinfo;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ma.itirc.vaxmind.R;
import ma.itirc.vaxmind.data.database.AppDatabase;
import ma.itirc.vaxmind.data.entity.User;

public class UserInfoActivity extends AppCompatActivity {

    private final ExecutorService io = Executors.newSingleThreadExecutor();

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_user_info);

        long userId = getIntent().getLongExtra("userId", -1);
        TextView tv = findViewById(R.id.tvUserInfo);

        io.execute(() -> {
            User u = AppDatabase.get(this).userDao().findById(userId);
            runOnUiThread(() -> {
                if (u != null) {
                    String msg = getString(R.string.user_info_template,
                            u.firstName, u.lastName, u.email, u.birthDate,
                            u.city, u.parent1Name, u.parent2Name,
                            u.parentPhones, u.parentCins,
                            (u.userCin == null ? "-" : u.userCin));
                    tv.setText(msg);
                }
            });
        });
    }
}
