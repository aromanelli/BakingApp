package info.romanelli.udacity.bakingapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface AppDataDao {

    String NAME_TABLE_APP_DATA = "app_data";

    @Query("SELECT * FROM " + NAME_TABLE_APP_DATA + " ORDER BY id")
    LiveData<List<AppDataEntry>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(AppDataEntry entry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(AppDataEntry entry);

    @Delete
    void delete(AppDataEntry entry);

    @Query("SELECT * FROM "+ NAME_TABLE_APP_DATA +" WHERE id = 0")
    LiveData<AppDataEntry> get();

}
