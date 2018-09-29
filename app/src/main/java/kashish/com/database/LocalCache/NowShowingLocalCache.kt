package kashish.com.database.LocalCache

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.util.Log
import kashish.com.database.Dao.NowShowingDao
import kashish.com.database.Entities.NowShowingEntry
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
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
            nowShowingDao.insert(repos)
            insertFinished()
        }
    }

    fun getAllNowShowing(): DataSource.Factory<Int, NowShowingEntry> {
        return nowShowingDao.loadAllNowShowing()
    }

    fun getAllItemsInNowShowing(): Int {
        val data  = runBlocking {
            async(CommonPool) {
                val numItems = nowShowingDao.getNumberOfRows()
                return@async numItems
            }.await()
        }
        return data

    }

}