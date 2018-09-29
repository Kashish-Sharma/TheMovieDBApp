package kashish.com.repositories

import android.arch.paging.LivePagedListBuilder
import kashish.com.boundaryCallbacks.TopRatedBoundaryCallbacks
import kashish.com.database.DatabaseResults.TopRatedResults
import kashish.com.database.LocalCache.TopRatedLocalCache
import kashish.com.network.NetworkService

/**
 * Created by Kashish on 14-08-2018.
 */
class TopRatedRepository(
        private val service: NetworkService,
        private val topRatedCache: TopRatedLocalCache
) {

    fun topRated(region: String): TopRatedResults {

        val dataSourceFactory = topRatedCache.getAllTopRated()

        val boundaryCallback = TopRatedBoundaryCallbacks(region, service, topRatedCache)
        val networkErrors = boundaryCallback.networkErrors
        // Get the paged list
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build()
        return TopRatedResults(data, networkErrors)
    }


    companion object {
        private const val NETWORK_PAGE_SIZE = 50
        private const val DATABASE_PAGE_SIZE = 60
    }


}