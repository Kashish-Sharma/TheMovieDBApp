package kashish.com.boundaryCallbacks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import android.util.Log
import kashish.com.database.Entities.PopularEntry
import kashish.com.database.LocalCache.PopularLocalCache
import kashish.com.network.NetworkService
import kashish.com.network.getPopularMovies
import kashish.com.utils.Constants
import java.util.*

/**
 * Created by Kashish on 15-08-2018.
 */
class PopularBoundaryCallbacks(
        private val region: String,
        private val service: NetworkService,
        private val cache: PopularLocalCache) : PagedList.BoundaryCallback<PopularEntry>() {

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }

    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = (cache.getAllItemsInPopular()/20) + 1

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
        Log.d("NowshowingCallback", "onZeroItemsLoaded")
        requestAndSavePopularData(region)
    }

    override fun onItemAtEndLoaded(itemAtEnd: PopularEntry) {
        Log.d("NowshowingCallback", "onItemAtEndLoaded")
        requestAndSavePopularData(region)
    }

    private fun requestAndSavePopularData(region: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true

        getPopularMovies(service,"en-US",
                lastRequestedPage,
                region,
                { movierequest ->
                    val popularEntryList: MutableList<PopularEntry> = mutableListOf()
                    for (i in 0 until movierequest.results!!.size){
                        val popularEntry = PopularEntry()
                        val movie =  movierequest.results!![i]
                        popularEntry.movieId = movie.id
                        popularEntry.voteCount = movie.voteCount
                        popularEntry.video = movie.video
                        popularEntry.voteAverage = movie.voteAverage
                        popularEntry.title = movie.title
                        popularEntry.popularity = movie.popularity
                        popularEntry.posterPath = movie.posterPath
                        popularEntry.originalLanguage = movie.originalLanguage
                        popularEntry.originalTitle = movie.originalTitle
                        popularEntry.genreIds = movie.genreString
                        popularEntry.backdropPath = movie.backdropPath
                        popularEntry.adult = movie.adult
                        popularEntry.overview = movie.overview
                        popularEntry.releaseDate = movie.releaseDate
                        for (j in 0 until movie.genreIds!!.size) {
                            if(j==movie.genreIds!!.size-1)
                                movie.genreString += Constants.getGenre(movie.genreIds!!.get(j))
                            else
                                movie.genreString += Constants.getGenre(movie.genreIds!!.get(j))+", "
                        }
                        popularEntry.genreString = movie.genreString
                        popularEntry.contentType = Constants.CONTENT_SIMILAR
                        popularEntry.timeAdded = Date().time

                        if (popularEntry.backdropPath.isNullOrEmpty()) popularEntry.backdropPath = Constants.RANDOM_PATH
                        if (popularEntry.posterPath.isNullOrEmpty()) popularEntry.posterPath = Constants.RANDOM_PATH
                        popularEntryList.add(popularEntry)
                    }
                    cache.insert(popularEntryList,{
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