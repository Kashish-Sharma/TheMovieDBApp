package kashish.com.database.LocalCache

import android.arch.lifecycle.LiveData
import android.util.Log
import kashish.com.database.Dao.UpcomingDao
import java.util.concurrent.Executor
import kashish.com.database.Entities.UpcomingEntry

/**
 * Created by Kashish on 14-08-2018.
 */
class UpcomingLocalCache(
        private val upcomingDao: UpcomingDao,
        private val ioExecutor: Executor
) {

    /**
     * Insert a list of searches in the database, on a background thread.
     */
    fun insert(repos: List<UpcomingEntry>, insertFinished: ()-> Unit) {
        ioExecutor.execute {
            Log.d("GithubLocalCache", "inserting ${repos.size} repos")
            upcomingDao.insert(repos)
            insertFinished()
        }
    }

    fun getAllUpcoming(): LiveData<List<UpcomingEntry>> {
        return upcomingDao.loadAllUpcoming()
    }
}