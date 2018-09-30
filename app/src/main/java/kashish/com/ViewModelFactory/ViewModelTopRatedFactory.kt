package kashish.com.ViewModelFactory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import kashish.com.repositories.TopRatedRepository
import kashish.com.viewmodels.TopRatedViewModel

/**
 * Created by Kashish on 14-08-2018.
 */
class ViewModelTopRatedFactory(private val repository: TopRatedRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TopRatedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TopRatedViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}