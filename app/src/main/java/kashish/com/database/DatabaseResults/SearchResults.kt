package kashish.com.database.DatabaseResults

import android.arch.lifecycle.LiveData
import kashish.com.database.Entities.SearchEntry

/**
 * Created by Kashish on 13-08-2018.
 */
data class SearchResults(
        val data: LiveData<List<SearchEntry>>,
        val networkErrors: LiveData<String>
)