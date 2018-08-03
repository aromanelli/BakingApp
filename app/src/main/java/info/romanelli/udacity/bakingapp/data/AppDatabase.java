package info.romanelli.udacity.bakingapp.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = {AppDataEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    final static private String TAG = AppDatabase.class.getSimpleName();

    final static private String DATABASE_NAME = "recipe_info";

    static private AppDatabase REF;

    /**
     * <p>Initialize this database lookup, associating the application context to the Room db.</p>
     * @param context Will call {@link Context#getApplicationContext()} on this reference.
     */
    static public void init(final Context context) {
        if (REF == null) {
            synchronized (TAG) {
                if (REF == null) {
                    Log.d(TAG, "$: Creating Room DB for Context [" + context + "]!");
                    REF = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            AppDatabase.DATABASE_NAME
                    ).build();
                } else {
                    Log.w(TAG, AppDatabase.class.getSimpleName() + " is already initialized." );
                }
            }
        }
    }

    static public AppDatabase $() {
        if (REF == null) {
            throw new IllegalStateException("Must initialize first!");
        }
        return REF;
    }

    abstract public AppDataDao getDao();
}
