package ma.itirc.vaxmind.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import ma.itirc.vaxmind.R;
import ma.itirc.vaxmind.data.database.AppDatabase;
import ma.itirc.vaxmind.data.entity.Vaccine;

public class VaccineAdapter extends RecyclerView.Adapter<VaccineAdapter.Holder> {

    private final List<Vaccine> data;
    private final SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public VaccineAdapter(List<Vaccine> data) { this.data = data; }

    /* ----------  Holder  ---------- */
    static class Holder extends RecyclerView.ViewHolder {
        TextView colName, colDate, colState;
        CheckBox ckDone;
        Holder(View v) {
            super(v);
            colName  = v.findViewById(R.id.colName);
            colDate  = v.findViewById(R.id.colDate);
            colState = v.findViewById(R.id.colState);
            ckDone   = v.findViewById(R.id.ckDone);
        }
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_vaccine, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        Vaccine v = data.get(pos);

        h.colName.setText(v.name);
        h.colDate.setText(v.doneDate == null ? v.plannedDate : v.doneDate);

        boolean done = v.doneDate != null;
        styleState(h, done);

        h.ckDone.setOnCheckedChangeListener(null);
        h.ckDone.setChecked(done);

        h.ckDone.setOnCheckedChangeListener((cb, isChecked) -> {
            v.doneDate = isChecked ? sdf.format(new Date()) : null;
            h.colDate.setText(isChecked ? v.doneDate : v.plannedDate);
            styleState(h, isChecked);

            Executors.newSingleThreadExecutor().execute(() ->
                    AppDatabase.get(cb.getContext())
                            .vaccineDao()
                            .update(v));
        });
    }

    private void styleState(Holder h, boolean done) {
        if (done) {
            h.colState.setText(R.string.vaccine_state_done);
            h.colState.setTextColor(
                    h.itemView.getContext().getColor(android.R.color.holo_green_dark));
        } else {
            h.colState.setText(R.string.vaccine_state_todo);
            h.colState.setTextColor(
                    h.itemView.getContext().getColor(android.R.color.holo_red_dark));
        }
    }

    @Override public int getItemCount() { return data.size(); }
}
