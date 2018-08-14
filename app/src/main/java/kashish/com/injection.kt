package kashish.com

import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import kashish.com.ViewModelFactory.ViewModelNowShowingFactory
import kashish.com.ViewModelFactory.ViewModelPopularFactory
import kashish.com.ViewModelFactory.ViewModelSearchFactory
import kashish.com.ViewModelFactory.ViewModelUpcomingFactory
import kashish.com.data.NowShowingRepository
import kashish.com.data.PopularRepository
import kashish.com.data.SearchRepository
import kashish.com.data.UpcomingRepository
import kashish.com.database.AppDatabase
import kashish.com.database.LocalCache.NowShowingLocalCache
import kashish.com.database.LocalCache.PopularLocalCache
import kashish.com.database.LocalCache.SearchLocalCache
import kashish.com.database.LocalCache.UpcomingLocalCache
import kashish.com.network.NetworkService
import java.util.concurrent.Executors

/**
 * Created by Kashish on 13-08-2018.
 */
object Injection {

    //Search
    private fun provideSearchCache(context: Context): SearchLocalCache {
        val database = AppDatabase.getInstance(context)
        return SearchLocalCache(database.searchDao(), Executors.newSingleThreadExecutor())
    }
    private fun provideSearchRepository(context: Context): SearchRepository {
        return SearchRepository(NetworkService.instance, provideSearchCache(context))
    }
    fun provideSearchViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelSearchFactory(provideSearchRepository(context))
    }

    //NowShowing
    private fun provideNowShowingCache(context: Context): NowShowingLocalCache {
        val database = AppDatabase.getInstance(context)
        return NowShowingLocalCache(database.nowShowingDao(), Executors.newSingleThreadExecutor())
    }
    private fun provideNowShowingRepository(context: Context): NowShowingRepository {
        return NowShowingRepository(NetworkService.instance, provideNowShowingCache(context))
    }
    fun provideNowShowingViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelNowShowingFactory(provideNowShowingRepository(context))
    }

    //Upcoming
    private fun provideUpcomingCache(context: Context): UpcomingLocalCache {
        val database = AppDatabase.getInstance(context)
        return UpcomingLocalCache(database.upcomingDao(), Executors.newSingleThreadExecutor())
    }
    private fun provideUpcomingRepository(context: Context): UpcomingRepository {
        return UpcomingRepository(NetworkService.instance, provideUpcomingCache(context))
    }
    fun provideUpcomingViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelUpcomingFactory(provideUpcomingRepository(context))
    }

    //Popular
    private fun providePopularCache(context: Context): PopularLocalCache {
        val database = AppDatabase.getInstance(context)
        return PopularLocalCache(database.poplarDao(), Executors.newSingleThreadExecutor())
    }
    private fun providePopularRepository(context: Context): PopularRepository {
        return PopularRepository(NetworkService.instance, providePopularCache(context))
    }
    fun providePopularViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelPopularFactory(providePopularRepository(context))
    }

}