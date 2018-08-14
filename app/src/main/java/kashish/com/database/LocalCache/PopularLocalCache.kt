package kashish.com.database.LocalCache

import android.arch.lifecycle.LiveData
import android.util.Log
import kashish.com.database.Dao.NowShowingDao
import kashish.com.database.Dao.PopularDao
import kashish.com.database.Entities.PopularEntry
import java.util.concurrent.Executor

/**
 * Created by Kashish on 14-08-2018.
 */
class PopularLocalCache(
        private val popularDao: PopularDao,
        private val ioExecutor: Executor
) {

    /**
     * Insert a list of searches in the database, on a background thread.
     */
    fun insert(repos: List<PopularEntry>, insertFinished: ()-> Unit) {
        ioExecutor.execute {
            Log.d("GithubLocalCache", "inserting ${repos.size} repos")
            popularDao.insert(repos)
            insertFinished()
        }
    }

    fun getAllPopular(): LiveData<List<PopularEntry>> {
        return popularDao.loadAllPopular()
    }
}