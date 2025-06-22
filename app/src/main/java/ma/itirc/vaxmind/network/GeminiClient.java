package ma.itirc.vaxmind.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeminiClient {
    private static GeminiService service;

    public static GeminiService get() {
        if (service == null) {
            service = new Retrofit.Builder()
                    .baseUrl("https://generativelanguage.googleapis.com/") // OK
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(GeminiService.class);
        }
        return service;
    }
}
