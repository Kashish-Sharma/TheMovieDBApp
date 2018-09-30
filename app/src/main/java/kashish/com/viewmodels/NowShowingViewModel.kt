package kashish.com.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import kashish.com.repositories.NowShowingRepository
import kashish.com.database.DatabaseResults.NowShowingResults
import kashish.com.database.Entities.NowShowingEntry

/**
 * Created by Kashish on 14-08-2018.
 */
class NowShowingViewModel(private val repository: NowShowingRepository ) : ViewModel() {

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    private val queryLiveData = MutableLiveData<String>()
    private val nowShowingResult: LiveData<NowShowingResults> = Transformations.map(queryLiveData, {
        repository.nowShowing(it)
    })

    val nowshowing: LiveData<PagedList<NowShowingEntry>> = Transformations.switchMap(nowShowingResult,
            { it -> it.data })
    val networkErrors: LiveData<String> = Transformations.switchMap(nowShowingResult,
            { it -> it.networkErrors })

    fun getNowShowing(region: String) {
        queryLiveData.value = region
    }

}