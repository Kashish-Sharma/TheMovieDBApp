package kashish.com.boundaryCallbacks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import android.util.Log
import kashish.com.database.Entities.UpcomingEntry
import kashish.com.database.LocalCache.UpcomingLocalCache
import kashish.com.network.NetworkService
import kashish.com.network.getUpcomingMovies
import kashish.com.utils.Constants
import java.util.*

/**
 * Created by Kashish on 15-08-2018.
 */
class UpcomingBoundaryCallback(
        private val region: String,
        private val service: NetworkService,
        private val cache: UpcomingLocalCache) : PagedList.BoundaryCallback<UpcomingEntry>() {

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }

    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = (cache.getAllItemsInUpcoming()/20) + 1

    private val _networkErrors = MutableLiveData<String>()
    // LiveData of network errors.
    val networkErrors: LiveData<String>
        get() = _networkErrors

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    /**
     * Database returned 0 items. We should query the backend for more items.
     */

    override fun onZeroItemsLoaded() {
        Log.d("UpcomingCallback", "onZeroItemsLoaded")
        requestAndSaveUpcomingData(region)
    }

    override fun onItemAtEndLoaded(itemAtEnd: UpcomingEntry) {
        Log.d("UpcomingCallback", "onItemAtEndLoaded")
        requestAndSaveUpcomingData(region)
    }

    private fun requestAndSaveUpcomingData(region: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true

        getUpcomingMovies(service,"en-US",
                lastRequestedPage,
                region,
                { movierequest ->
                    val upcomingEntryList: MutableList<UpcomingEntry> = mutableListOf()
                    for (i in 0 until movierequest.results!!.size){
                        val upcomingEntry = UpcomingEntry()
                        val movie =  movierequest.results!![i]
                        upcomingEntry.movieId = movie.id
                        upcomingEntry.voteCount = movie.voteCount
                        upcomingEntry.video = movie.video
                        upcomingEntry.voteAverage = movie.voteAverage
                        upcomingEntry.title = movie.title
                        upcomingEntry.popularity = movie.popularity
                        upcomingEntry.posterPath = movie.posterPath
                        upcomingEntry.originalLanguage = movie.originalLanguage
                        upcomingEntry.originalTitle = movie.originalTitle
                        upcomingEntry.genreIds = movie.genreString
                        upcomingEntry.backdropPath = movie.backdropPath
                        upcomingEntry.adult = movie.adult
                        upcomingEntry.overview = movie.overview
                        upcomingEntry.releaseDate = movie.releaseDate
                        for (j in 0 until movie.genreIds!!.size) {
                            if(j==movie.genreIds!!.size-1)
                                movie.genreString += Constants.getGenre(movie.genreIds!!.get(j))
                            else
                                movie.genreString += Constants.getGenre(movie.genreIds!!.get(j))+", "
                        }
                        upcomingEntry.genreString = movie.genreString
                        upcomingEntry.contentType = Constants.CONTENT_SIMILAR
                        upcomingEntry.timeAdded = Date().time

                        if (upcomingEntry.backdropPath.isNullOrEmpty()) upcomingEntry.backdropPath = Constants.RANDOM_PATH
                        if (upcomingEntry.posterPath.isNullOrEmpty()) upcomingEntry.posterPath = Constants.RANDOM_PATH
                        upcomingEntryList.add(upcomingEntry)
                    }
                    cache.insert(upcomingEntryList,{
                        lastRequestedPage++
                        isRequestInProgress = false
                    })
                }, {
            error ->
            _networkErrors.postValue(error)
            isRequestInProgress = false
        })

    }

}