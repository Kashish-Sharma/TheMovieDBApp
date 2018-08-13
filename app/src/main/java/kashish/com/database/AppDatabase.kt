package kashish.com.database

import android.arch.persistence.db.SupportSQLiteOpenHelper
import android.arch.persistence.room.*
import android.content.Context
import android.util.Log
import kashish.com.database.Entities.*
import kashish.com.database.Converters.DateConverter
import kashish.com.database.Dao.*

/**
 * Created by Kashish on 11-08-2018.
 */

@Database(entities = arrayOf(FavouritesEntry::class, SearchEntry::class,
        NowShowingEntry::class, PopularEntry::class,
        TopRatedEntry::class, UpcomingEntry::class), version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase: RoomDatabase() {

    companion object {
        private val LOG_TAG: String = AppDatabase::class.simpleName.toString()
        private val LOCK: Any = Object()
        private val DATABSE_NAME: String = "movies"
        @Volatile
        private var sInstance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase{
            if (sInstance == null){
                synchronized(LOCK){
                    Log.d(LOG_TAG,"Creating new database instance")
                    sInstance = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java,AppDatabase.DATABSE_NAME)
                            .build()
                }
            }
            Log.d(LOG_TAG,"Getting the database instance")
            return sInstance!!
        }

    }


    abstract fun favouritesDao(): FavouritesDao
    abstract fun nowShowingDao(): NowShowingDao
    abstract fun poplarDao(): PopularDao
    abstract fun searchDao(): SearchDao
    abstract fun topRatedDao(): TopRatedDao
    abstract fun upcomingDao(): UpcomingDao



    override fun createOpenHelper(config: DatabaseConfiguration?): SupportSQLiteOpenHelper {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearAllTables() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}