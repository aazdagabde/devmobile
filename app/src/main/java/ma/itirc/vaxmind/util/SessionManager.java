package ma.itirc.vaxmind.util;

import android.content.Context;
import android.content.SharedPreferences;

/** Stocke l’ID utilisateur pour éviter de redemander le login. */
public final class SessionManager {
    private static final String PREF = "session";
    private static final String KEY_USER = "userId";

    private SessionManager(){}

    /* Enregistre la session */
    public static void save(Context ctx, long userId) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit().putLong(KEY_USER, userId).apply();
    }

    /* Récupère l’utilisateur connecté, ou -1 si aucun */
    public static long current(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getLong(KEY_USER, -1);
    }

    /* Vide la session (logout) */
    public static void clear(Context ctx) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit().clear().apply();
    }
}
