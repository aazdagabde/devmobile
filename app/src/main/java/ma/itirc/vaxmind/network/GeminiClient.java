package ma.itirc.vaxmind.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Singleton Retrofit configuré (Retry + Logging) pour l’API Gemini. */
public final class GeminiClient {

    private GeminiClient() {}   // utilitaire

    /* -----------  OkHttp  ----------- */
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .addInterceptor(new RetryInterceptor())                       // retry 503/429
            .addInterceptor(new HttpLoggingInterceptor()                  // log debug
                    .setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build();

    /* -----------  Retrofit  ----------- */
    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")        // slash final !
            .client(CLIENT)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static GeminiService api() {
        return RETROFIT.create(GeminiService.class);
    }
}
