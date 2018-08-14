package kashish.com.data

import android.arch.lifecycle.MutableLiveData
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
    // keep the last requested page. When the request is successful, increment the page number.
    private var lastTopRatedRequestedPage = 1
    // LiveData of network errors.
    private val networkTopRatedErrors = MutableLiveData<String>()
    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false
    fun topRated(doReload: Boolean): TopRatedResults {
        lastTopRatedRequestedPage = 1
        requestMoreTopRated(doReload)
        // Get data from the local cache
        val data = topRatedCache.getAllTopRated()
        return TopRatedResults(data, networkTopRatedErrors)
    }
    fun requestMoreTopRated(doReload: Boolean) {
        requestAndSaveTopRatedData(doReload)
    }
    private fun requestAndSaveTopRatedData(doReload: Boolean) {
        if (isRequestInProgress) return

        if (doReload){
            lastTopRatedRequestedPage = 1
        }


        isRequestInProgress = true

        getTopRatedMovies(service,"en-US",
                lastTopRatedRequestedPage,
                "US|IN|UK",
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
                        topRatedEntry.timeAdded = Date()

                        if (topRatedEntry.backdropPath.isNullOrEmpty()) topRatedEntry.backdropPath = Constants.RANDOM_PATH
                        if (topRatedEntry.posterPath.isNullOrEmpty()) topRatedEntry.posterPath = Constants.RANDOM_PATH
                        topRatedEntryList.add(topRatedEntry)
                    }
                    topRatedCache.insert(topRatedEntryList,{
                        lastTopRatedRequestedPage++
                        isRequestInProgress = false
                    })
                }, {
            error ->
            networkTopRatedErrors.postValue(error)
            isRequestInProgress = false
        })

    }

}