package kashish.com

import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import kashish.com.ViewModelFactory.ViewModelSearchFactory
import kashish.com.data.TmdbRepository
import kashish.com.database.AppDatabase
import kashish.com.database.LocalCache.SearchLocalCache
import kashish.com.network.NetworkService
import java.util.concurrent.Executors

/**
 * Created by Kashish on 13-08-2018.
 */
object Injection {

    private fun provideCache(context: Context): SearchLocalCache {
        val database = AppDatabase.getInstance(context)
        return SearchLocalCache(database.searchDao(), Executors.newSingleThreadExecutor())
    }

    private fun provideRepository(context: Context): TmdbRepository {
        return TmdbRepository(NetworkService.instance, provideCache(context))
    }

    fun provideSearchViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelSearchFactory(provideRepository(context))
    }

}