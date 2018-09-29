package kashish.com.repositories

import android.arch.paging.LivePagedListBuilder
import kashish.com.boundaryCallbacks.UpcomingBoundaryCallback
import kashish.com.database.DatabaseResults.UpcomingResults
import kashish.com.database.LocalCache.UpcomingLocalCache
import kashish.com.network.NetworkService

/**
 * Created by Kashish on 14-08-2018.
 */
class UpcomingRepository(
        private val service: NetworkService,
        private val upcomingCache: UpcomingLocalCache
) {


    /**
     * Search repositories whose names match the query.
     */
    fun upcoming(region: String): UpcomingResults {
        val dataSourceFactory = upcomingCache.getAllUpcoming()

        val boundaryCallback = UpcomingBoundaryCallback(region, service, upcomingCache)
        val networkErrors = boundaryCallback.networkErrors
        // Get the paged list
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build()
        // Get data from the local cache
        return UpcomingResults(data, networkErrors)
    }



    companion object {
        private const val NETWORK_PAGE_SIZE = 50
        private const val DATABASE_PAGE_SIZE = 60
    }

}