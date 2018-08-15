package kashish.com.data

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.LivePagedListBuilder
import kashish.com.boundaryCallbacks.UpcomingBoundaryCallback
import kashish.com.database.DatabaseResults.UpcomingResults
import kashish.com.database.Entities.UpcomingEntry
import kashish.com.database.LocalCache.UpcomingLocalCache
import kashish.com.network.NetworkService
import kashish.com.network.getUpcomingMovies
import kashish.com.utils.Constants
import java.util.*

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
    fun upcoming(doReload: Boolean): UpcomingResults {
        val dataSourceFactory = upcomingCache.getAllUpcoming()

        val boundaryCallback = UpcomingBoundaryCallback(doReload, service, upcomingCache)
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
        private const val DATABASE_PAGE_SIZE = 20
    }

}