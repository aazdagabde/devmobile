package ma.itirc.vaxmind.ui.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mindrot.jbcrypt.BCrypt;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ma.itirc.vaxmind.R;
import ma.itirc.vaxmind.data.database.AppDatabase;
import ma.itirc.vaxmind.data.entity.User;

public class SettingsActivity extends AppCompatActivity {

    private EditText etName, etCity, etOldPwd, etNewPwd;
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userId  = getIntent().getLongExtra("userId", -1);
        etName  = findViewById(R.id.etName);
        etCity  = findViewById(R.id.etCity);
        etOldPwd= findViewById(R.id.etOldPwd);
        etNewPwd= findViewById(R.id.etNewPwd);

        // pré-remplir les champs
        io.execute(() -> {
            User u = AppDatabase.get(this).userDao().findById(userId);
            if (u != null) {
                runOnUiThread(() -> {
                    etName.setText(u.firstName + " " + u.lastName);
                    etCity.setText(u.city);
                });
            }
        });

        findViewById(R.id.btnSave).setOnClickListener(v -> save());
    }

    private void save() {
        String fullName = etName.getText().toString().trim();
        String city     = etCity.getText().toString().trim();
        String oldPwd   = etOldPwd.getText().toString();
        String newPwd   = etNewPwd.getText().toString();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(city)) {
            Toast.makeText(this, "Nom et Ville obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] split = fullName.split(" ", 2);
        String first = split[0];
        String last  = split.length > 1 ? split[1] : "";

        io.execute(() -> {
            var dao = AppDatabase.get(this).userDao();
            User u  = dao.findById(userId);
            if (u == null) return;

            // Mise à jour des informations
            u.firstName = first;
            u.lastName  = last;
            u.city      = city;

            // Changement de mot de passe si les champs sont remplis
            if (!TextUtils.isEmpty(oldPwd) && !TextUtils.isEmpty(newPwd)) {
                if (BCrypt.checkpw(oldPwd, u.passwordHash)) {
                    u.passwordHash = BCrypt.hashpw(newPwd, BCrypt.gensalt());
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Ancien mot de passe incorrect", Toast.LENGTH_SHORT).show());
                    return;
                }
            }

            dao.update(u);
            runOnUiThread(() ->
                    Toast.makeText(this, "Modifications enregistrées", Toast.LENGTH_SHORT).show());
        });
    }
}
