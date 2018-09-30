package kashish.com.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import kashish.com.repositories.TopRatedRepository
import kashish.com.database.DatabaseResults.TopRatedResults
import kashish.com.database.Entities.TopRatedEntry

/**
 * Created by Kashish on 14-08-2018.
 */
class TopRatedViewModel(private val repository: TopRatedRepository ) : ViewModel() {

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    private val queryLiveData = MutableLiveData<String>()
    private val nowShowingResult: LiveData<TopRatedResults> = Transformations.map(queryLiveData, {
        repository.topRated(it)
    })

    val topRated: LiveData<PagedList<TopRatedEntry>> = Transformations.switchMap(nowShowingResult,
            { it -> it.data })
    val networkErrors: LiveData<String> = Transformations.switchMap(nowShowingResult,
            { it -> it.networkErrors })

    fun getTopRated(region: String) {
        queryLiveData.value = region
    }

}