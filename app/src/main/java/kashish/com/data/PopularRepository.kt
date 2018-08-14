package kashish.com.data

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import kashish.com.database.DatabaseResults.NowShowingResults
import kashish.com.database.DatabaseResults.PopularResults
import kashish.com.database.Entities.NowShowingEntry
import kashish.com.database.Entities.PopularEntry
import kashish.com.database.LocalCache.NowShowingLocalCache
import kashish.com.database.LocalCache.PopularLocalCache
import kashish.com.network.NetworkService
import kashish.com.network.getNowShowingMovies
import kashish.com.network.getPopularMovies
import kashish.com.utils.Constants
import java.util.*

/**
 * Created by Kashish on 14-08-2018.
 */
class PopularRepository(
        private val service: NetworkService,
        private val popularCache: PopularLocalCache
) {
    // keep the last requested page. When the request is successful, increment the page number.
    private var lastPopularRequestedPage = 1
    // LiveData of network errors.
    private val networkPopularErrors = MutableLiveData<String>()
    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false
    fun popular(doReload: Boolean): PopularResults {
        lastPopularRequestedPage = 1
        requestMorePopular(doReload)
        // Get data from the local cache
        val data = popularCache.getAllPopular()
        return PopularResults(data, networkPopularErrors)
    }
    fun requestMorePopular(doReload: Boolean) {
        requestAndSavePopularData(doReload)
    }
    private fun requestAndSavePopularData(doReload: Boolean) {
        if (isRequestInProgress) return

        if (doReload){
            lastPopularRequestedPage = 1
        }


        isRequestInProgress = true

        getPopularMovies(service,"en-US",
                lastPopularRequestedPage,
                "US|IN|UK",
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
                        popularEntry.timeAdded = Date()

                        if (popularEntry.backdropPath.isNullOrEmpty()) popularEntry.backdropPath = Constants.RANDOM_PATH
                        if (popularEntry.posterPath.isNullOrEmpty()) popularEntry.posterPath = Constants.RANDOM_PATH
                        popularEntryList.add(popularEntry)
                    }
                    popularCache.insert(popularEntryList,{
                        lastPopularRequestedPage++
                        isRequestInProgress = false
                    })
                }, {
            error ->
            networkPopularErrors.postValue(error)
            isRequestInProgress = false
        })

    }

}