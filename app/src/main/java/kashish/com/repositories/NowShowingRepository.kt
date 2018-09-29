package kashish.com.repositories

import android.arch.paging.LivePagedListBuilder
import kashish.com.boundaryCallbacks.NowShowingBoundaryCallbacks
import kashish.com.database.DatabaseResults.NowShowingResults
import kashish.com.database.LocalCache.NowShowingLocalCache
import kashish.com.network.NetworkService

/**
 * Created by Kashish on 14-08-2018.
 */
class NowShowingRepository(
        private val service: NetworkService,
        private val nowShowingCache: NowShowingLocalCache
) {

    fun nowShowing(region: String): NowShowingResults {
        // Get data source factory from the local cache
        val dataSourceFactory = nowShowingCache.getAllNowShowing()

        val boundaryCallback = NowShowingBoundaryCallbacks(region, service, nowShowingCache)
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