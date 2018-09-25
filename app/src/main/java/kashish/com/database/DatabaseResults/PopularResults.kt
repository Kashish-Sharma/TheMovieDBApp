package kashish.com.database.DatabaseResults

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import kashish.com.database.Entities.PopularEntry

/**
 * Created by Kashish on 14-08-2018.
 */
data class PopularResults(
        val data: LiveData<PagedList<PopularEntry>>,
        val networkErrors: LiveData<String>
)