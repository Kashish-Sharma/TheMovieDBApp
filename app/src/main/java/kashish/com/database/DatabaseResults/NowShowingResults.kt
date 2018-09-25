package kashish.com.database.DatabaseResults

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import kashish.com.database.Entities.NowShowingEntry

/**
 * Created by Kashish on 14-08-2018.
 */
data class NowShowingResults(
        val data: LiveData<PagedList<NowShowingEntry>>,
        val networkErrors: LiveData<String>
)