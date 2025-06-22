package ma.itirc.vaxmind.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

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

/**
 * Page d'accueil principale :
 *  • Affiche "Bonjour [Nom Prénom]"
 *  • 4 grands boutons : Informations, Vaccination, Paramètres, Assistant Santé
 */
public class MainActivity extends AppCompatActivity {

    /** Id de l'utilisateur connecté (reçu depuis LoginActivity) */
    private long userId = -1;

    /** Exécuteur I/O pour accéder à la base Room hors UI-thread */
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    /** Vue message de bienvenue */
    private TextView txtHi;

    @Override
    protected void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_main);

        /* ----------  1. Récupération du userId transmis par Intent  ---------- */
        userId = getIntent().getLongExtra("userId", -1);
        if (userId == -1) {          // Si absent → retour écran Login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        /* ----------  2. Références UI ---------- */
        txtHi = findViewById(R.id.txtHi);

        MaterialButton btnUser    = findViewById(R.id.btnUserInfo);
        MaterialButton btnVaccin  = findViewById(R.id.btnVaccines);
        MaterialButton btnParams  = findViewById(R.id.btnSettings);
        MaterialButton btnChatbot = findViewById(R.id.btnChat);

        /* ----------  3. Affichage "Bonjour [Nom Prénom]" asynchrone ---------- */
        io.execute(() -> {
            // ⚠️ Assurez-vous que UserDao possède la méthode findById(long)
            User u = AppDatabase.get(this).userDao().findById(userId);
            runOnUiThread(() -> {
                if (u != null) {
                    txtHi.setText(getString(R.string.main_hi, u.firstName, u.lastName));
                } else {
                    Toast.makeText(this, "Utilisateur introuvable", Toast.LENGTH_SHORT).show();
                }
            });
        });

        /* ----------  4. Navigation boutons ---------- */
        btnUser.setOnClickListener(v ->
                startActivity(new Intent(this, UserInfoActivity.class)
                        .putExtra("userId", userId)));

        btnVaccin.setOnClickListener(v ->
                startActivity(new Intent(this, VaccineActivity.class)
                        .putExtra("userId", userId)));

        btnParams.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)
                        .putExtra("userId", userId)));

        btnChatbot.setOnClickListener(v ->
                startActivity(new Intent(this, ChatbotActivity.class)
                        .putExtra("userId", userId)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        io.shutdown();   // Bonne pratique : libérer le thread pool
    }
}
