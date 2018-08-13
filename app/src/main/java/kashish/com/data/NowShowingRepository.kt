package kashish.com.data

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import kashish.com.database.DatabaseResults.NowShowingResults
import kashish.com.database.Entities.NowShowingEntry
import kashish.com.database.LocalCache.NowShowingLocalCache
import kashish.com.network.NetworkService
import kashish.com.network.getNowShowingMovies
import kashish.com.utils.Constants
import java.util.*

/**
 * Created by Kashish on 14-08-2018.
 */
class NowShowingRepository(
        private val service: NetworkService,
        private val nowShowingCache: NowShowingLocalCache
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
    fun nowShowing(): NowShowingResults {
        lastRequestedPage = 1
        requestMoreNowShowing()
        // Get data from the local cache
        val data = nowShowingCache.getAllNowShowing()
        return NowShowingResults(data, networkErrors)
    }

    fun requestMoreNowShowing() {
        requestAndSaveNowShowingData()
    }
    private fun requestAndSaveNowShowingData() {
        if (isRequestInProgress) return

        isRequestInProgress = true

        getNowShowingMovies(service,"en-US",
        lastRequestedPage,
        "US|IN|UK",
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
                nowShowingEntry.timeAdded = Date()

                if (nowShowingEntry.backdropPath.isNullOrEmpty()) nowShowingEntry.backdropPath = Constants.RANDOM_PATH
                if (nowShowingEntry.posterPath.isNullOrEmpty()) nowShowingEntry.posterPath = Constants.RANDOM_PATH
                nowShowingEntryList.add(nowShowingEntry)
            }
            nowShowingCache.insert(nowShowingEntryList,{
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