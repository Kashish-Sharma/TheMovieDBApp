package kashish.com.boundaryCallbacks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import android.util.Log
import kashish.com.database.Entities.NowShowingEntry
import kashish.com.database.LocalCache.NowShowingLocalCache
import kashish.com.network.NetworkService
import kashish.com.network.getNowShowingMovies
import kashish.com.utils.Constants
import java.util.*

/**
 * Created by Kashish on 15-08-2018.
 */
class NowShowingBoundaryCallbacks(
        private val region: String,
        private val service: NetworkService,
        private val cache: NowShowingLocalCache) : PagedList.BoundaryCallback<NowShowingEntry>() {

    // keep the last requested page. When the request is successful, increment the page number.

    var lastRequestedPage: Int = (cache.getAllItemsInNowShowing()/20) + 1

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
        requestAndSaveNowShowingData(region)
    }

    override fun onItemAtEndLoaded(itemAtEnd: NowShowingEntry) {
        Log.d("NowshowingCallback", "onItemAtEndLoaded")
        requestAndSaveNowShowingData(region)
    }

    private fun requestAndSaveNowShowingData(region: String) {

        if (isRequestInProgress) return


        isRequestInProgress = true

        getNowShowingMovies(service,"en-US",
                lastRequestedPage,
                region,
                { movierequest ->
                    val nowShowingEntryList: MutableList<NowShowingEntry> = mutableListOf()
                    for (i in 0 until movierequest.results!!.size){
                        val nowShowingEntry = NowShowingEntry()
                        val movie =  movierequest.results!![i]
                        nowShowingEntry.movieId = movie.id
                        nowShowingEntry.voteCount = movie.voteCount
                        nowShowingEntry.video = movie.video
                        nowShowingEntry.voteAverage = movie.voteAverage
                        nowShowingEntry.title = movie.title
                        nowShowingEntry.popularity = movie.popularity
                        nowShowingEntry.posterPath = movie.posterPath
                        nowShowingEntry.originalLanguage = movie.originalLanguage
                        nowShowingEntry.originalTitle = movie.originalTitle
                        nowShowingEntry.genreIds = movie.genreString
                        nowShowingEntry.backdropPath = movie.backdropPath
                        nowShowingEntry.adult = movie.adult
                        nowShowingEntry.overview = movie.overview
                        nowShowingEntry.releaseDate = movie.releaseDate
                        for (j in 0 until movie.genreIds!!.size) {
                            if(j==movie.genreIds!!.size-1)
                                movie.genreString += Constants.getGenre(movie.genreIds!!.get(j))
                            else
                                movie.genreString += Constants.getGenre(movie.genreIds!!.get(j))+", "
                        }
                        nowShowingEntry.genreString = movie.genreString
                        nowShowingEntry.contentType = Constants.CONTENT_SIMILAR
                        nowShowingEntry.timeAdded = Date().time

                        if (nowShowingEntry.backdropPath.isNullOrEmpty()) nowShowingEntry.backdropPath = Constants.RANDOM_PATH
                        if (nowShowingEntry.posterPath.isNullOrEmpty()) nowShowingEntry.posterPath = Constants.RANDOM_PATH
                        nowShowingEntryList.add(nowShowingEntry)
                    }
                    cache.insert(nowShowingEntryList,{
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