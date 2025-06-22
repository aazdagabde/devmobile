package ma.itirc.vaxmind.ui.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ma.itirc.vaxmind.R;
import ma.itirc.vaxmind.network.GeminiClient;
import ma.itirc.vaxmind.network.GeminiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotActivity extends AppCompatActivity {

    /* ------------------  AJOUT ICI : clé en clair  ------------------ */
    private static final String GEMINI_API_KEY = "AIzaSyBvCjYm9F7xpQsRXvtOGLF6jKGNTu2cttg";
    /* ---------------------------------------------------------------- */

    private EditText   input;
    private TextView   chat;
    private ScrollView scroll;
    private ProgressBar progress;

    @Override protected void onCreate(@Nullable Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_chatbot);

        input    = findViewById(R.id.edPrompt);
        chat     = findViewById(R.id.tvChat);
        scroll   = findViewById(R.id.scrollChat);
        progress = findViewById(R.id.progressBar);
        Button btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(v -> askGemini());
    }

    private void askGemini() {
        String prompt = input.getText().toString().trim();
        if (prompt.isEmpty()) return;

        progress.setVisibility(View.VISIBLE);
        GeminiService.GeminiRequest body = new GeminiService.GeminiRequest(prompt);

        GeminiClient.get()
                .generateContent(GEMINI_API_KEY, body)         // <-- utilisation directe
                .enqueue(new Callback<>() {
                    @Override public void onResponse(
                            Call<GeminiService.GeminiResponse> c,
                            Response<GeminiService.GeminiResponse> r) {

                        progress.setVisibility(View.GONE);
                        if (r.isSuccessful() && r.body() != null) {
                            append("Vous : " + prompt);
                            append("SantéBot : " + r.body().firstAnswer());
                            input.setText("");
                        } else {
                            Toast.makeText(ChatbotActivity.this,
                                    "Réponse API invalide", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(
                            Call<GeminiService.GeminiResponse> c, Throwable t) {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(ChatbotActivity.this,
                                t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void append(String line) {
        chat.append(line);
        chat.append("\n\n");                 // ou  chat.append(line + "\n\n");
        scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
    }
}
