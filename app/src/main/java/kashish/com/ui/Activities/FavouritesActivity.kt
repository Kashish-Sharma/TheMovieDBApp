package kashish.com.ui.Activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import kashish.com.R
import kashish.com.adapters.MovieAdapter
import kashish.com.database.Entities.FavouritesEntry
import kashish.com.interfaces.OnMovieClickListener
import kashish.com.models.Movie
import kashish.com.utils.Constants.Companion.CONTENT_MOVIE
import kashish.com.viewmodels.FavouritesViewModel

class FavouritesActivity : AppCompatActivity(), OnMovieClickListener, SharedPreferences.OnSharedPreferenceChangeListener  {

    private val GRID_COLUMNS_PORTRAIT = 1
    private val GRID_COLUMNS_LANDSCAPE = 2
    private val TAG: String = SimilarMoviesActivity::class.simpleName.toString()

    lateinit var mFavouriteAdapter: MovieAdapter
    var favouriteData: MutableList<Movie> = mutableListOf()
    private lateinit var mFavouriteRecyclerView : RecyclerView
    private lateinit var mGridLayoutManager: GridLayoutManager

    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mFavouriteProgress: ProgressBar

    //Toolbar
    private lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (mSharedPreferences.getBoolean(getString(R.string.pref_night_mode_key)
                ,resources.getBoolean(R.bool.pref_night_mode_default_value))) {
            setTheme(R.style.AppThemeDark)
        } else{
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        initViews()
        setToolbar()
        initContentList()
        initFavouriteRecyclerView()
        fetchFavouriteMovie()
    }

    private fun initFavouriteRecyclerView(){
        configureRecyclerAdapter(resources.configuration.orientation)
        mFavouriteAdapter = MovieAdapter(favouriteData,this,mSharedPreferences)
        mFavouriteRecyclerView.setAdapter(mFavouriteAdapter)
    }

    private fun initViews(){
        mToolbar = findViewById(R.id.activity_favourites_toolbar)
        mFavouriteRecyclerView = findViewById(R.id.activity_favourites_movies_recycler_view)
        mFavouriteProgress = findViewById(R.id.activity_favourites_progress)
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    private fun fetchFavouriteMovie(){
        val favouriteviewModel: FavouritesViewModel =
                ViewModelProviders.of(this).get(FavouritesViewModel::class.java)
        favouriteviewModel.getMovies().observe(this, object : Observer<MutableList<FavouritesEntry>>{
            override fun onChanged(t: MutableList<FavouritesEntry>?) {
                clearList()
                    for (i in 0 until t!!.size){
                        val movie = Movie()
                        val movieEntry = t[i]
                        movie.id = movieEntry.movieId
                        movie.voteCount = movieEntry.voteCount
                        movie.video = movieEntry.video
                        movie.voteAverage = movieEntry.voteAverage
                        movie.title = movieEntry.title
                        movie.popularity = movieEntry.popularity
                        movie.posterPath = movieEntry.posterPath!!
                        movie.originalLanguage = movieEntry.originalLanguage
                        movie.originalTitle = movieEntry.originalTitle
                        movie.backdropPath = movieEntry.backdropPath!!
                        movie.adult = movieEntry.adult
                        movie.overview = movieEntry.overview
                        movie.releaseDate = movieEntry.releaseDate
                        movie.genreString = movieEntry.genreString!!
                        movie.contentType = CONTENT_MOVIE

                        favouriteData.add(movie)
                    }
                    mFavouriteAdapter.notifyDataSetChanged()
                    mFavouriteProgress.visibility = View.GONE

            }
        })
        Log.d("FavouritesViewModelTAG","Retreiving updates from livedata")
    }


    private fun initContentList(){
        favouriteData = mutableListOf()
    }


    private fun clearList() {
        val size = favouriteData.size
        favouriteData.clear()
        mFavouriteAdapter.notifyItemRangeRemoved(0, size)
    }

    private fun setToolbar(){
        mToolbar.title = "Favourites"
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.getItemId()
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        configureRecyclerAdapter(newConfig!!.orientation)
    }

    private fun configureRecyclerAdapter(orientation: Int) {
        val isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT
        mGridLayoutManager = GridLayoutManager(this, if (isPortrait) GRID_COLUMNS_PORTRAIT else GRID_COLUMNS_LANDSCAPE)
        mFavouriteRecyclerView.setLayoutManager(mGridLayoutManager)
    }


    override fun onMovieClickListener(movie: Movie) {
        val detailIntent = Intent(this, DetailActivity::class.java)
        detailIntent.putExtra("movie",movie)
        startActivity(detailIntent)
    }

    private fun restartActivity(){
        this.recreate()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, key: String?) {
        if(key.equals(getString(R.string.pref_night_mode_key)))
            restartActivity()
        }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

}

