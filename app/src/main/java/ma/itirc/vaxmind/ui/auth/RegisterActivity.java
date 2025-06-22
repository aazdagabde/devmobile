package ma.itirc.vaxmind.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.regex.Pattern;

import ma.itirc.vaxmind.R;
import ma.itirc.vaxmind.data.database.AppDatabase;
import ma.itirc.vaxmind.data.entity.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameEt, lastNameEt, emailEt, passEt,
            birthEt, cityEt, parent1Et, parent2Et,
            parentPhoneEt, parentCinEt, userCinEt;
    private ProgressBar progress;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    // 1 à 2 lettres suivies de 4 à 6 chiffres  (ex. "AB123456")
    private static final Pattern CIN_PATTERN   = Pattern.compile("^[A-Za-z]{1,2}\\d{4,6}$");

    // exactement 10 chiffres (format téléphone marocain 0xxxxxxxxx)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstNameEt = findViewById(R.id.etFirstName);
        lastNameEt  = findViewById(R.id.etLastName);
        emailEt     = findViewById(R.id.etEmail);
        passEt      = findViewById(R.id.etPassword);
        birthEt     = findViewById(R.id.etBirthDate);
        cityEt      = findViewById(R.id.etCity);
        parent1Et   = findViewById(R.id.etParent1Name);
        parent2Et   = findViewById(R.id.etParent2Name);
        parentPhoneEt = findViewById(R.id.etParentPhone);
        parentCinEt   = findViewById(R.id.etParentCin);
        userCinEt     = findViewById(R.id.etUserCin);
        progress      = findViewById(R.id.progressBar);

        findViewById(R.id.btnRegister).setOnClickListener(v -> attemptRegister());
        findViewById(R.id.btnToLogin).setOnClickListener(
                v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    private void attemptRegister() {
        String first   = firstNameEt.getText().toString().trim();
        String last    = lastNameEt.getText().toString().trim();
        String email   = emailEt.getText().toString().trim();
        String pass    = passEt.getText().toString().trim();
        String birth   = birthEt.getText().toString().trim();
        String city    = cityEt.getText().toString().trim();
        String p1      = parent1Et.getText().toString().trim();
        String p2      = parent2Et.getText().toString().trim();
        String pPhone  = parentPhoneEt.getText().toString().trim();
        String pCin    = parentCinEt.getText().toString().trim();
        String userCin = userCinEt.getText().toString().trim();

        if (first.isEmpty()|| last.isEmpty()|| email.isEmpty()|| pass.isEmpty()
                || birth.isEmpty()|| city.isEmpty()
                || p1.isEmpty()|| p2.isEmpty()|| pPhone.isEmpty()|| pCin.isEmpty()) {
            Toast.makeText(this, "Tous les champs obligatoires doivent être remplis", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Email invalide"); emailEt.requestFocus(); return;
        }
        if (!PHONE_PATTERN.matcher(pPhone).matches()) {
            parentPhoneEt.setError("Téléphone invalide"); parentPhoneEt.requestFocus(); return;
        }
        if (!CIN_PATTERN.matcher(pCin).matches()) {
            parentCinEt.setError("CIN parent invalide"); parentCinEt.requestFocus(); return;
        }
        if (!userCin.isEmpty() && !CIN_PATTERN.matcher(userCin).matches()) {
            userCinEt.setError("CIN utilisateur invalide"); userCinEt.requestFocus(); return;
        }

        progress.setVisibility(View.VISIBLE);

        io.execute(() -> {
            AppDatabase db = AppDatabase.get(this);
            if (db.userDao().findByEmail(email) != null) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    emailEt.setError("Email déjà existant");
                    emailEt.requestFocus();
                });
                return;
            }

            User u = new User();
            u.firstName = first;
            u.lastName  = last;
            u.email     = email;
            u.passwordHash = BCrypt.hashpw(pass, BCrypt.gensalt());
            u.birthDate = birth;
            u.city      = city;
            u.parent1Name = p1;
            u.parent2Name = p2;
            u.parentPhones = pPhone;
            u.parentCins   = pCin;
            u.userCin      = TextUtils.isEmpty(userCin) ? null : userCin;

            long id = db.userDao().insert(u);

            runOnUiThread(() -> {
                progress.setVisibility(View.GONE);
                if (id > 0) {
                    Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Erreur base de données", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
