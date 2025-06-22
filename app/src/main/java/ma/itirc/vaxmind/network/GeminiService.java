package ma.itirc.vaxmind.network;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Interface Retrofit ↔ API Google Generative Language (Gemini).
 * Appelle le modèle gemini-2.0-flash en v1beta.
 */
public interface GeminiService {

    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    Call<GeminiResponse> generateContent(
            @Query("key") String apiKey,      // ?key=YOUR_API_KEY
            @Body  GeminiRequest body);       // JSON { "contents":[…] }

    /* ----------  Modèles de requête / réponse ---------- */

    /** Corps minimal accepté par l’API (prompt texte simple) */
    class GeminiRequest {
        public List<Map<String, Object>> contents;

        public GeminiRequest(String prompt) {
            // Construit la structure JSON attendue par l’API
            contents = List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
            );
        }
    }

    /** Parsing très simplifié de la réponse */
    class GeminiResponse {
        public List<Candidate> candidates;

        public static class Candidate {
            public Content content;
        }
        public static class Content {
            public List<Part> parts;
        }
        public static class Part {
            public String text;
        }

        /** Retourne la première réponse textuelle ou chaîne vide */
        public String firstAnswer() {
            return (candidates != null && !candidates.isEmpty())
                    ? candidates.get(0).content.parts.get(0).text
                    : "";
        }
    }
}
