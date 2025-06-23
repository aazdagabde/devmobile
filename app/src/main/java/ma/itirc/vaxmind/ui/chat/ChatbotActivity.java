package ma.itirc.vaxmind.ui.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ma.itirc.vaxmind.BuildConfig;
import ma.itirc.vaxmind.R;
import ma.itirc.vaxmind.network.GeminiClient;
import ma.itirc.vaxmind.network.GeminiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Chatbot « VaxMind » – spécialité Santé / Vaccins
 * (retry réseau déjà géré par RetryInterceptor).
 */
public class ChatbotActivity extends AppCompatActivity {

    private static final String SYSTEM_PROMPT =
            "Tu es VaxMind, un assistant spécialisé dans la santé et les vaccins. " +
                    "Réponds aux questions santé/vaccins en français. " +
                    "Si la question est hors de ce domaine, réponds seulement : " +
                    "\"Je m'excuse, ma spécialité est la santé et les vaccins.\"";

    private EditText   edPrompt;
    private ProgressBar progress;
    private ChatAdapter adapter;
    private final List<ChatMessage> messages = new ArrayList<>();

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_chatbot);

        edPrompt  = findViewById(R.id.edPrompt);
        progress  = findViewById(R.id.progressBar);

        RecyclerView rv = findViewById(R.id.rvChat);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter();
        rv.setAdapter(adapter);

        findViewById(R.id.btnSend).setOnClickListener(v -> sendPrompt());
    }

    private void sendPrompt() {
        String prompt = edPrompt.getText().toString().trim();
        if (prompt.isEmpty()) return;

        edPrompt.setText("");
        append(new ChatMessage(prompt, true));

        progress.setVisibility(View.VISIBLE);

        String fullPrompt = SYSTEM_PROMPT + "\n\nQuestion : " + prompt;
        GeminiService.GeminiRequest body = new GeminiService.GeminiRequest(fullPrompt);

        GeminiClient.api()
                .generateContent(BuildConfig.GEMINI_API_KEY, body)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<GeminiService.GeminiResponse> c,
                            @NonNull Response<GeminiService.GeminiResponse> r) {

                        progress.setVisibility(View.GONE);

                        if (r.isSuccessful() && r.body() != null) {
                            append(new ChatMessage(r.body().firstAnswer(), false));
                        } else {
                            String details = "";
                            try { if (r.errorBody()!=null) details = r.errorBody().string(); }
                            catch (IOException ignored) {}
                            append(new ChatMessage("Erreur API : " + r.code() + "\n" + details, false));
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<GeminiService.GeminiResponse> c,
                            @NonNull Throwable t) {

                        progress.setVisibility(View.GONE);
                        append(new ChatMessage("Erreur réseau : " + t.getMessage(), false));
                    }
                });
    }

    /* ---------- utils ---------- */
    private void append(ChatMessage m) {
        messages.add(m);
        adapter.submitList(new ArrayList<>(messages));
    }
}
