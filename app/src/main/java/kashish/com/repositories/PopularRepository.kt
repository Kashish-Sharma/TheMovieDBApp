package kashish.com.repositories

import android.arch.paging.LivePagedListBuilder
import kashish.com.boundaryCallbacks.PopularBoundaryCallbacks
import kashish.com.database.DatabaseResults.PopularResults
import kashish.com.database.LocalCache.PopularLocalCache
import kashish.com.network.NetworkService

/**
 * Created by Kashish on 14-08-2018.
 */
class PopularRepository(
        private val service: NetworkService,
        private val popularCache: PopularLocalCache
) {

    fun popular(region: String): PopularResults {

        val dataSourceFactory = popularCache.getAllPopular()

        val boundaryCallback = PopularBoundaryCallbacks(region, service, popularCache)
        val networkErrors = boundaryCallback.networkErrors

        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build()
        return PopularResults(data, networkErrors)
    }



    companion object {
        private const val NETWORK_PAGE_SIZE = 50
        private const val DATABASE_PAGE_SIZE = 60
    }

}