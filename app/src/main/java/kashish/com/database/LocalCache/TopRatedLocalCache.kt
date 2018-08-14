package kashish.com.database.LocalCache

import android.arch.lifecycle.LiveData
import android.util.Log
import kashish.com.database.Dao.TopRatedDao
import kashish.com.database.Entities.TopRatedEntry
import java.util.concurrent.Executor

/**
 * Created by Kashish on 14-08-2018.
 */
class TopRatedLocalCache(
        private val topRatedDao: TopRatedDao,
        private val ioExecutor: Executor
) {

    /**
     * Insert a list of searches in the database, on a background thread.
     */
    fun insert(repos: List<TopRatedEntry>, insertFinished: ()-> Unit) {
        ioExecutor.execute {
            Log.d("GithubLocalCache", "inserting ${repos.size} repos")
            topRatedDao.insert(repos)
            insertFinished()
        }
    }

    fun getAllTopRated(): LiveData<List<TopRatedEntry>> {
        return topRatedDao.loadAllToprated()
    }
}