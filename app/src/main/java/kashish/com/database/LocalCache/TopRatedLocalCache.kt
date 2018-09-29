package kashish.com.database.LocalCache

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import android.util.Log
import kashish.com.database.Dao.TopRatedDao
import kashish.com.database.Entities.TopRatedEntry
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
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
            topRatedDao.insert(repos)
            insertFinished()
        }
    }

    fun getAllTopRated(): DataSource.Factory<Int, TopRatedEntry> {
        return topRatedDao.loadAllToprated()
    }

    fun getAllItemsInTopRated(): Int {
        val data  = runBlocking {
            async(CommonPool) {
                val numItems = topRatedDao.getNumberOfRows()
                return@async numItems
            }.await()
        }
        return data

    }

}