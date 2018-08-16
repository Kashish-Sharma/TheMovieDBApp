package kashish.com.boundaryCallbacks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import android.util.Log
import kashish.com.database.Entities.SearchEntry
import kashish.com.database.LocalCache.SearchLocalCache
import kashish.com.network.NetworkService
import kashish.com.network.getSearchMovies
import kashish.com.utils.Constants
import java.util.*

/**
 * Created by Kashish on 15-08-2018.
 */
class SearchBoundaryCallbacks(
        private val query: String,
        private val service: NetworkService,
        private val cache: SearchLocalCache) : PagedList.BoundaryCallback<SearchEntry>() {

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }

    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = 1

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
        Log.d("SearchBoundaryCallback", "onZeroItemsLoaded")
        requestAndSaveData(query)
    }

    override fun onItemAtEndLoaded(itemAtEnd: SearchEntry) {
        Log.d("SearchBoundaryCallback", "onItemAtEndLoaded")
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        getSearchMovies(service,query, lastRequestedPage
                ,{ movierequest ->
            val searchEntryList: MutableList<SearchEntry> = mutableListOf()
            for (i in 0 until movierequest.results!!.size){
                val searchEntry = SearchEntry()
                val movie =  movierequest.results!![i]
                searchEntry.movieId = movie.id
                searchEntry.voteCount = movie.voteCount
                searchEntry.video = movie.video
                searchEntry.voteAverage = movie.voteAverage
                searchEntry.title = movie.title
                searchEntry.popularity = movie.popularity
                searchEntry.posterPath = movie.posterPath
                searchEntry.originalLanguage = movie.originalLanguage
                searchEntry.originalTitle = movie.originalTitle
                searchEntry.genreIds = movie.genreString
                searchEntry.backdropPath = movie.backdropPath
                searchEntry.adult = movie.adult
                searchEntry.overview = movie.overview
                searchEntry.releaseDate = movie.releaseDate
                for (j in 0 until movie.genreIds!!.size) {
                    if(j==movie.genreIds!!.size-1)
                        movie.genreString += Constants.getGenre(movie.genreIds!!.get(j))
                    else
                        movie.genreString += Constants.getGenre(movie.genreIds!!.get(j))+", "
                }
                searchEntry.genreString = movie.genreString
                searchEntry.contentType = Constants.CONTENT_SIMILAR
                searchEntry.timeAdded = Date().time

                if (searchEntry.backdropPath.isNullOrEmpty()) searchEntry.backdropPath = Constants.RANDOM_PATH
                if (searchEntry.posterPath.isNullOrEmpty()) searchEntry.posterPath = Constants.RANDOM_PATH
                searchEntryList.add(searchEntry)
            }
            cache.insert(searchEntryList,{
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