package ma.itirc.vaxmind.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.mindrot.jbcrypt.BCrypt;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ma.itirc.vaxmind.R;
import ma.itirc.vaxmind.data.database.AppDatabase;
import ma.itirc.vaxmind.data.entity.User;
import ma.itirc.vaxmind.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt, passEt;
    private ProgressBar progress;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt  = findViewById(R.id.etEmail);
        passEt   = findViewById(R.id.etPassword);
        progress = findViewById(R.id.progressBar);

        findViewById(R.id.btnLogin).setOnClickListener(v -> attemptLogin());
        findViewById(R.id.btnToRegister).setOnClickListener(
                v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String email = emailEt.getText().toString().trim();
        String pass  = passEt.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email et mot de passe requis", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Email invalide"); emailEt.requestFocus(); return;
        }

        progress.setVisibility(View.VISIBLE);

        io.execute(() -> {
            AppDatabase db = AppDatabase.get(this);
            User u = db.userDao().findByEmail(email);

            runOnUiThread(() -> {
                progress.setVisibility(View.GONE);

                if (u == null) {
                    emailEt.setError("Compte introuvable"); emailEt.requestFocus();
                    return;
                }
                if (!BCrypt.checkpw(pass, u.passwordHash)) {
                    passEt.setError("Mot de passe incorrect"); passEt.requestFocus();
                    return;
                }

                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("userId", u.id);
                startActivity(i);
                finish();
            });
        });
    }
}
