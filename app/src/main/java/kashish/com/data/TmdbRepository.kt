package kashish.com.data

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import kashish.com.database.DatabaseResults.SearchResults
import kashish.com.database.Entities.SearchEntry
import kashish.com.database.LocalCache.SearchLocalCache
import kashish.com.models.Movie
import kashish.com.network.NetworkService
import kashish.com.network.getSearchMovies
import kashish.com.utils.Constants.Companion.CONTENT_SIMILAR
import kashish.com.utils.Constants.Companion.RANDOM_PATH
import kashish.com.utils.Urls.Companion.TMDB_API_KEY
import java.util.*

/**
 * Created by Kashish on 13-08-2018.
 */
class TmdbRepository(
        private val service: NetworkService,
        private val searchCache: SearchLocalCache
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
    fun search(query: String): SearchResults {
        Log.d("GithubRepository", "New query: $query")
        lastRequestedPage = 1
        requestAndSaveSearchData(query)
        // Get data from the local cache
        val data = searchCache.searchesByName(query)
        return SearchResults(data, networkErrors)
    }
    fun requestMoreSearches(query: String) {
        requestAndSaveSearchData(query)
    }
    private fun requestAndSaveSearchData(query: String) {
        if (isRequestInProgress) return
        Log.i("SearchInfo", query + " is the TmdbRepo")

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
                searchEntry.genreString = movie.genreString
                searchEntry.contentType = CONTENT_SIMILAR
                searchEntry.timeAdded = Date()

                if (searchEntry.backdropPath.isNullOrEmpty()) searchEntry.backdropPath = RANDOM_PATH
                if (searchEntry.posterPath.isNullOrEmpty()) searchEntry.posterPath = RANDOM_PATH
                searchEntryList.add(searchEntry)
            }
            searchCache.insert(searchEntryList,{
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