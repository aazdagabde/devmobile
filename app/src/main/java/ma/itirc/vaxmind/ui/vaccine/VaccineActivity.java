package ma.itirc.vaxmind.ui.vaccine;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ma.itirc.vaxmind.R;
import ma.itirc.vaxmind.data.database.AppDatabase;
import ma.itirc.vaxmind.data.entity.Vaccine;
import ma.itirc.vaxmind.data.seed.VaccineSchedule;
import ma.itirc.vaxmind.ui.adapter.VaccineAdapter;

public class VaccineActivity extends AppCompatActivity {

    private final ExecutorService io = Executors.newSingleThreadExecutor();

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_vaccine);

        long userId = getIntent().getLongExtra("userId", -1);
        RecyclerView rv = findViewById(R.id.rvVaccines);
        rv.setLayoutManager(new LinearLayoutManager(this));

        io.execute(() -> {
            var dao   = AppDatabase.get(this).vaccineDao();
            List<Vaccine> list = dao.listOfUser(userId);

            // ➜ si vide, pré-remplir
            if (list.isEmpty()) {
                dao.insertAll(VaccineSchedule.generate(userId));
                list = dao.listOfUser(userId);      // relire après insertion
            }

            List<Vaccine> finalList = list;
            runOnUiThread(() -> rv.setAdapter(new VaccineAdapter(finalList)));
        });
    }
}
