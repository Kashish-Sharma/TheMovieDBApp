package kashish.com.boundaryCallbacks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import android.util.Log
import kashish.com.database.Entities.TopRatedEntry
import kashish.com.database.LocalCache.TopRatedLocalCache
import kashish.com.network.NetworkService
import kashish.com.network.getTopRatedMovies
import kashish.com.utils.Constants
import java.util.*

/**
 * Created by Kashish on 15-08-2018.
 */
class TopRatedBoundaryCallbacks(
        private val region: String,
        private val service: NetworkService,
        private val cache: TopRatedLocalCache) : PagedList.BoundaryCallback<TopRatedEntry>() {

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }

    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = (cache.getAllItemsInTopRated()/20) + 1

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
        Log.d("TopRatedCallback", "onZeroItemsLoaded")
        requestAndSaveTopRatedData(region)
    }

    override fun onItemAtEndLoaded(itemAtEnd: TopRatedEntry) {
        Log.d("TopRatedCallback", "onItemAtEndLoaded")
        requestAndSaveTopRatedData(region)
    }

    private fun requestAndSaveTopRatedData(region: String) {
        if (isRequestInProgress) return


        isRequestInProgress = true

        getTopRatedMovies(service,"en-US",
                lastRequestedPage,
                region,
                { movierequest ->
                    val topRatedEntryList: MutableList<TopRatedEntry> = mutableListOf()
                    for (i in 0 until movierequest.results!!.size){
                        val topRatedEntry = TopRatedEntry()
                        val movie =  movierequest.results!![i]
                        topRatedEntry.movieId = movie.id
                        topRatedEntry.voteCount = movie.voteCount
                        topRatedEntry.video = movie.video
                        topRatedEntry.voteAverage = movie.voteAverage
                        topRatedEntry.title = movie.title
                        topRatedEntry.popularity = movie.popularity
                        topRatedEntry.posterPath = movie.posterPath
                        topRatedEntry.originalLanguage = movie.originalLanguage
                        topRatedEntry.originalTitle = movie.originalTitle
                        topRatedEntry.genreIds = movie.genreString
                        topRatedEntry.backdropPath = movie.backdropPath
                        topRatedEntry.adult = movie.adult
                        topRatedEntry.overview = movie.overview
                        topRatedEntry.releaseDate = movie.releaseDate
                        for (j in 0 until movie.genreIds!!.size) {
                            if(j==movie.genreIds!!.size-1)
                                movie.genreString += Constants.getGenre(movie.genreIds!!.get(j))
                            else
                                movie.genreString += Constants.getGenre(movie.genreIds!!.get(j))+", "
                        }
                        topRatedEntry.genreString = movie.genreString
                        topRatedEntry.contentType = Constants.CONTENT_SIMILAR
                        topRatedEntry.timeAdded = Date().time

                        if (topRatedEntry.backdropPath.isNullOrEmpty()) topRatedEntry.backdropPath = Constants.RANDOM_PATH
                        if (topRatedEntry.posterPath.isNullOrEmpty()) topRatedEntry.posterPath = Constants.RANDOM_PATH
                        topRatedEntryList.add(topRatedEntry)
                    }
                    cache.insert(topRatedEntryList,{
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