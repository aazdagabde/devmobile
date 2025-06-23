package ma.itirc.vaxmind.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ma.itirc.vaxmind.R;
import ma.itirc.vaxmind.data.database.AppDatabase;
import ma.itirc.vaxmind.data.entity.User;
import ma.itirc.vaxmind.ui.main.MainActivity;
import ma.itirc.vaxmind.util.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPwd;
    private ProgressBar progress;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);

        /* ----------  Déjà connecté ? sauter le formulaire ---------- */
        long sessionId = SessionManager.current(this);
        if (sessionId != -1) {
            launchMain(sessionId);
            return;
        }

        setContentView(R.layout.activity_login);
        etEmail  = findViewById(R.id.etEmail);
        etPwd    = findViewById(R.id.etPassword);
        progress = findViewById(R.id.progressBar);

        findViewById(R.id.btnLogin).setOnClickListener(v -> attemptLogin());
        findViewById(R.id.btnToRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String pwd   = etPwd.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email invalide"); return;
        }
        if (pwd.isEmpty()) { etPwd.setError("Mot de passe vide"); return; }

        progress.setVisibility(View.VISIBLE);

        io.execute(() -> {
            User u = AppDatabase.get(this).userDao().findByEmail(email);
            runOnUiThread(() -> progress.setVisibility(View.GONE));
            if (u == null || !org.mindrot.jbcrypt.BCrypt.checkpw(pwd, u.passwordHash)) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show());
            } else {
                SessionManager.save(this, u.id);          // <— enregistre la session
                runOnUiThread(() -> launchMain(u.id));
            }
        });
    }

    private void launchMain(long id) {
        startActivity(new Intent(this, MainActivity.class)
                .putExtra("userId", id));
        finish();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        io.shutdown();
    }
}
