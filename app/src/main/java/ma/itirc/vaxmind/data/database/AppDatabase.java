package ma.itirc.vaxmind.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ma.itirc.vaxmind.data.dao.UserDao;
import ma.itirc.vaxmind.data.dao.VaccineDao;
import ma.itirc.vaxmind.data.entity.User;
import ma.itirc.vaxmind.data.entity.Vaccine;

@Database(entities = {User.class, Vaccine.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract VaccineDao vaccineDao();

    private static volatile AppDatabase INSTANCE;
    public static AppDatabase get(Context ctx) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(),
                            AppDatabase.class, "vaxmind.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
