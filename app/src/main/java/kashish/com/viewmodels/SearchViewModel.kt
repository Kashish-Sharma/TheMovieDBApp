package kashish.com.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.util.Log
import kashish.com.data.TmdbRepository
import kashish.com.database.DatabaseResults.SearchResults
import kashish.com.database.Entities.SearchEntry

/**
 * Created by Kashish on 13-08-2018.
 */
class SearchViewModel(private val repository: TmdbRepository) : ViewModel() {

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    private val queryLiveData = MutableLiveData<String>()
    private val searchResult: LiveData<SearchResults> = Transformations.map(queryLiveData, {
        repository.search(it)
    })

    val searches: LiveData<List<SearchEntry>> = Transformations.switchMap(searchResult,
            { it -> it.data })
    val networkErrors: LiveData<String> = Transformations.switchMap(searchResult,
            { it -> it.networkErrors })

    /**
     * Search a repository based on a query string.
     */
    fun searchRepo(queryString: String) {
        Log.i("SearchInfo", queryString + " is the searchRepo")

        queryLiveData.postValue(queryString)
    }

    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int) {
        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            val immutableQuery = lastQueryValue()
            if (immutableQuery != null) {
                repository.requestMoreSearches(immutableQuery)
            }
        }
    }

    /**
     * Get the last query value.
     */
    fun lastQueryValue(): String? = queryLiveData.value
}