package kashish.com.data

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.LivePagedListBuilder
import kashish.com.boundaryCallbacks.TopRatedBoundaryCallbacks
import kashish.com.database.DatabaseResults.TopRatedResults
import kashish.com.database.Entities.TopRatedEntry
import kashish.com.database.LocalCache.TopRatedLocalCache
import kashish.com.network.NetworkService
import kashish.com.network.getTopRatedMovies
import kashish.com.utils.Constants
import java.util.*

/**
 * Created by Kashish on 14-08-2018.
 */
class TopRatedRepository(
        private val service: NetworkService,
        private val topRatedCache: TopRatedLocalCache
) {

    fun topRated(doReload: Boolean): TopRatedResults {

        val dataSourceFactory = topRatedCache.getAllTopRated()

        val boundaryCallback = TopRatedBoundaryCallbacks(doReload, service, topRatedCache)
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