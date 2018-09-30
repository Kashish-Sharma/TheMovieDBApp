package kashish.com.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import kashish.com.repositories.UpcomingRepository
import kashish.com.database.DatabaseResults.UpcomingResults
import kashish.com.database.Entities.UpcomingEntry

/**
 * Created by Kashish on 14-08-2018.
 */
class UpcomingViewModel(private val repository: UpcomingRepository) : ViewModel() {

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    private val queryLiveData = MutableLiveData<String>()
    private val upcomingResult: LiveData<UpcomingResults> = Transformations.map(queryLiveData, {
        repository.upcoming(it)
    })

    val upcoming: LiveData<PagedList<UpcomingEntry>> = Transformations.switchMap(upcomingResult,
            { it -> it.data })
    val networkErrors: LiveData<String> = Transformations.switchMap(upcomingResult,
            { it -> it.networkErrors })

    fun getUpcoming(region: String) {
        queryLiveData.value = region
    }

}