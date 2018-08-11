package kashish.com.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.util.Log
import kashish.com.database.AppDatabase
import kashish.com.database.MovieEntry

/**
 * Created by Kashish on 11-08-2018.
 */
class FavouritesViewModel(application: Application) : AndroidViewModel(application) {

    private var movies: LiveData<MutableList<MovieEntry>>
    private val FavouritesViewModelTAG:String = "FavouritesViewModelTAG"

    init {
        val database: AppDatabase = AppDatabase.getInstance(this.getApplication())
        Log.d(FavouritesViewModelTAG,"Actively retrieving movies from database")
        movies = database.movieDao().loadAllFavourites()
    }

    fun getMovies(): LiveData<MutableList<MovieEntry>>{
        return movies
    }



}