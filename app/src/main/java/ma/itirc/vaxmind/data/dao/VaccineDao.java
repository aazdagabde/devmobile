package ma.itirc.vaxmind.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ma.itirc.vaxmind.data.entity.Vaccine;

@Dao
public interface VaccineDao {
    @Insert
    long insert(Vaccine v);

    @Insert
    void insertAll(List<Vaccine> list);

    @Update
    void update(Vaccine v);

    @Query("SELECT * FROM Vaccine WHERE userId = :uid")
    List<Vaccine> listOfUser(long uid);
}
