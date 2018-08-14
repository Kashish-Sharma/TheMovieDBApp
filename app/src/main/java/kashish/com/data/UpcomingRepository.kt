package kashish.com.data

import android.arch.lifecycle.MutableLiveData
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

    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = 1

    // LiveData of network errors.
    private val networkErrors = MutableLiveData<String>()

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    /**
     * Search repositories whose names match the query.
     */
    fun upcoming(doReload: Boolean): UpcomingResults {
        lastRequestedPage = 1
        requestMoreUpcoming(doReload)
        // Get data from the local cache
        val data = upcomingCache.getAllUpcoming()
        return UpcomingResults(data, networkErrors)
    }

    fun requestMoreUpcoming(doReload: Boolean) {
        requestAndSaveUpcomingData(doReload)
    }
    private fun requestAndSaveUpcomingData(doReload: Boolean) {
        if (isRequestInProgress) return

        if (doReload){
            lastRequestedPage = 1
        }

        isRequestInProgress = true

        getUpcomingMovies(service,"en-US",
                lastRequestedPage,
                "US|IN|UK",
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
                        upcomingEntry.timeAdded = Date()

                        if (upcomingEntry.backdropPath.isNullOrEmpty()) upcomingEntry.backdropPath = Constants.RANDOM_PATH
                        if (upcomingEntry.posterPath.isNullOrEmpty()) upcomingEntry.posterPath = Constants.RANDOM_PATH
                        upcomingEntryList.add(upcomingEntry)
                    }
                    upcomingCache.insert(upcomingEntryList,{
                        lastRequestedPage++
                        isRequestInProgress = false
                    })
                }, {
            error ->
            networkErrors.postValue(error)
            isRequestInProgress = false
        })

    }

}