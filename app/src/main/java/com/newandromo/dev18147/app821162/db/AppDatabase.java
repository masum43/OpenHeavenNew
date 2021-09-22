package com.newandromo.dev18147.app821162.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.newandromo.dev18147.app821162.AppExecutors;
import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.db.converter.Converters;
import com.newandromo.dev18147.app821162.db.dao.CategoryDao;
import com.newandromo.dev18147.app821162.db.dao.EntryDao;
import com.newandromo.dev18147.app821162.db.dao.FeedDao;
import com.newandromo.dev18147.app821162.db.dao.YoutubeSearchDao;
import com.newandromo.dev18147.app821162.db.dao.YoutubeTypeDao;
import com.newandromo.dev18147.app821162.db.dao.YoutubeVideoDao;
import com.newandromo.dev18147.app821162.db.entity.CategoryEntity;
import com.newandromo.dev18147.app821162.db.entity.EntryEntity;
import com.newandromo.dev18147.app821162.db.entity.EntryEntityFts;
import com.newandromo.dev18147.app821162.db.entity.FeedEntity;
import com.newandromo.dev18147.app821162.db.entity.YoutubeSearchEntity;
import com.newandromo.dev18147.app821162.db.entity.YoutubeTypeEntity;
import com.newandromo.dev18147.app821162.db.entity.YoutubeVideoEntity;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import timber.log.Timber;

@Database(entities =
        {FeedEntity.class, CategoryEntity.class, EntryEntity.class, EntryEntityFts.class,
                YoutubeTypeEntity.class, YoutubeVideoEntity.class, YoutubeSearchEntity.class},
        version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "myApp_new.db" + BuildConfig.VERSION_CODE;
    private static AppDatabase mInstance;

    public abstract CategoryDao categoryDao();

    public abstract FeedDao feedDao();

    public abstract EntryDao entryDao();

    public abstract YoutubeTypeDao youtubeTypeDao();

    public abstract YoutubeVideoDao youtubeVideoDao();

    public abstract YoutubeSearchDao youtubeSearchDao();

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(Context context, AppExecutors executors) {
        if (mInstance == null) {
            synchronized (AppDatabase.class) {
                if (mInstance == null) {
                    mInstance = buildDatabase(context.getApplicationContext(), executors);
                    mInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static AppDatabase buildDatabase(Context context, AppExecutors executors) {
        return Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {
                            // Generate the data for pre-population
                            try {
                                AppDatabase database = AppDatabase.getInstance(context, executors);

                                String jsonString = AppUtils.getAssetJsonData(context, "default_feeds_new.json");

                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONArray defaultsArray = jsonObject.getJSONArray("defaults");

                                List<String> languageCodes = new ArrayList<>();
                                for (int i = 0; i < defaultsArray.length(); i++) {
                                    languageCodes.add(defaultsArray.getJSONObject(i).getString("id"));
                                }

                                String languageCode = PrefUtils.getFeedsLanguageCode(context);
                                int index = languageCodes.contains(languageCode) ?
                                        languageCodes.indexOf(languageCode) : 0;

                                JSONObject defaultObj = defaultsArray.getJSONObject(index);

                                List<FeedEntity> feeds = DataGenerator.populateFeedsByCategory(database, defaultObj);

                                List<YoutubeTypeEntity> youtubeTypes = DataGenerator.populateYoutubeTypes(defaultObj);

                                insertData(database, feeds, youtubeTypes);
                                // notify that the database was created and it's ready to be used
                                database.setDatabaseCreated();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }).build();
    }

    private static void insertData(AppDatabase database,
                                   List<FeedEntity> feeds, List<YoutubeTypeEntity> youtubeTypes) {
        database.runInTransaction(() -> {
            database.feedDao().insertAll(feeds);

            long[] rowIds = database.youtubeTypeDao().insertTypes(youtubeTypes);

            if (BuildConfig.DEBUG)
                Timber.d("%s new Youtube types inserted! IDs= %s",
                        String.valueOf(rowIds.length), Arrays.toString(rowIds));
        });
    }

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    private LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }
}
