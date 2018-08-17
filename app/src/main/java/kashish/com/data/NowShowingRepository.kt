package kashish.com.data

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.LivePagedListBuilder
import android.util.Log
import kashish.com.boundaryCallbacks.NowShowingBoundaryCallbacks
import kashish.com.database.DatabaseResults.NowShowingResults
import kashish.com.database.Entities.NowShowingEntry
import kashish.com.database.LocalCache.NowShowingLocalCache
import kashish.com.network.NetworkService
import kashish.com.network.getNowShowingMovies
import kashish.com.utils.Constants
import java.util.*

/**
 * Created by Kashish on 14-08-2018.
 */
class NowShowingRepository(
        private val service: NetworkService,
        private val nowShowingCache: NowShowingLocalCache
) {

    fun nowShowing(doReload: Boolean): NowShowingResults {
        // Get data source factory from the local cache
        val dataSourceFactory = nowShowingCache.getAllNowShowing()

        val boundaryCallback = NowShowingBoundaryCallbacks(doReload, service, nowShowingCache)
        val networkErrors = boundaryCallback.networkErrors

        // Get the paged list
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build()
        return NowShowingResults(data, networkErrors)
    }


    companion object {
        private const val NETWORK_PAGE_SIZE = 50
        private const val DATABASE_PAGE_SIZE = 60
    }

}