package ma.itirc.vaxmind.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull public String firstName;
    @NonNull public String lastName;
    @NonNull public String email;
    @NonNull public String passwordHash;
    @NonNull public String birthDate;
    @NonNull public String city;
    @NonNull public String parent1Name;
    @NonNull public String parent2Name;
    @NonNull public String parentPhones;
    @NonNull public String parentCins;
    public String userCin;
}
