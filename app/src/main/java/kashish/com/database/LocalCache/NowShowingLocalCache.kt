package kashish.com.database.LocalCache

import android.arch.lifecycle.LiveData
import android.util.Log
import kashish.com.database.Dao.NowShowingDao
import kashish.com.database.Entities.NowShowingEntry
import java.util.concurrent.Executor

/**
 * Created by Kashish on 14-08-2018.
 */
class NowShowingLocalCache(
        private val nowShowingDao: NowShowingDao,
        private val ioExecutor: Executor
) {

    /**
     * Insert a list of searches in the database, on a background thread.
     */
    fun insert(repos: List<NowShowingEntry>, insertFinished: ()-> Unit) {
        ioExecutor.execute {
            Log.d("GithubLocalCache", "inserting ${repos.size} repos")
            nowShowingDao.insert(repos)
            insertFinished()
        }
    }

    fun getAllNowShowing(): LiveData<List<NowShowingEntry>> {
        return nowShowingDao.loadAllNowShowing()
    }
}