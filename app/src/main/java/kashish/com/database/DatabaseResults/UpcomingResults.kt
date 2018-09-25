package kashish.com.database.DatabaseResults

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import kashish.com.database.Entities.UpcomingEntry

/**
 * Created by Kashish on 14-08-2018.
 */
data class UpcomingResults(
        val data: LiveData<PagedList<UpcomingEntry>>,
        val networkErrors: LiveData<String>
)