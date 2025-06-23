package ma.itirc.vaxmind.network;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Intercepteur OkHttp qui retente jusqu’à 3 fois sur 503 (Service Unavailable)
 * ou 429 (Quota exceeded). Back-off exponentiel : 0,5 s → 1 s → 2 s.
 */
public class RetryInterceptor implements Interceptor {

    private static final int MAX_RETRY = 3;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request  req = chain.request();
        Response res = null;
        int tryCount = 0;

        while (true) {
            res = chain.proceed(req);

            boolean shouldRetry =
                    (res.code() == 503 || res.code() == 429) && tryCount < MAX_RETRY;

            if (!shouldRetry) return res;

            tryCount++;
            long backoff = (long) Math.pow(2, tryCount - 1) * 500;   // 0.5, 1, 2 s
            try { Thread.sleep(backoff); } catch (InterruptedException ignored) {}
        }
    }
}
