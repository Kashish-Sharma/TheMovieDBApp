package kashish.com.database.DatabaseResults

import android.arch.lifecycle.LiveData
import kashish.com.database.Entities.TopRatedEntry

/**
 * Created by Kashish on 14-08-2018.
 */
data class TopRatedResults(
        val data: LiveData<List<TopRatedEntry>>,
        val networkErrors: LiveData<String>
)