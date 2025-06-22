package ma.itirc.vaxmind.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "id",
        childColumns = "userId",
        onDelete = CASCADE))
public class Vaccine {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;
    @NonNull public String name;
    @NonNull public String plannedDate;
    public String doneDate;
    public String notes;
}
