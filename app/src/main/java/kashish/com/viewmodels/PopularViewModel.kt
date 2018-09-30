package kashish.com.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import kashish.com.repositories.PopularRepository
import kashish.com.database.DatabaseResults.PopularResults
import kashish.com.database.Entities.PopularEntry

/**
 * Created by Kashish on 14-08-2018.
 */
class PopularViewModel(private val repository: PopularRepository) : ViewModel() {

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    private val queryLiveData = MutableLiveData<String>()
    private val popularResult: LiveData<PopularResults> = Transformations.map(queryLiveData, {
        repository.popular(it)
    })

    val nowshowing: LiveData<PagedList<PopularEntry>> = Transformations.switchMap(popularResult,
            { it -> it.data })
    val networkErrors: LiveData<String> = Transformations.switchMap(popularResult,
            { it -> it.networkErrors })

    fun getPopular(region: String) {
        queryLiveData.value = region
    }

}