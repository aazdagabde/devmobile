package ma.itirc.vaxmind.network;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Interface Retrofit → Google Generative Language API (Gemini).
 * Route :  v1beta/models/gemini-2.0-flash:generateContent
 */
public interface GeminiService {

    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    Call<GeminiResponse> generateContent(
            @Query("key") String apiKey,
            @Body GeminiRequest body);

    /* ----------  DTO ---------- */

    /** Corps minimal « prompt texte » */
    class GeminiRequest {
        public List<Map<String, Object>> contents;

        public GeminiRequest(String prompt) {
            //  {"contents":[{"parts":[{"text":"..."}]}]}
            contents = List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
            );
        }
    }

    /** Parsing très simplifié de la réponse Gemini */
    class GeminiResponse {
        public List<Candidate> candidates;

        public String firstAnswer() {
            if (candidates == null || candidates.isEmpty()) return "";
            return candidates.get(0).content.parts.get(0).text;
        }

        /* -- sous-structures            */
        public static class Candidate { public Content content; }
        public static class Content   { public List<Part> parts; }
        public static class Part      { public String text; }
    }
}
