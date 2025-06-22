package ma.itirc.vaxmind.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ma.itirc.vaxmind.data.entity.User;
import ma.itirc.vaxmind.data.entity.Vaccine;

@Dao
public interface UserDao {
    @Insert
    long insert(User u);

    @Query("SELECT * FROM User WHERE email = :mail LIMIT 1")
    User findByEmail(String mail);


    @Query("SELECT * FROM User WHERE id = :uid LIMIT 1")
    User findById(long uid);

    @Update
    void update(User user);

    @Insert
    void insertAll(List<Vaccine> list);

}
