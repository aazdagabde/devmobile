package ma.itirc.vaxmind.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ma.itirc.vaxmind.R;
import ma.itirc.vaxmind.data.database.AppDatabase;
import ma.itirc.vaxmind.data.entity.User;
import ma.itirc.vaxmind.ui.auth.LoginActivity;
import ma.itirc.vaxmind.ui.chat.ChatbotActivity;
import ma.itirc.vaxmind.ui.settings.SettingsActivity;
import ma.itirc.vaxmind.ui.userinfo.UserInfoActivity;
import ma.itirc.vaxmind.ui.vaccine.VaccineActivity;
import ma.itirc.vaxmind.util.SessionManager;

public class MainActivity extends AppCompatActivity {

    private long userId;
    private TextView txtHi;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_main);

        /* ----------  Session obligatoire ---------- */
        userId = SessionManager.current(this);
        if (userId == -1) {                          // aucune session → retour login
            startActivity(new Intent(this, LoginActivity.class));
            finish(); return;
        }

        /* ----------  Références vues ---------- */
        txtHi = findViewById(R.id.txtHi);
        MaterialButton btnUser = findViewById(R.id.btnUserInfo);
        MaterialButton btnVacc = findViewById(R.id.btnVaccines);
        MaterialButton btnSet  = findViewById(R.id.btnSettings);
        MaterialButton btnOut  = findViewById(R.id.btnLogout);
        FloatingActionButton fabChat = findViewById(R.id.fabChat);

        /* Bonjour Prénom Nom */
        io.execute(() -> {
            User u = AppDatabase.get(this).userDao().findById(userId);
            if (u != null) runOnUiThread(() ->
                    txtHi.setText(getString(R.string.main_hi, u.firstName, u.lastName)));
        });

        /* Navigation */
        btnUser.setOnClickListener(v -> launch(UserInfoActivity.class));
        btnVacc.setOnClickListener(v -> launch(VaccineActivity.class));
        btnSet .setOnClickListener(v -> launch(SettingsActivity.class));
        fabChat.setOnClickListener(v -> launch(ChatbotActivity.class));

        /* Déconnexion */
        btnOut.setOnClickListener(v -> askLogout());
    }

    private void launch(Class<?> cls) {
        startActivity(new Intent(this, cls).putExtra("userId", userId));
    }

    private void askLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Déconnexion")
                .setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")
                .setPositiveButton("Oui", (d, w) -> {
                    SessionManager.clear(this);
                    startActivity(new Intent(this, LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                })
                .setNegativeButton("Non", null)
                .show();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        io.shutdown();
    }
}
